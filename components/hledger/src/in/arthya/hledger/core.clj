(ns in.arthya.hledger.core
  (:require [clojure.string :as str]))

(defn ->transaction
  [{:keys [memo]
    :as transaction}]
  (merge
   (select-keys transaction
                [:date :payee :tags])

   {:comment (str/split-lines memo)
    :postings [(select-keys transaction
                            [:amount
                             :units
                             :unit-price
                             :currency
                             :commodity
                             :account])]}))

(defn space
  "Creates string with number of spaces given. Useful for indentation"
  [count]
  (apply str (repeat count " ")))

(defn comment->str [comment]
  (str "\n" (space 4) "; "
       (->> comment
            (str/join (str "\n" (space 4) "; ")))))

(defn ->posting-entry [{:keys [account comment amount currency
                               units unit-price commodity]}]
  (if commodity
    (str (space 4) account (space 2)
         units " " commodity " @ "
         unit-price " " currency)
    (str (space 4) account (when (and amount currency)
                             (str (space 2) amount " " currency))
         (when comment (comment->str comment)))))

(defn transaction->str
  [{:keys [date payee
           tags comment postings]}]
  (let [tag-line (when tags (str " ; "
                                 (str/join ", " tags)))]
    (->>
     (concat
      [(str date " " payee tag-line
            (when comment (comment->str comment)))]
      (map ->posting-entry postings))
     (str/join "\n"))))

(defn ->hledger-entry
  "From transaction adapter record, create a hledger entry in plain text"
  [transaction]
  (-> transaction
      transaction->str))
