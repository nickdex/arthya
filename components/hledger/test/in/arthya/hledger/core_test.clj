(ns in.arthya.hledger.core-test
  (:require [clojure.test :refer [deftest is]]
            [in.arthya.hledger.core :as hledger]))

(deftest hledger-transaction-test
  (is (= {:date "2021/12/16",
          :payee "Fruit Vendor",
          :tags ["T:A"],
          :comment ["Some memo"]
          :postings [{:quantity "-10", :account "Expense:Food"}]}
         (hledger/->transaction
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Expense:Food"
           :quantity "-10"})))

  (is (= {:date "2021/12/16",
          :payee "NSE",
          :tags ["T:A"],
          :comment ["Some memo"]
          :postings [{:account "Assets:Demat",
                      :quantity 100,
                      :commodity "NIFTY",
                      :price {:commodity "INR"
                              :quantity 20.5}}]}
         (hledger/->transaction
          {:date "2021/12/16"
           :payee "NSE"
           :tags ["T:A"]
           :memo "Some memo"
           :account "Assets:Demat"
           :quantity 100
           :commodity "NIFTY"
           :price {:commodity "INR"
                   :quantity 20.5}}))))

(deftest comment->str-test
  (is (= "\n    ; 446274839060\n    ; SWIGGY"
         (hledger/comment->str
          ["446274839060" "SWIGGY"]))))

(deftest transaction->str-test
  (is (= "2024/01/02 Swiggy\n    ; 446274839060\n    ; SWIGGY\n    Assets:Checking:Sodexo-6102               -242.0 INR\n    Expenses:Food"
         (hledger/transaction->str
          {:comment ["446274839060" "SWIGGY"]
           :date "2024/01/02",
           :payee "Swiggy",
           :postings [{:account "Assets:Checking:Sodexo-6102",
                       :quantity "-242.0",
                       :commodity "INR"}
                      {:account "Expenses:Food"}],
           :tags nil}))))

(deftest hledger-entry-test
  (is (= "2021/12/16 Fruit Vendor ; T:A\n    ; Some memo\n    Expense:Food                                 -10 INR\n    ; Item: One\n    ; Item: Two"
         (hledger/->hledger-entry
          {:comment ["Some memo"]
           :date "2021/12/16",
           :payee "Fruit Vendor",
           :postings [{:account "Expense:Food",
                       :quantity "-10",
                       :comment ["Item: One" "Item: Two"]
                       :commodity "INR"}],
           :tags ["T:A"]})))

  (is (= "2021/12/16 NSE ; T:A\n    ; Some memo\n    Assets:Demat                                 100 NIFTY @ 20.5 INR"
         (hledger/->hledger-entry
          {:comment ["Some memo"]
           :date "2021/12/16",
           :payee "NSE",
           :postings [{:account "Assets:Demat",
                       :commodity "NIFTY",
                       :price {:commodity "INR"
                               :quantity 20.5}
                       :quantity 100}],
           :tags ["T:A"]})))

  (is (= "2021/12/16 Fruit Vendor ; T:A\n    ; Some memo\n    ; second line\n    Expense:Food                                 -10 INR"
         (hledger/->hledger-entry
          {:comment ["Some memo" "second line"]
           :date "2021/12/16",
           :payee "Fruit Vendor",
           :postings [{:account "Expense:Food", :quantity "-10", :commodity "INR"}],
           :tags ["T:A"]}))))

(deftest space-test
  (is (= " "
         (hledger/space 1)))
  (is (= "    "
         (hledger/space 4)))
  (is (= "  "
         (hledger/space 2))))

(deftest posting-entry-test
  (is (= "    Assets:Checking:Demat                       -200 HIMFUT @ 10.5 INR"
         (hledger/->posting-entry
          {:account "Assets:Checking:Demat"
           :price {:commodity "INR"
                   :quantity 10.50}
           :quantity -200
           :commodity "HIMFUT"})))
  (is (= "    Assets:Checking:SBI                     -20000.0 INR"
         (hledger/->posting-entry
          {:account "Assets:Checking:SBI"
           :quantity -20000.0
           :commodity "INR"}))))
