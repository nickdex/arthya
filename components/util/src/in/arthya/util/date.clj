(ns in.arthya.util.date
  (:require [clj-time.format :as f]))

(def output-format (f/formatter "yyyy/MM/dd"))

(defn format-date
  "Converts the LocalDate object back into the desired output string format"
  [date]
  (f/unparse output-format date))

(defn fix-date
  "Uses clj-time library formatter to convert string to LocalDate object, then unparse it to desired format"
  [date-str {:keys [input]}]
  (-> (f/formatter input)
      (f/parse date-str)
      format-date))
