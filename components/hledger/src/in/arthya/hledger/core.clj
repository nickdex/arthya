(ns in.arthya.hledger.core
  (:require [clojure.string :as str]))

(defn ->transaction
  [{:keys [memo]
    :as transaction}]
  (merge
   (select-keys transaction
                [:date :payee :tags])

   {:comment memo
    :postings [(select-keys transaction
                            [:amount
                             :units
                             :unit-price
                             :commodity
                             :account])]}))

(defn ->hledger-entry
  "Converts transaction adapter record to hledger map and create ledger entry in plain text"
  [transaction]
  (let [{:keys [date payee
                tags comment postings]} (->transaction transaction)
        tag-line (when tags (str " ; "
                                 (str/join ", " tags)))]
    (->>
     (concat
      [(str date " " payee tag-line
            (when comment (str "\n    ; "
                               (->> (str/split-lines comment)
                                    (str/join "\n    ; ")))))]
      (map (fn [{:keys [account comment amount
                        units unit-price commodity]}]
             (if commodity
               (str "    " account "  "
                    units " " commodity " @ "
                    unit-price " INR")
               (str "    " account "  " amount " " "INR"
                    (when comment
                      (str "\n        ; " comment)))))
           postings))
     (str/join "\n"))))
