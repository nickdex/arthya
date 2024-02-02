(ns in.arthya.hledger.core
  (:require [clojure.string :as str]))

(defn ->transaction
  [{:keys [date payee tags memo amount account]}]
  {:date date
   :payee payee
   :tags tags
   :comment memo
   :postings [{:amount amount
               :account account}]})

(defn ->hledger-record
  "Converts transaction adapter record to hledger map and create ledger entry in plain text"
  [transaction]
  (let [{:keys [date payee tags comment postings]} (->transaction transaction)
        tag-line (when tags (str " ; "
                                 (str/join ", " tags)))]
    (->>
     (concat
      [(str date " " payee tag-line
            (when comment (str "\n    ; "  comment)))]
      (map (fn [{:keys [account comment amount]}]
             (str "    " account "  " amount " " "INR"
                  (when comment
                    (str "\n        ; " comment))))
           postings))
     (str/join "\n"))))
