(ns in.arthya.hledger.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.hledger.interface :as hledger]))

(deftest hledger-transaction-test
  (is (= {:comment ["Vada pav"]
          :date "2021/12/16",
          :payee "Fruit Vendor",
          :postings [{:account "Expense:Food", :units "-10", :commodity "INR"}],
          :tags ["T:A"]}
         (hledger/->hledger-transaction
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :memo "Vada pav"
           :tags ["T:A"]
           :account "Expense:Food"
           :commodity "INR"
           :amount "-10"}))))

(deftest hledger-entry-test
  (is (= "2021/12/16 Fruit Vendor ; T:A\n    ; Vada pav\n    Expense:Food                                 -10 INR"
         (hledger/->hledger-entry
          {:comment ["Vada pav"]
           :date "2021/12/16",
           :payee "Fruit Vendor",
           :postings [{:account "Expense:Food", :units "-10", :commodity "INR"}],
           :tags ["T:A"]}))))
