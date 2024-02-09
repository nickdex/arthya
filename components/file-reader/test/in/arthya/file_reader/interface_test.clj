(ns in.arthya.file-reader.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.file-reader.interface :as file-reader]
            [in.arthya.util.interface :as util]))

(def ^:private parsed-file-no-opts
  (file-reader/read-excel
   "components/file-reader/resources/file-reader/OpTransactionHistory19-11-2023.xls"))

(def ^:private trimmed-file
  (file-reader/read-excel
   "components/file-reader/resources/file-reader/OpTransactionHistory19-11-2023.xls"
   {:skip-start 13
    :terminate-pred #(not (util/contains-partial? % "legends"))}))

(def ^:private csv-file
  (file-reader/read-csv
   "components/file-reader/resources/file-reader/8504313962_MFOrderBook-Jan2024.csv"
   {:columns ["Amount" "Date" "Last recorded NAV" "Unit" "Status" "Fund Name" "Scheme Name"]}))

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
  (is (= (count csv-file) 92))
  (is (= (first csv-file)
         {"Amount" 5000.0,
          "Date" "01-JAN-2024 00:00:00",
          "Last recorded NAV" 139.1762,
          "Unit" 35.924,
          "Status" "Executed",
          "Fund Name" "NIPPON INDIA MUTUAL FUND",
          "Scheme Name" "Nippon India Small Cap Fund - Growth Plan - Growth Option"})))
