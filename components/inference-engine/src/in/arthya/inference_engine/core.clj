(ns in.arthya.inference-engine.core
  (:require
   [in.arthya.util.interface :as util]))

(def payee-inferrence-map
  {["amazon"] "Amazon",
   ["anand"] "Anand Sweets and Savouries",
   ["cafe vishala"] "Cafe Vishala",
   ["keerthi"] "Keerthi Triumph",
   ["nataraj food"] "Nataraj Chole Bhature",
   ["openai"] "Open AI Inc",
   ["organic load"] "Organic Loaf",
   ["polar bear"] "Polar Bear",
   ["salary"] "Sumeru",
   ["shell" "naga gowri sampth"] "Shell",
   ["shoppy mart"] "Shoppy Mart",
   ["sri sri tattva panchak"] "Panchamrut",
   ["sumeru hotel and re"] "Sumeru Hotel and Real Estate",
   ["swiggy" "bundl tech"] "Swiggy",
   ["zomato"] "Zomato"})

(def account-inferrence-map
  {["organic loaf"
    "chole bhature"
    "panchamrut"
    "anand sweets"
    "polar bear"
    "cafe vishala"
    "swiggy"
    "zomato"] "Expenses:Food"
   ["shell"] "Expenses:Travel:Fuel"})

(defn first-matched-val
  [attribute inferrence-map]
  (some #(when (util/includes-any? attribute %)
           (inferrence-map %))
        (keys inferrence-map)))

(defn infer-payee [{:keys [memo]}]
  (let [infered-payee (first-matched-val memo payee-inferrence-map)]
    (if infered-payee
      infered-payee
      "Unknown")))

(defn infer-account [{:keys [payee]}]
  (let [infered-account (first-matched-val payee account-inferrence-map)]
    (if infered-account
     infered-account
     "Unknown")))
