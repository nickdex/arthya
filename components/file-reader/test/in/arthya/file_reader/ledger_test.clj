(ns in.arthya.file-reader.ledger-test
  (:require
   [clojure.test :refer [deftest is]]
   [in.arthya.file-reader.ledger :as ledger]))

(deftest clean-comment-test
  (is (= "2889"
         (ledger/clean-comment
           "; 2889"))))
