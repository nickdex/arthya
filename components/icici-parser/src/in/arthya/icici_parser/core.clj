(ns in.arthya.icici-parser.core
  (:require [clj-time.format :as f]
            [clojure.string :as str]))

(def input-format (f/formatter "dd/MM/yyyy"))
(def output-format (f/formatter "yyyy/MM/dd"))

;; Convert the date string into a LocalDate object
(defn parse-date [date-str]
  (f/parse input-format date-str))

;; Convert the LocalDate object back into the desired output string format
(defn format-date [date]
  (f/unparse output-format date))

(defn fix-date [date-time]
  (-> date-time parse-date format-date))

(defn merge-paired-sequences [seq-of-seqs]
  (loop [s seq-of-seqs
         result []]
    (cond
      (empty? s) (reverse result)  ; When all sequences are processed

      ;; If the current head is a pair (current and next), merge them
      (nil? (get (second s) "Transaction Date"))
      (recur (drop 2 s) (conj result (update (first s) "Transaction Remarks" str (get (second s)  "Transaction Remarks"))))

      :else  ; Otherwise, just move the head to the result
      (recur (rest s) (conj result (first s))))))

(defn ->posting [row]
  (let [date (fix-date (get row "Transaction Date"))
        serial (get row "S No.")
        memo (get row "Transaction Remarks")
        debit (get row "Withdrawal Amount (INR )")
        credit (get row "Deposit Amount (INR )")
        amount (if (= "0.0" debit)
                 credit
                 (str "-" debit))]
    {:date date
     :memo memo
     :amount amount
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

(defn ->hledger-record [transaction]
  (let [{:keys [date payee tags memo postings]} transaction
        tag-line (when tags (str " ; "
                                 (str/join ", " tags)))]
    (->>
     (concat
      [(str date " " payee tag-line
            (when memo (str "\n    ; "  memo)))]
      (map (fn [{:keys [account comment amount]}]
             (str "    " account "  " amount " " "INR"
                  (when comment
                    (str "\n        ; " comment))))
           postings))
     (str/join "\n"))))

(defn- clean-postings [records]
  (->> records
       (drop 13)
       (drop-last 29)
       (map ->statement-map)
       merge-paired-sequences
       (map ->posting)))

(defn- ->transaction
  [{:keys [date memo amount]}]
  {:date date
   :tag ["Source:ICICI"]
   :postings [{:comment memo
               :amount amount
               :account "Assets:Checking:ICICI"}]})

(defn ->hledger
  [records]
  (->>
   (clean-postings records)
   (map ->transaction)
   (map ->hledger-record)))
