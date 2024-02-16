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

(defn parse-currency-from-string
  "Get currency as double value from string.
   Returns 0.0 in case of nil or empty string.

   Sample - 11,468.63 Dr."
  [s]
  (if (or (nil? s)
          (empty? (str/trim s)))
    0.0
    (let [cleaned (-> s
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
