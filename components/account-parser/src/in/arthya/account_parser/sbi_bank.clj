(ns in.arthya.account-parser.sbi-bank
  (:require
   [clojure.string :as str]
   [in.arthya.util.interface :as util]))

(defn ->statement-map
  [row]
  (zipmap ["Txn Date" "Value Date" "Description" "Ref No./Cheque No." "Debit" "Credit" "Balance"]
          row))

(defn ->posting
  [row]
  (let [date (util/fix-date (get row "Txn Date")
                            {:input "d MMM yyyy"})
        memo (str (get row "Description")
                  "\n"
                  (get row "Ref No./Cheque No."))
        debit (util/parse-currency (get row "Debit"))
        credit (util/parse-currency (get row "Credit"))
        amount (if (or (nil? debit) (= 0 debit) (= 0.0 debit))
                 credit
                 (* -1 debit))]
    {:date date
     :memo memo
     :quantity amount
     :commodity "INR"
     :account :sbi-bank}))

(defn parse
  [records]
  (->> records
       (map #(str/split % #"\t"))
       (map ->statement-map)
       (map ->posting)))

; LocalWords:  Txn Cheque MMM yyyy
