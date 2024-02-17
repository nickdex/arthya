(ns in.arthya.file-reader.ledger-test
  (:require
   [clojure.test :refer [deftest is]]
   [in.arthya.file-reader.ledger :as ledger]))

(deftest clean-comment-test
  (is (= "2889"
         (ledger/clean-comment
          "; 2889"))))

(deftest posting-test
  (is (= {:account "Assets:Checking:Sodexo-6102", :amount "-239.0", :currency "INR"}
         (ledger/->posting
          ["Assets:Checking:Sodexo-6102  -239.0 INR"])))
  (is (= {:account "Assets:Checking:Sodexo-6102"}
         (ledger/->posting
          ["Assets:Checking:Sodexo-6102  "])))
  (is (= {:account "Assets:Checking:Sodexo-6102",
          :amount "-239.0",
          :comment ["Test"],
          :currency "INR"}
         (ledger/->posting
          ["Assets:Checking:Sodexo-6102  -239.0 INR"
           "; Test"]))))
