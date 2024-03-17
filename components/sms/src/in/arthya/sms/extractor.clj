(ns in.arthya.sms.extractor
  (:require
   [in.arthya.util.interface :as util]))

(defn icici-card [msg]
  (let [amount-spent (re-find #"(INR|USD)\s([\d,\.]+)" msg)
        card-account (re-find #"ICICI Bank Card (\w+)" msg)
        date-of-transaction (re-find #"\bon\s(\d{2}-\w{3}-\d{2})" msg)
        payee (re-find #"\bat\s(.*?)\." msg)]
    {:commodity (second amount-spent)
     :quantity (* -1 (util/parse-currency (nth amount-spent 2)))
     :account (str "Liabilities:CreditCard:" (subs (second card-account) 2))
     :date (-> date-of-transaction
               second
               (util/fix-date {:input "dd-MMM-yy"}))
     :payee (util/title-case (second payee))}))

(defn icici-bank
  [message]
  (let [account-number (re-find #"Acct (\w+)" message)
        debited-amount (re-find #"Rs ([\d,\.]+)" message)
        date-of-transaction (re-find #"on (\d{2}-\w{3}-\d{2})" message)
        credited-to (re-find #"; (.+) credited" message)
        upi-reference (re-find #"UPI:(\d+)" message)]
    {:account (str "Assets:Checking:ICICI");;(second account-number)
     :quantity (* -1 (util/parse-currency (second debited-amount)))
     :date (-> date-of-transaction
               second
               (util/fix-date {:input "dd-MMM-yy"}))
     :commodity "INR"
     :payee (util/title-case (second credited-to))
     :memo (str "UPI Ref: " (second upi-reference))}))

(defn pluxee-spent [s]
  (let [amount-spent-re (re-find #"Rs\. (\d+\.\d+)" s)
        card-re (re-find #", card no\.xx(\d+)," s)
        datetime-re (re-find #", on (\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2}) at " s)
        payee-re (re-find #" at (.+)\. Avl bal" s)
        available-balance-re (re-find #"Avl bal Rs\.(\d+\.\d+)\." s)]
    {:quantity (* -1 (util/parse-currency (second amount-spent-re)))
     :account (str "Assets:Checking:Sodexo:" (second card-re))
     :commodity "INR"
     :date (-> datetime-re
               second
               (util/fix-date {:input "dd-MM-yyyy HH:mm:ss"}))
     :payee (second payee-re)
     :available-balance (second available-balance-re)}))

(defn pluxee-spent-old [s]
  (let [amount-spent-re (re-find #"Rs\. (\d+\.\d+)" s)
        card-re (re-find #"Pluxee Card xx(\d+)" s)
        datetime-re (re-find #"on (\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2}) at " s)
        payee-re (re-find #" at (.+?)\. Txn no\." s)
        txn-no-re (re-find #"\. Txn no\. (\d+)\." s)
        available-balance-re (re-find #"Avl bal is Rs\. (\d+\.\d+)\." s)]
    {:quantity (second amount-spent-re)
     :account (str "Assets:Checking:Sodexo:" (second card-re))
     :commodity "INR"
     :date (-> datetime-re
               second
               (util/fix-date {:input "MMM dd yyyy HH:mm:ss"}))
     :payee (second payee-re)
     :memo (str "Reference: " (second txn-no-re))
     :available-balance (second available-balance-re)}))

(defn pluxee-refund [s]
  (let [card-re (re-find #"Pluxee Card xx(\d+)" s)
        credited-amount-re (re-find #"credited with INR (\d+\.\d+)" s)
        credit-datetime-re (re-find #"on \w+ (\w+ \d+ \d+ \d+:\d+:\d+)" s)]
    {:account (str "Assets:Checking:Sodexo:" (second card-re))
     :commodity "INR"
     :quantity (-> credited-amount-re
                   second
                   util/parse-currency
                   (* -1))
     :date (-> credit-datetime-re
               second
               (util/fix-date {:input  "MMM dd yyyy HH:mm:ss"}))}))

(defn pluxee-topup [s]
  {:account "Assets:Checking:Sodexo:6102"
   :payee "Sumeru"
   :commodity "INR"
   :quantity 3500
   :date (-> (re-find #"on \w+ (\w+ \d+ \d+ \d+:\d+:\d+)" s)
             second
             (util/fix-date {:input  "MMM dd yyyy HH:mm:ss"}))})

(def extractor-map
  {"spent on ICICI Bank Card" icici-card
   "ICICI Bank Acct XX016 debited" icici-bank
   "spent from Pluxee" pluxee-spent
   "spent from Meal Card Wallet linked to your Pluxee Card" pluxee-spent-old
   "Pluxee Card xx6102 has been credited" pluxee-refund
   "Pluxee Card has been successfully credited with Rs.3500" pluxee-topup})
