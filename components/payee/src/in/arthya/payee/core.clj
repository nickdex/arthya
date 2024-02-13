(ns in.arthya.payee.core
  (:require
   [in.arthya.util.interface :as util]))

(defn infer
  "Based on memo string matches, returns appropriate payee name, *Unknown* otherwise"
  [{:keys [memo]}]
  (cond
    (util/includes-any? memo ["cafe vishala"]) "Cafe Vishala"
    (util/includes-any? memo ["shoppy mart"]) "Shoppy Mart"
    (util/includes-any? memo ["organic load"]) "Organic Loaf"
    (util/includes-any? memo ["polar bear"]) "Polar Bear"
    (util/includes-any? memo ["shell" "naga gowri sampth"]) "Shell"
    (util/includes-any? memo ["sri sri tattva panchak"]) "Panchamrut"
    (util/includes-any? memo ["keerthi"]) "Keerthi Triumph"
    (util/includes-any? memo ["sumeru hotel and re"]) "Sumeru Hotel and Real Estate"
    (util/includes-any? memo ["anand"]) "Anand Sweets and Savouries"
    (util/includes-any? memo ["openai"]) "Open AI Inc"
    (util/includes-any? memo ["amazon"]) "Amazon"
    (util/includes-any? memo ["swiggy" "bundl tech"]) "Swiggy"
    (util/includes-any? memo ["nataraj food"]) "Nataraj Chole Bhature"
    :else "Unknown"))

