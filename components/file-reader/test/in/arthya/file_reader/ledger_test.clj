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

(deftest entry-test
  (is (= {:comment ["446274839060" "SWIGGY"],
          :date "2024/01/02",
          :payee "Swiggy",
          :postings [{:account "Assets:Checking:Sodexo-6102",
                      :amount "-242.0",
                      :currency "INR"}
                     {:account "Expenses:Food"}],
          :tags nil}
         (ledger/->entry
          ["2024/01/02 Swiggy" "; 446274839060" "; SWIGGY" "Assets:Checking:Sodexo-6102  -242.0 INR" "Expenses:Food"]))))
