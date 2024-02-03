(ns in.arthya.hledger.core-test
  (:require [clojure.test :refer [deftest is]]
            [in.arthya.hledger.core :as hledger]))

(deftest hledger-transaction
  (is (= (hledger/->transaction
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Expense:Food"
           :amount "-10"})
         {:date "2021/12/16",
          :payee "Fruit Vendor",
          :tags ["T:A"],
          :comment "Some memo",
          :postings [{:amount "-10", :account "Expense:Food"}]})))

