(ns in.arthya.file-reader.ledger-test
  (:require
   [clojure.test :refer [deftest is]]
   [in.arthya.file-reader.ledger :as ledger]))

(deftest clean-comment-test
  (is (= (ledger/clean-comment
           "; 2889")
         "2889")))
