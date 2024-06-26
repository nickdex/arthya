(ns in.arthya.account-parser.icici-bank
  (:require [in.arthya.util.interface :as util]))

(defn merge-paired-sequences [seq-of-seqs]
  (loop [s seq-of-seqs
         result []]
    (cond
      (empty? s) (reverse result)  ; When all sequences are processed

      ;; If the current head is a pair (current and next), merge them
      (nil? (get (second s) "Value Date"))
      (recur (drop 2 s) (conj result (update (first s) "Transaction Remarks" str (get (second s)  "Transaction Remarks"))))

      :else  ; Otherwise, just move the head to the result
      (recur (rest s) (conj result (first s))))))

(defn ->posting
  "Converts statement map to transaction row map containing date, amount and memo. Can contain additional information as required"
  [row]
  (let [date (util/fix-date (get row "Value Date"))
        serial (get row "S No.")
        memo (get row "Transaction Remarks")
        debit (util/parse-currency (get row "Withdrawal Amount (INR )"))
        credit (util/parse-currency (get row "Deposit Amount (INR )"))
        amount (if (or (= 0 debit) (= 0.0 debit))
                 credit
                 (* -1 debit))]
    {:date date
     :memo memo
     :quantity amount
     :commodity "INR"
     :account :icici-bank
     :sno serial}))

(defn ->statement-map
  "Convert a row (list) to map using statement header"
  [row]
  (zipmap '("S No."
            "Value Date"
            "Transaction Date"
            "Cheque Number"
            "Transaction Remarks"
            "Withdrawal Amount (INR )"
            "Deposit Amount (INR )"
            "Balance (INR )")
          row))

(defn clean-postings
  "Cleans incoming statement rows to generate records for further processing based on logic. Eg inferring other fields"
  ([records]
   (->> records
        (map ->statement-map)
        merge-paired-sequences
        (map ->posting))))

(defn parse
  [records]
  (clean-postings records))
