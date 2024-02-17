(ns in.arthya.file-reader.ledger
  (:require
   [clojure.string :as str]))

(defn group-lines [file]
  (let [lines (str/split-lines file)
        groups (partition-by empty? lines)
        groups (remove #(-> % first empty?) groups)]
    groups))

(defn parse-header [line]
  (try
    (let [parts (str/split line #"\s" 2)
          date (first parts)
          rest (second parts)
          idx (when rest (.indexOf rest ";"))
          [payee tags] (if (and rest (>= idx 0))
                         [(str/trim (subs rest 0 idx))
                          (str/trim (subs rest (inc idx)))]
                         [rest nil])
          tags (when tags (vec (map str/trim (str/split tags #","))))]
      {:date date
       :payee payee
       :tags tags})
    (catch Exception e (prn line (.getMessage e)))))

(defn ->posting [element]
  (let [[posting & comments] element
        [account r] (str/split posting #"\s\s+" 2)
        [amount unit] (if r
                        (str/split r #"\s")
                        [0 "INR"])]
    {:account account
     :amount amount
     :currency unit
     :comment comments}))

(defn group-items [lines]
  (reduce (fn [acc line]
            (if (clojure.string/starts-with? line ";")
              (update-in acc [(dec (count acc))] conj line) ; Add comment to last group
              (conj acc [line]))) ; Start a new group with the item
          []
          lines))

(defn ->entry [rows]
  (let [groups (group-items rows)
        [header & body] groups
        [transaction & transaction-comments] header
        transaction (parse-header transaction)
        accounts {:postings (->> body
                                 (map ->posting))}]
    (merge transaction accounts {:comment transaction-comments})))
