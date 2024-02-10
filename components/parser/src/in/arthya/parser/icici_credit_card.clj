(ns in.arthya.parser.icici-credit-card
  (:require [clojure.string :as str]
            [in.arthya.util.interface :as util]))

(defn ->posting
  "Converts statement map to transaction row map containing date, amount and memo. Can contain additional information as required"
  [row]
  (let [date (util/fix-date (get row "Transaction Date"))
        memo (str (get row "Reference Number")
                  "\n"
                  (get row "Details"))
        amount-str (-> (get row "Amount (INR)"))
        amount (util/parse-currency-from-string amount-str)
        amount (cond
                 (str/includes? amount-str "Cr.") amount
                 (str/includes? amount-str "Dr.") (* -1 amount)
                 :else (throw (AssertionError.
                               (str "String doesn't contain amount value"
                                    "\nRow\n"
                                    row))))]
    {:date date
     :memo memo
     :amount amount
     :account :icici-credit-card}))

(defn parse
  [records]
  (map ->posting records))
