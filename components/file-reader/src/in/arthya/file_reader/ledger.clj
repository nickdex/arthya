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
          tags (when tags
                 (->> (str/split tags #",")
                      (map str/trim)
                      vec))]
      (zipmap [:date :payee :tags]
              (remove nil? [date payee tags])))
    (catch Exception e (prn line (.getMessage e)))))

(defn clean-memo [memo]
  (-> memo
      (subs 1)
      str/trim))

(defn posting-price [price]
  (let [[quantity commodity] price]
    (util/create-map [:quantity :commodity]
                     [(util/parse-currency quantity) commodity])))

(defn ->posting [element]
  (let [[posting & memo] element
        [account r] (str/split (str/trim posting) #"\s\s+" 2)
        [quantity commodity _ & price]
        (when r
          (str/split r #"\s+"))
        quantity (util/parse-currency quantity)]
    (util/create-map [:account :quantity :commodity :memo
                      :price]
                     [account quantity commodity (->> memo
                                                      (map clean-memo)
                                                      (str/join "\n"))
                      (posting-price price)])))

(defn group-items [lines]
  (reduce (fn [acc line]
            (if (clojure.string/starts-with? line ";")
              (update-in acc [(dec (count acc))] conj line) ; Add memo to last group
              (conj acc [line]))) ; Start a new group with the item
          []
          lines))

(defn ->entry [rows]
  (let [groups (group-items rows)
        [header & body] groups
        [transaction & transaction-memos] header
        transaction (parse-header transaction)
        accounts {:postings (->> body
                                 (map ->posting))}]
    (merge transaction accounts
           (util/create-map
            [:memo] [(->> transaction-memos
                          (map clean-memo)
                          (str/join "\n"))]))))
