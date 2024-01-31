(ns in.arthya.hledger.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.hledger.interface :as hledger]))

(deftest hledger-notation
  (is (= (hledger/->hledger-record
          {:date "2021/12/16"
           :payee "Fruit Vendor"
           :memo "Some memo"
           :tags ["T:A"]
           :postings [{:account "Expense:Food"
                       :amount "-10"
                       :comment "Vada pav"}]})
         "2021/12/16 Fruit Vendor ; T:A\n    ; Some memo\n    Expense:Food  -10 INR\n        ; Vada pav")))
