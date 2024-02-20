(ns in.arthya.hledger.core
  (:require
   [clojure.string :as str]
   [in.arthya.util.interface :as util]))

(defn ->transaction
  [{:keys [memo amount]
    :as transaction}]
  (merge
   (select-keys transaction
                [:date :payee :tags])

   {:comment (when memo
               (->> (str/split-lines memo)
                    (map str/trim)))
    :postings [(merge
                (util/create-map [:units] [amount])
                (select-keys transaction
                             [:account,
                              :commodity,
                              :conversion-commodity,
                              :conversion-units]))]}))

(defn space
  "Creates string with number of spaces given. Useful for indentation"
  [count]
  (apply str (repeat count " ")))

(defn comment->str [comment]
  (str "\n" (space 4) "; "
       (->> comment
            (str/join (str "\n" (space 4) "; ")))))

(defn ->posting-entry [{:keys [account comment units commodity
                               conversion-commodity conversion-units]}]
  (let [posting-space (space
                       (- 52
                          4 ;; Indent
                          (count account)
                          (count (str units))))]
    (str (space 4) account
         (if (and conversion-units
                  conversion-commodity)
           (str posting-space
                units " " commodity " @ "
                conversion-units " " conversion-commodity)
           (str (when (and units commodity)
                  (str posting-space units " " commodity))
                (when comment (comment->str comment)))))))

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
