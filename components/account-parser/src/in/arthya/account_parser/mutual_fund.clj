(ns in.arthya.account-parser.mutual-fund
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
  {:account :mutual-fund,
   :commodity (commodity-name (get row "Scheme Name")),
   :quantity (get row "Unit")
   :price {:commodity "INR"
           :quantity (get row "Last recorded NAV")}
   :date (fix-date (get row "Date")),
   :payee (get row "Fund Name")})

(defn parse
  "Converts transaction records from mutual fund statement to common transaction structure. Expects list of list"
  [records]
  (->> records
       (map ->record)))
