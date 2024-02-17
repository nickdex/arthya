(ns in.arthya.parser.interface-test
  (:require [clojure.test :refer [deftest is]]
            [in.arthya.parser.icici-bank :as icici-bank]
            [in.arthya.parser.interface :as parser]))

(deftest icici-bank-test
  (is (= [{:account :icici-bank,
           :amount -30.0,
           :date "2021/01/02",
           :memo "UPI/100217342352/NO REMARKS/gpay-1117387932/Axis B",
           :sno "3"}
          {:account :icici-bank,
           :amount -40.0,
           :date "2021/01/02",
           :memo "UPI/100286735847/NA/gpay-1117670878/Axis Bank Ltd.",
           :sno "2"}
          {:account :icici-bank,
           :amount -150.0,
           :date "2021/01/01",
           :memo "UPI/100173979427/NA/Q71714136@ybl/Punjab National ",
           :sno "1"}]
         (parser/parse :icici-bank
                       [["1"
                         "01/01/2021"
                         "01/01/2021"
                         "-"
                         "UPI/100173979427/NA/Q71714136@ybl/Punjab National "
                         "150.00"
                         "0.0"
                         "535726.55"]
                        ["2"
                         "02/01/2021"
                         "02/01/2021"
                         "-"
                         "UPI/100286735847/NA/gpay-1117670878/Axis Bank Ltd."
                         "40.00"
                         "0.0"
                         "535686.55"]
                        ["3"
                         "02/01/2021"
                         "02/01/2021"
                         "-"
                         "UPI/100217342352/NO REMARKS/gpay-1117387932/Axis B"
                         "30.00"
                         "0.0"
                         "535656.55"]]))))
