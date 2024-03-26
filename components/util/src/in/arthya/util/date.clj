(ns in.arthya.util.date
  (:require
   [tick.core :as t]
   [clojure.string :as str]))

(defn find-months-in-string [input-str]
  (let [month-regex (re-pattern "(?i)\\b(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\b")
        matches (re-seq month-regex input-str)]
    (when (seq matches)
      (first
       (map first matches)))))

(defn fix-month [date-str]
  (let [month (find-months-in-string date-str)
        handle-sep-fn #(if (= "Sep" %)
                         "Sept"
                         %)
        capitalized-month (-> month
                              str/lower-case
                              str/capitalize
                              handle-sep-fn)]
    (str/replace date-str month capitalized-month)))

(defn fix-date
  "Convert string to LocalDate object, then change it to desired format.
  Sanitizes the input in case source is Sending *Sep* with format *MMM*, the library expects *Sept*"
  [date-str {:keys [input output]
             :or {output "yyyy/MM/dd"
                  input "dd/MM/yyyy"}}]
  (let [sanitized-date-str (if (str/includes? input "MMM")
                             (fix-month date-str)
                             date-str)]
    (t/format (t/formatter output)
              (t/parse-date sanitized-date-str (t/formatter input)))))
