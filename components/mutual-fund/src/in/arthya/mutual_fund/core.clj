(ns in.arthya.mutual-fund.core
  (:require [clj-time.format :as f]
            [clojure.string :as str]))

(defn- commodity-name [text]
  (let [[scheme-name & plans] (str/split text #" - ")
        first-part (->> #" "
                        (str/split scheme-name)
                        (map first)
                        (str/join ""))]
    (str/upper-case
     (str first-part
          (when-let [second-part (not-empty (str/join "" (map first plans)))]
            (str "_" second-part))))))

(def input-format (f/formatter "dd-MMM-yyyy"))
(def output-format (f/formatter "yyyy/MM/dd"))


;; Convert the date string into a LocalDate object
(defn parse-date [date-str]
  (f/parse input-format date-str))

;; Convert the LocalDate object back into the desired output string format
(defn format-date [date]
  (f/unparse output-format date))

(defn fix-date [date]
  (-> date
      parse-date
      format-date))

(defn fix-date-format [date-str]
  (let [[day month year] (str/split date-str #"-")]
    (str/join "-" [day (str/capitalize month) year])))

(defn ->record [transaction]
  (let [unit-price (get transaction "Last recorded NAV")
        unit-name (commodity-name (get transaction "Scheme Name"))
        fund (get transaction "Fund Name")
        status (get transaction "Status")
        date  (-> transaction
                  (get "Date")
                  (str/split #"\s")
                  first
                  fix-date-format)
        date (format-date (parse-date (str/replace date #"Sep" "Sept")))
        units (get transaction "Unit")]
    {:date date
     :units units
     :fund fund
     :unit-name unit-name
     :status status
     :unit-price unit-price}))

(defn parse
  [records]
  (->> records
       (map ->record)))