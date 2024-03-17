(ns in.arthya.util.date
  (:require
   [clj-time.format :as f]
   [clojure.string :as str]))

(defn format-date
  "Converts the LocalDate object back into the desired output string format"
  ([date output]
   (f/unparse (f/formatter (if output
                             output
                             "yyyy/MM/dd")) date)))

(defn fix-date
  "Uses clj-time library to convert string to LocalDate object, then change it to desired format.
  Sanitizes the input in case source is Sending *Sep* with format *MMM*, the library expects *Sept*"
  [date-str {:keys [input output]}]
  (let [sanitized-date-str (if (and (str/includes? input "MMM")
                                    (str/includes? date-str "Sep"))
                             (str/replace date-str #"Sep" "Sept")
                             date-str)]
    (-> (f/formatter input)
        (f/parse sanitized-date-str)
        (format-date output))))
