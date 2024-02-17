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

(defn comment->str [comment]
  (str "\n    ; "
       (->> (str/split-lines comment)
            (str/join "\n    ; "))))

(defn ->posting-entry [{:keys [account comment amount
                        units unit-price commodity]}]
             (if commodity
               (str "    " account "  "
                    units " " commodity " @ "
                    unit-price " INR")
               (str "    " account "  " amount " " "INR"
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
      ->transaction
      transaction->str))
