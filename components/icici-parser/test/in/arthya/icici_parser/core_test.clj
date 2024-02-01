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

(deftest posting-test
  (is (= (core/->posting
          {"S No." "36",
           "Value Date" "27/01/2024",
           "Transaction Date" "27/01/2024",
           "Cheque Number" "-",
           "Transaction Remarks" "UPI/439329851533/2 nights tent/9483182708@ybl/Bank of Baroda/ACD01HN54XZ6AE4700",
           "Withdrawal Amount (INR )" 1600.0,
           "Deposit Amount (INR )" 0.0,
           "Balance (INR )" 4075.94})
         {:date "2024/01/27",
          :memo "UPI/439329851533/2 nights tent/9483182708@ybl/Bank of Baroda/ACD01HN54XZ6AE4700",
          :amount -1600.0,
          :sno "36"}))
  (is (= (core/->posting
          {"S No." "33",
           "Value Date" "27/01/2024",
           "Transaction Date" "27/01/2024",
           "Cheque Number" "-",
           "Transaction Remarks" "MMT/IMPS/402711565960/ReqPay/Mr  NIKHIL/State Bank",
           "Withdrawal Amount (INR )" 0.0,
           "Deposit Amount (INR )" 20000.0,
           "Balance (INR )" 20705.94})
         {:date "2024/01/27", :memo "MMT/IMPS/402711565960/ReqPay/Mr  NIKHIL/State Bank", :amount 20000.0, :sno "33"})))

(deftest clean-postings-test
  (is (= (core/clean-postings
          ['("34"
             "27/01/2024"
             "27/01/2024"
             "-"
             "UPI/439344021256/Deposit 1/9131161006@payt/State Bank Of I/ACD01HN4QG9TS2X69VSC"
             15000.0
             0.0
             5705.94)
           '("35"
             "27/01/2024"
             "27/01/2024"
             "-"
             "UPI/402784415982/bananas/q692910553@ybl/Yes Bank Ltd/ACD01HN53S95N29BXYZ70W0XRY"
             30.0
             0.0
             5675.94)])
         '({:date "2024/01/27",
            :memo "UPI/402784415982/bananas/q692910553@ybl/Yes Bank Ltd/ACD01HN53S95N29BXYZ70W0XRY",
            :amount -30.0,
            :sno "35"}
           {:date "2024/01/27",
            :memo "UPI/439344021256/Deposit 1/9131161006@payt/State Bank Of I/ACD01HN4QG9TS2X69VSC",
            :amount -15000.0,
            :sno "34"}))))
