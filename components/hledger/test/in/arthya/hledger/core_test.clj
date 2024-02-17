(ns in.arthya.hledger.core-test
  (:require [clojure.test :refer [deftest is]]
            [in.arthya.hledger.core :as hledger]))

(deftest hledger-transaction-test
  (is (= {:date "2021/12/16",
          :payee "Fruit Vendor",
          :tags ["T:A"],
          :comment "Some memo",
          :postings [{:amount "-10", :account "Expense:Food"}]}
         (hledger/->transaction
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Expense:Food"
           :amount "-10"})))
  (is (= {:date "2021/12/16",
          :payee "NSE",
          :tags ["T:A"],
          :comment "Some memo",
          :postings [{:units 100, :unit-price 20.5, :commodity "NIFTY", :account "Assets:Demat"}]}
         (hledger/->transaction
          {:date "2021/12/16"
           :payee "NSE"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Assets:Demat"
           :units 100
           :commodity "NIFTY"
           :unit-price 20.5}))))

(deftest comment->str-test
  (is (= "\n    ; 446274839060\n    ; SWIGGY"
         (hledger/comment->str
          "446274839060\nSWIGGY"))))

(deftest transaction->str-test
  (is (= "2024/01/02 Swiggy\n    ; 446274839060\n    ; SWIGGY\n    Assets:Checking:Sodexo-6102  -242.0 INR\n    Expenses:Food"
         (hledger/transaction->str
          {:comment "446274839060\nSWIGGY",
           :date "2024/01/02",
           :payee "Swiggy",
           :postings [{:account "Assets:Checking:Sodexo-6102",
                       :amount "-242.0",
                       :currency "INR"}
                      {:account "Expenses:Food"}],
           :tags nil}))))

(deftest hledger-entry-test
  (is (= "2021/12/16 Fruit Vendor ; T:A\n    ; Some memo\n    Expense:Food  -10 INR"
         (hledger/->hledger-entry
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Expense:Food"
           :currency "INR"
           :amount "-10"})))

  (is (= "2021/12/16 NSE ; T:A\n    ; Some memo\n    Assets:Demat  100 NIFTY @ 20.5 INR"
         (hledger/->hledger-entry
          {:date "2021/12/16"
           :payee "NSE"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Assets:Demat"
           :currency "INR"
           :units 100
           :commodity "NIFTY"
           :unit-price 20.5})))

  (is (= "2021/12/16 Fruit Vendor ; T:A\n    ; Some memo\n    ; second line\n    Expense:Food  -10 INR"
         (hledger/->hledger-entry
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :tags ["T:A"]
           :memo "Some memo\nsecond line"
           :account "Expense:Food"
           :currency "INR"
           :amount "-10"}))))
