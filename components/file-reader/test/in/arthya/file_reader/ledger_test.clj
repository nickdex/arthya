(ns in.arthya.file-reader.ledger-test
  (:require
   [clojure.test :refer [deftest is]]
   [in.arthya.file-reader.ledger :as ledger]))

(deftest clean-memo-test
  (is (= "2889"
         (ledger/clean-memo
          "; 2889"))))

(deftest posting-test
  (is (= {:account "Assets:Checking:Sodexo-6102", :quantity "-239.0", :commodity "INR"}
         (ledger/->posting
          ["Assets:Checking:Sodexo-6102  -239.0 INR"])))
  (is (= {:account "Assets:Checking:Sodexo-6102"}
         (ledger/->posting
          ["Assets:Checking:Sodexo-6102  "])))
  (is (= {:account "Assets:Checking:Demat",
          :quantity "20"
          :price {:quantity "10.15"
                  :commodity "INR"}
          :commodity "NISCF_GG"
          :memo ["Test"]}
         (ledger/->posting
          ["Assets:Checking:Demat  20 NISCF_GG @ 10.15 INR"
           "; Test"])))
  (is (= {:account "Assets:Checking:Demat",
          :quantity "35.924",
          :price {:quantity "139.1762"
                  :commodity "INR"}
          :memo ["Test"],
          :commodity "NISCF_GG"}
         (ledger/->posting
          ["Assets:Checking:Demat               35.924 NISCF_GG @ 139.1762 INR"
           "; Test"]))))

(deftest entry-test
  (is (= {:memo ["446274839060" "SWIGGY"],
          :date "2024/01/02",
          :payee "Swiggy",
          :postings [{:account "Assets:Checking:Sodexo-6102",
                      :quantity "-242.0",
                      :commodity "INR"}
                     {:account "Expenses:Food"}],
          :tags nil}
         (ledger/->entry
          ["2024/01/02 Swiggy" "; 446274839060" "; SWIGGY" "Assets:Checking:Sodexo-6102  -242.0 INR" "Expenses:Food"]))))
