(ns in.arthya.inference-engine.core
  (:require
   [in.arthya.util.interface :as util]))

(def payee-inferrence-map
  {["amazon"] "Amazon",
   ["anand"] "Anand Sweets and Savouries",
   ["cafe vishala"] "Cafe Vishala",
   ["eating love"] "Eating Love"
   ["sumeru hotel"] "Sumeru Hotel"
   ["keerthi"] "Keerthi Triumph",
   ["nataraj food"] "Nataraj Chole Bhature",
   ["chatgpt""openai"] "Open AI Inc",
   ["organic load"] "Organic Loaf",
   ["polar bear"] "Polar Bear",
   ["salary"] "Sumeru",
   ["shell" "naga gowri sampth" "naga gowri"] "Shell",
   ["shyamjis chole bhature"] "Shyam Chole Bhature",
   ["shoppy mart"] "Shoppy Mart",
   ["sri sri tattva panchak"] "Panchamrut",
   ["indian oil"] "Indian Oil"
   ["subway"] "Subway"
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
    "pav bhaji"
    "fruits"
    "fafda"
    "eating love"
    "sumeru hotel"
    "lunch"
    "swiggy"
    "zomato"] "Expenses:Food"
   ["shoppy mart"] "Expenses:Food:Groceries"
   ["open ai"] "Expenses:Education"
   ["shell"] "Expenses:Travel:Fuel"})

(defn first-matched-val
  [attributes inferrence-map]
  (some (fn [attribute]
          (some #(when (util/includes-any? attribute %)
                   (inferrence-map %))
                (keys inferrence-map)))
        attributes))

(defn infer-payee [{:keys [memo]}]
  (let [infered-payee (first-matched-val [memo] payee-inferrence-map)]
    (if infered-payee
      infered-payee
      "Unknown")))

(defn infer-account [{:keys [payee memo]}]
  (let [infered-account (first-matched-val [payee memo] account-inferrence-map)]
    (if infered-account
      infered-account
      "Unknown")))
