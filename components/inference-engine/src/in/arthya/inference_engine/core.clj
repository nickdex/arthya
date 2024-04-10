(ns in.arthya.inference-engine.core
  (:require
   [in.arthya.util.interface :as util]))

(def payee-inferrence-map
  {["123566003      000000016900    WuVI45S8EC1"] "Amazon Kindle",
   ["12356ND07      000000064616    Wb3jm9UKlM1"] "Google Workspace",
   ["9739387442@payt" "pooja sunil"] "Pooja Sunil Makhija",
   ["DEBIT-ACHDr NACH00000000056470 KISETSUSAISONF"] "Cred Cash",
   ["091001527016:Int.Pd"] "ICICI Bank"
   ["Iskon"] "Iskon Temple",
   ["Murthy and Co D"] "Indian Oil",
   ["amazon"] "Amazon",
   ["anand sweets"] "Anand Sweets and Savouries",
   ["appleservices"] "Apple LLC",
   ["avirmukhi enterprises"] "Titan Eye Plus",
   ["better batter"] "Better Batter",
   ["bookmyshow"] "BookMyShow",
   ["cafe vishala"] "Cafe Vishala",
   ["chatgpt" "openai"] "Open AI Inc",
   ["cred.club"] "Cred",
   ["devanshee"] "Devanshee",
   ["eating love"] "Eating Love",
   ["indian oil"] "Indian Oil",
   ["anishajha@okici"] "Anisha Jha"
   ["innovative re"] "Bigbasket",
   ["jarin taj"] "Asma Maid",
   ["keerthi"] "Keerthi Triumph",
   ["melting moments"] "Melting Moments",
   ["mookambika"] "Udupi Garden",
   ["nataraj food"] "Nataraj Chole Bhature",
   ["nikhil"] "Nikhil",
   ["oam industries"] "Haldiram",
   ["organic load"] "Organic Loaf",
   ["polar bear"] "Polar Bear",
   ["preeti puran"] "Preeti Beniwal",
   ["q772272788@ybl"] "Jitendra Singh",
   ["salary"] "Sumeru",
   ["shell" "naga gowri sampth" "naga gowri"] "Shell",
   ["shoppy mart"] "Shoppy Mart",
   ["shyamjis chole bhature"] "Shyam Chole Bhature",
   ["srinivas putta"] "Srinivas Putta",
   ["sriveda sattva" "sri sri tattva panchak" "bharatpe.910049"] "Panchamrut",
   ["subway"] "Subway",
   ["sumeru hotel and re"
    "SUMERU/bharatpe.905292"
    "SUMERU/bharatpe9071999"
    "sumeru hotel"] "Sumeru Hotel",
   ["sumeru software solutions pvt ltd"] "Sumeru Software",
   ["swiggy" "bundl tech"] "Swiggy",
   ["twc kanakpura"] "Third Wave Coffee",
   ["vishnurajv139@"] "Venkatesha S G",
   ["waterwala"] "Drink Prime",
   ["zomato"] "Zomato"})

(def account-inferrence-map
  {["123566003      000000016900    WuVI45S8EC1"] "Expenses:Education:Books",
   ["Cred Cash"] "Liabilities:Bike:Triumph-Speed-400",
   ["Savi Consultant"] "Expenses:Food",
   ["ach/"] "Income:Dividends",
   ["anand sweets"
    "melting moments"
    "organic loaf"
    "cafe vishala"
    "eating love"
    "panchamrut"
    "polar bear"
    "sumeru hotel"
    "pooja sunil makh"
    "haldiram"
    "third wave coffee"
    "the rameshwaram"
    "therameshwaram"
    "mr dharmalingam"
    "swiggy"
    "zomato"
    "udupi garden"] "Expenses:Food",
   ["apple"
    "biscuits"
    "chole bhature"
    "dinner"
    "fafda"
    "brunch"
    "coconut"
    "chocolate"
    "vanilla"
    "corn"
    "bread"
    "almond"
    "chole kulche"
    "breakfast"
    "fruits"
    "grapes"
    "lunch"
    "paan"
    "pav bhaji"
    "roti"
    "snacks"] "Expenses:Food",
   ["apple llc"] "Expenses:Utilities:Digital",
   ["asma maid"] "Expenses:Utilities:Housekeeping",
   ["auto" "cab"] "Expenses:Travel",
   ["bookmyshow"] "Expenses:Entertainment:Movies",
   ["drink prime"] "Expenses:Utilities:Water",
   ["medicine"] "Expenses:Health:Medicine"
   ["eye checkup"] "Expenses:Health:Eye",
   ["google workspac"] "Expenses:Miscellaneous:Business",
   ["indian oil" "shell" "petrol"] "Expenses:Travel:Fuel",
   ["iskon temple"] "Expenses:Miscellaneous",
   ["lassi" "coffee" "tea" "juice"] "Expenses:Food:Beverages",
   ["091001527016:Int.Pd"] "Income:Interest"
   ["tailor"] "Expenses:Utilities"
   ["rent"] "Expenses:Utilities:Rent"
   ["open ai"] "Expenses:Education",
   ["shoppy mart" "bigbasket" "grocery" "groceries"] "Expenses:Food:Groceries",
   ["stitch" "clothes iron" "iron clothes"] "Expenses:Utilities:Clothes",
   ["suma j"] "Expenses:Health:Medicine",
   ["sumeru software" "salary"] "Income:Salary:AOLD",
   ["titan eye plus"] "Expenses:Health:Eye",
   ["vaidic dharma"] "Expenses:Miscellaneous:AOL"
   ["wellbeing nutrition"] "Expenses:Health"})

(defn first-matched-val
  [attributes inferrence-map]
  (some (fn [attribute]
          (some #(when (util/includes-any? attribute %)
                   (inferrence-map %))
                (keys inferrence-map)))
        attributes))

(defn infer-payee [transaction]
  (let [memo (:memo transaction)
        payee (:payee transaction)]
    (or
     (first-matched-val [memo payee] payee-inferrence-map)
     payee)))

(defn infer-account [transaction]
  (let [memo (:memo transaction)
        payee (:payee transaction)]
    (first-matched-val (remove nil? [payee memo]) account-inferrence-map)))
