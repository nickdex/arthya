(ns in.arthya.util.interface
  (:require [clojure.string :as str]
            [in.arthya.util.date :as date]))

(defn includes-any?
  "Return true if any of the strings matches the attribute, false otherwise.
   In case attribute is nil, return false"
  [attribute strings]
  (if (nil? attribute)
    false
    (let [lower-category (str/lower-case attribute)]
      (some #(str/includes? lower-category (str/lower-case %)) strings))))

(defn contains-partial?
  "Return true if any of rows partially contains the search string"
  [rows search-str]
  (some #(includes-any? % [search-str]) rows))

(defn parse-currency
  "Ensures to return currency as double value.
   Returns 0.0 in case of nil or empty string.
  "
  [s]
  (cond
    (= (type 0.0) (type s)) s
    (= (type 0) (type s)) (double s)
    (or (nil? s) (empty? (str/trim s))) 0.0
    :else (let [cleaned (-> s
                            (clojure.string/replace #"[^\d.]+" "")
                            (clojure.string/replace #"\.$" ""))
                parsed (Double/parseDouble cleaned)]
            parsed)))

(defn fix-date
  "Converts any input date string to yyyy/MM/dd

   Options:
   - :input - Specifies input date format. Defaults to dd/MM/yyyy"
  ([date-str] (fix-date date-str {:input "dd/MM/yyyy"}))
  ([date-str opts]
   (date/fix-date date-str opts)))

(defn non-empty-value? [val]
  (cond
    (nil? val) false
    (string? val) (seq val)
    (coll? val) (seq val)
    :else true))

(defn create-map
  "Creates a map from keys and values, excluding entries with values that are nil, empty lists, empty vectors, or empty strings."
  [keys values]
  (let [pairs (filter (fn [[_ val]] (non-empty-value? val))
                      (map vector keys values))]
    (into {} pairs)))

(defn title-case [s]
  (str/join " " (map #(str/capitalize %) (str/split s #"\s+"))))
