(ns in.arthya.file-reader.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.file-reader.interface :as file-reader]
            [in.arthya.util.interface :as util]))

(def ^:private parsed-file-no-opts
  (file-reader/read-excel
   "components/file-reader/resources/file-reader/test.xls"))

(def ^:private trimmed-file
  (file-reader/read-excel
   "components/file-reader/resources/file-reader/test.xls"
   {:skip 13
    :terminate-pred #(not (util/contains-partial? % "legends"))}))

(def ^:private csv-file-columns
  (file-reader/read-csv
   "components/file-reader/resources/file-reader/test.csv"
   {:columns ["Amount" "Date" "Last recorded NAV" "Unit" "Status" "Fund Name" "Scheme Name"]}))

(def ^:private csv-file
  (file-reader/read-csv
   "components/file-reader/resources/file-reader/test2.csv"
   {:skip 12}))

(deftest rows-parsed
  (is (seq? parsed-file-no-opts))
  (is (= 678 (count parsed-file-no-opts)))

  (is (seq? trimmed-file))
  (is (= 636 (count trimmed-file))))

(deftest table-headers-included
  (is (some
       #(= %
           ["S No."
            "Value Date"
            "Transaction Date"
            "Cheque Number"
            "Transaction Remarks"
            "Withdrawal Amount (INR )"
            "Deposit Amount (INR )"
            "Balance (INR )"])
       parsed-file-no-opts)))

(deftest csv-rows-parsed
  (is (= (count csv-file-columns) 1))
  (is (= (count csv-file) 3))
  (is (= (first csv-file)
         {"col0" nil,
          "col1" nil,
          "Transaction Date" "01/02/2024",
          "Details" "INSTANT EMI OFFUS CONVERSION_C",
          "col4" nil,
          "col5" nil,
          "Amount (INR)" "11,211.33 Cr.",
          "col7" nil,
          "col8" nil,
          "Reference Number" 8691532626,
          "col10" nil,
          "col11" nil}))
  (is (= (first csv-file-columns)
         {"Amount" 5000.0,
          "Date" "01-JAN-2024 00:00:00",
          "Last recorded NAV" 139.1762,
          "Unit" 35.924,
          "Status" "Executed",
          "Fund Name" "NIPPON INDIA MUTUAL FUND",
          "Scheme Name" "Nippon India Small Cap Fund - Growth Plan - Growth Option"})))

(def ^:private ledger-file-parsed
  (file-reader/read-ledger
   "components/file-reader/resources/file-reader/test.ledger"))

(deftest ledger-file-parsed-test
  (is (= (count ledger-file-parsed) 3))
  (is (= (first ledger-file-parsed)
         {:comment '("; 189" "; UPI Payment Received"),
          :date "2020/01/19",
          :payee "ICICI Bank",
          :postings '({:account "Liabilities:Credit-Card:ICICI-Amazon",
                       :amount "7626.61",
                       :comment nil,
                       :currency "INR"}
                      {:account "Assets:Checking:ICICI",
                       :amount 0,
                       :comment nil,
                       :currency "INR"}),
          :tags nil}))
  (is (= (second ledger-file-parsed)
         {:comment nil,
          :date "2020/01/20",
          :payee "Unknown",
          :postings '({:account "Liabilities:Credit-Card:ICICI-Amazon",
                       :amount "-19.76",
                       :comment [ "; 606686" "; Interest Amount Amortization - <3/6>" ],
                       :currency "INR"}
                      {:account "Liabilities:Credit-Card:ICICI-Amazon",
                       :amount "-3.56",
                       :comment [ "; 5606688" "; IGST-CI@18%" ],
                       :currency "INR"}
                      {:account "Expenses:Tax:GST",
                       :amount "3.56",
                       :comment nil,
                       :currency "INR"}
                      {:account "Liabilities:Credit-Card:ICICI-Amazon",
                       :amount "-363.41",
                       :comment [ "; 606702" "; Principal Amount Amortization - <3/6>" ],
                       :currency "INR"}
                      {:account "Expenses:Shopping",
                       :amount 0,
                       :comment nil,
                       :currency "INR"}),
          :tags nil}))
  (is (= (nth ledger-file-parsed 2)
         {:comment '("; UPI/8404579/travel/paytm-75722521@/Paytm Payments /AC2T4HPWZZRG32"),
          :date "2020/01/24",
          :payee "Shoppy Mart",
          :postings '({:account "Assets:Checking:ICICI",
                       :amount "-100.0",
                       :comment nil,
                       :currency "INR"}
                      {:account "Expenses:Travel:Trip",
                       :amount 0,
                       :comment nil,
                       :currency "INR"}),
          :tags ["Trip:Chikmagalur"]})))
