(ns in.arthya.util.date
  (:require
   [clj-time.format :as f]
   [clojure.string :as str]))

(def output-format (f/formatter "yyyy/MM/dd"))

(defn format-date
  "Converts the LocalDate object back into the desired output string format"
  [date]
  (f/unparse output-format date))

(defn fix-date
  "Uses clj-time library to convert string to LocalDate object, then change it to desired format.
  Sanitizes the input in case source is Sending *Sep* with format *MMM*, the library expects *Sept*"
  [date-str {:keys [input]}]
  (let [sanitized-date-str (if (and (str/includes? input "MMM")
                                (str/includes? date-str "Sep"))
                         (str/replace date-str #"Sep" "Sept")
                         date-str)]
    (-> (f/formatter input)
        (f/parse sanitized-date-str)
        format-date)))
