(ns in.arthya.file-reader.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.file-reader.interface :as file-reader]))

(def file-path "components/file-reader/resources/file-reader/OpTransactionHistory19-11-2023.xls")

(deftest all-rows-parsed
  (is (= 678 (count (file-reader/read-excel file-path)))))

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
       (file-reader/read-excel file-path))))

