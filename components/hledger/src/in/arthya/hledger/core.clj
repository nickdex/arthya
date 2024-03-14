(ns in.arthya.hledger.core
  (:require
   [clojure.string :as str]
   [in.arthya.util.interface :as util]))

(defn ->transaction
  "Create hledger style transactions.
  If provided with xaccount value will create balanced transaction with two postings"
  [{:keys [memo quantity xaccount]
    :as transaction}]
  (merge
   (select-keys transaction
                [:date :payee :tags])

   {:comment (when memo
               (->> (str/split-lines memo)
                    (map str/trim)))
    :postings (remove nil?
                      [(merge
                        (util/create-map [:quantity] [quantity])
                        (select-keys transaction
                                     [:account,
                                      :commodity,
                                      :price]))
                       (when xaccount {:account xaccount})])}))

(defn space
  "Creates string with number of spaces given. Useful for indentation"
  [count]
  (apply str (repeat count " ")))

(defn comment->str [comment]
  (str "\n" (space 4) "; "
       (->> comment
            (str/join (str "\n" (space 4) "; ")))))

(defn price->str [{:keys [quantity commodity]}]
  (str quantity " " commodity))

(defn ->posting-entry [{:keys [account comment quantity commodity
                               price]}]
  (let [posting-space (space
                       (- 52
                          4 ;; Indent
                          (count account)
                          (count (str quantity))))]
    (str (space 4) account
         (when quantity
           (str posting-space
                (if price
                  (str quantity " " commodity " @ "
                       (price->str price))
                  (str quantity " " commodity))))
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
