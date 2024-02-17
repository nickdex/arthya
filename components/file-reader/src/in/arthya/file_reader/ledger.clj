(ns in.arthya.file-reader.ledger
  (:require
   [clojure.string :as str]
   [in.arthya.util.interface :as util]))

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

(defn clean-comment [comment]
  (-> comment
      (subs 1)
      str/trim))

(defn ->posting [element]
  (let [[posting & comments] element
        [account r] (str/split posting #"\s\s+" 2)
        [amount unit] (if r
                        (str/split r #"\s")
                        [0 "INR"])]
    (util/create-map [:account :amount :currency :comment]
                     [account amount unit (map clean-comment comments)])))

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
    (merge transaction accounts
           (util/create-map
            [:comment] [(map clean-comment transaction-comments)]))))
