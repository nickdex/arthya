(ns in.arthya.parser.sodexo
  (:require
   [clojure.string :as str]
   [in.arthya.util.interface :as util]))

(defn ->statement-map
  "Convert a row (list) to map using statement header"
  [row]
  (zipmap ["Date & Time of Balance"
           "Card type"
           "Transaction ID"
           "Transaction Details"
           "Credit(in Rs.)"
           "Debit(in Rs.)"
           "Previous Balance(in Rs.)"
           "New Balance(in Rs.)"]
          row))

(defn fix-date [s]
  (-> s
      (str/split #"\s")
      first
      util/fix-date))

(defn ->posting
  [row]
  (let [id (get row "Transaction ID")
        date (fix-date (get row "Date & Time of Balance"))
        memo (get row "Transaction Details")
        memo (str id "\n" memo)
        credit (util/parse-currency (get row "Credit(in Rs.)"))
        debit (util/parse-currency (get row "Debit(in Rs.)"))
        amount (if (or (= 0 debit) (= 0.0 debit))
                 credit
                 (* -1 debit))]
    {:date date
     :memo memo
     :quantity amount
     :commodity "INR"
     :account :sodexo}))

(defn parse
  [records]
  (->> records
       (map ->statement-map)
       (map ->posting)))
