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
          :postings [{:amount "-10", :account "Expense:Food"}]}))
  (is (= (hledger/->transaction
          {:date "2021/12/16"
           :payee "NSE"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Assets:Demat"
           :units 100
           :commodity "NIFTY"
           :unit-price 20.5})
         {:date "2021/12/16",
          :payee "NSE",
          :tags ["T:A"],
          :comment "Some memo",
          :postings [{:units 100, :unit-price 20.5, :commodity "NIFTY", :account "Assets:Demat"}]})))

(deftest hledger-entry
  (is (= (hledger/->hledger-record
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Expense:Food"
           :amount "-10"})
         "2021/12/16 Fruit Vendor ; T:A\n    ; Some memo\n    Expense:Food  -10 INR")))

(deftest hledger-commodity-entry
  (is (= (hledger/->hledger-record
          {:date "2021/12/16"
           :payee "NSE"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Assets:Demat"
           :units 100
           :commodity "NIFTY"
           :unit-price 20.5})
         "2021/12/16 NSE ; T:A\n    ; Some memo\n    Assets:Demat  100 NIFTY @ 20.5 INR")))
