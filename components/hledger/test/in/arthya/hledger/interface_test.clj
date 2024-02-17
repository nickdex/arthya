(ns in.arthya.hledger.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.hledger.interface :as hledger]))

(deftest hledger-transaction-test
  (is (= {:comment "Vada pav",
          :date "2021/12/16",
          :payee "Fruit Vendor",
          :postings [{:account "Expense:Food", :amount "-10", :currency "INR"}],
          :tags ["T:A"]}
         (hledger/->hledger-transaction
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :memo "Vada pav"
           :tags ["T:A"]
           :account "Expense:Food"
           :currency "INR"
           :amount "-10"}))))

(deftest hledger-entry-test
  (is (= "2021/12/16 Fruit Vendor ; T:A\n    ; Vada pav\n    Expense:Food  -10 INR"
         (hledger/->hledger-entry
          {:date "2021/12/16", :payee "Fruit Vendor", :tags ["T:A"], :comment "Vada pav", :postings [{:amount "-10", :currency "INR", :account "Expense:Food"}]} ))))
