(ns in.arthya.icici-parser.core-test
  (:require [clojure.test :refer [deftest is]]
            [in.arthya.icici-parser.core :as core]))

(deftest merge-rows-over-two-lines
  (is (= (core/merge-paired-sequences
          [{"S No." "61",
            "Value Date" "25/05/2021",
            "Transaction Date" "25/05/2021",
            "Cheque Number" "-",
            "Transaction Remarks" "EBA/MF-AXIS ASS-",
            "Withdrawal Amount (INR )" "2500.00",
            "Deposit Amount (INR )" "0.0",
            "Balance (INR )" "586644.72"}
           {"S No." nil,
            "Value Date" nil,
            "Transaction Date" nil,
            "Cheque Number" nil,
            "Transaction Remarks" "10377800/20210525053459           ",
            "Withdrawal Amount (INR )" nil,
            "Deposit Amount (INR )" nil,
            "Balance (INR )" nil}
           {"S No." "62",
            "Value Date" "25/05/2021",
            "Transaction Date" "25/05/2021",
            "Cheque Number" "-",
            "Transaction Remarks" "EBA/MF-ICICI PR-10377801/20210525054555           ",
            "Withdrawal Amount (INR )" "2500.00",
            "Deposit Amount (INR )" "0.0",
            "Balance (INR )" "584144.72"}])
         '({"S No." "62"
            "Value Date" "25/05/2021",
            "Transaction Date" "25/05/2021",
            "Cheque Number" "-",
            "Transaction Remarks" "EBA/MF-ICICI PR-10377801/20210525054555           ",
            "Withdrawal Amount (INR )" "2500.00",
            "Deposit Amount (INR )" "0.0",
            "Balance (INR )" "584144.72"}
           {"S No." "61",
            "Value Date" "25/05/2021",
            "Transaction Date" "25/05/2021",
            "Cheque Number" "-",
            "Transaction Remarks" "EBA/MF-AXIS ASS-10377800/20210525053459           ",
            "Withdrawal Amount (INR )" "2500.00",
            "Deposit Amount (INR )" "0.0",
            "Balance (INR )" "586644.72"}))))

(deftest hledger-notation
  (is (= (core/->hledger-record
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :memo "Some memo"
           :tags ["T:A"]
           :postings [{:account "Expense:Food"
                       :amount "-10"
                       :comment "Vada pav"}]})
         "2021/12/16 Fruit Vendor ; T:A\n    ; Some memo\n    Expense:Food  -10 INR\n        ; Vada pav")))
