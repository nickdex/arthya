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
  (is (= 1 (count csv-file-columns)))
  (is (= 3 (count csv-file)))
  (is (= {"col0" nil,
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
          "col11" nil}
         (first csv-file)))
  (is (= {"Amount" 5000.0,
          "Date" "01-JAN-2024 00:00:00",
          "Last recorded NAV" 139.1762,
          "Unit" 35.924,
          "Status" "Executed",
          "Fund Name" "NIPPON INDIA MUTUAL FUND",
          "Scheme Name" "Nippon India Small Cap Fund - Growth Plan - Growth Option"}
         (first csv-file-columns))))

(def ^:private ledger-file-parsed
  (file-reader/read-ledger
   "components/file-reader/resources/file-reader/test.ledger"))

(deftest ledger-file-parsed-test
  (is (= 3 (count ledger-file-parsed)))
  (is (= {:comment [ "189" "UPI Payment Received" ],
          :date "2020/01/19",
          :payee "ICICI Bank",
          :postings [ {:account "Liabilities:Credit-Card:ICICI-Amazon",
                       :quantity "7626.61",
                       :commodity "INR"}
                      {:account "Assets:Checking:ICICI"} ],
          :tags nil}
         (first ledger-file-parsed)))
  (is (= {:date "2020/01/20",
          :payee "Unknown",
          :postings [ {:account "Liabilities:Credit-Card:ICICI-Amazon",
                       :quantity "-19.76",
                       :comment ["606686" "Interest Amount Amortization - <3/6>"],
                       :commodity "INR"}
                      {:account "Liabilities:Credit-Card:ICICI-Amazon",
                       :quantity "-3.56",
                       :comment ["5606688" "IGST-CI@18%"],
                       :commodity "INR"}
                      {:account "Expenses:Tax:GST",
                       :quantity "3.56",
                       :commodity "INR"}
                      {:account "Liabilities:Credit-Card:ICICI-Amazon",
                       :quantity "-363.41",
                       :comment ["606702" "Principal Amount Amortization - <3/6>"],
                       :commodity "INR"}
                      {:account "Expenses:Shopping"} ],
          :tags nil}
         (second ledger-file-parsed)))
  (is (= {:comment [ "UPI/8404579/travel/paytm-75722521@/Paytm Payments /AC2T4HPWZZRG32" ],
          :date "2020/01/24",
          :payee "Shoppy Mart",
          :postings [ {:account "Assets:Checking:ICICI",
                       :quantity "-100.0",
                       :commodity "INR"}
                      {:account "Expenses:Travel:Trip"} ],
          :tags ["Trip:Chikmagalur"]}
         (nth ledger-file-parsed 2))))

(def ^:private text-file-parsed
  (file-reader/read-text
   "components/file-reader/resources/file-reader/text-test.xls"
   {:skip 19
    :terminate-pred #(seq %)}))

(deftest text-file-parsed-test
  (is (= 6
         (count text-file-parsed)))
  (is (= "Txn Date\tValue Date\tDescription\tRef No./Cheque No.\t        Debit\tCredit\tBalance\t"
         (first text-file-parsed)))
  (is (= "27 Jan 2024\t27 Jan 2024\t   TO TRANSFER-INB IMPS/P2A/402711565960/XXXXXXX016ICIC--\tIMPS00246444860MOAIYHKOG0               TRANSFER T\t20,000.00\t \t1,59,236.55"
         (last text-file-parsed))))
