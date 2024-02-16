(ns in.arthya.parser.mutual-fund
  (:require
   [clojure.string :as str]
   [in.arthya.util.interface :as util]))

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

(defn fix-date
  [s]
  (-> s
      (str/split #"\s")
      first
      (util/fix-date {:input "dd-MMM-yyyy"})))

(defn ->record [row]
  (let [unit-price (get row "Last recorded NAV")
        unit-name (commodity-name (get row "Scheme Name"))
        fund (get row "Fund Name")
        date (fix-date (get row "Date"))
        units (get row "Unit")]
    {:date date
     :payee fund
     :account :mutual-fund
     :commodity unit-name
     :units units
     :unit-price unit-price}))

(defn parse
  "Converts transaction records from mutual fund statement to common transaction structure. Expects list of list"
  [records]
  (->> records
       (map ->record)))
