(ns in.arthya.sms.extractor
  (:require
   [in.arthya.util.interface :as util]))

(defn create-txn [txn]
  (into {} (filter (comp not nil? val) txn)))

(defn icici-card [message]
  (let [amount-spent (re-find #"(INR|USD)\s([\d,\.]+)" message)
        card-account (re-find #"ICICI Bank Card (\w+)" message)
        date-of-transaction (re-find #"\bon\s(\d{2}-\w{3}-\d{2})" message)
        payee (re-find #"\bat\s(.*?)\." message)]
    (create-txn {:account (str
                           "Liabilities:CreditCard:"
                           (subs (second card-account) 2)),
                 :commodity (second amount-spent),
                 :date (->
                        date-of-transaction
                        second
                        (util/fix-date {:input "dd-MMM-yy"})),
                 :payee (util/title-case (second payee)),
                 :quantity (-> amount-spent
                               (nth 2)
                               util/parse-currency
                               -)
                 :source message})))

(defn icici-bank
  [message]
  (let [account-number (re-find #"Acct (\w+)" message)
        debited-amount (re-find #"Rs ([\d,\.]+)" message)
        date-of-transaction (re-find #"on (\d{2}-\w{3}-\d{2})" message)
        credited-to (re-find #"; (.+) credited" message)
        upi-reference (re-find #"UPI:(\d+)" message)]
    (create-txn {:account (str "Assets:Checking:ICICI"),
                 :memo (str "UPI Ref - " (second upi-reference)),
                 :commodity "INR",
                 :date (-> date-of-transaction second (util/fix-date {:input "dd-MMM-yy"})),
                 :payee (util/title-case (second credited-to)),
                 :quantity (-> debited-amount
                               second
                               util/parse-currency
                               -)
                 :source message})))

(defn pluxee-spent [message]
  (let [amount-spent-re (re-find #"Rs\. (\d+\.\d+)" message)
        card-re (re-find #", card no\.xx(\d+)," message)
        datetime-re (re-find #", on (\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2}) at " message)
        payee-re (re-find #" at (.+)\. Avl bal" message)
        available-balance-re (re-find #"Avl bal Rs\.(\d+\.\d+)\." message)]
    (create-txn {:account (str "Assets:Checking:Sodexo:" (second card-re)),
                 :memo (str "Available Balance - " (second available-balance-re))
                 :commodity "INR",
                 :date (->
                        datetime-re
                        second
                        (util/fix-date {:input "dd-MM-yyyy HH:mm:ss"})),
                 :payee (second payee-re),
                 :quantity (-> amount-spent-re
                               second
                               util/parse-currency
                               -)
                 :source message})))

(defn pluxee-spent-old [message]
  (let [amount-spent-re (re-find #"Rs\. (\d+\.\d+)" message)
        card-re (re-find #"Pluxee Card xx(\d+)" message)
        datetime-re (re-find #"on (\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2}) at " message)
        payee-re (re-find #" at (.+?)\. Txn no\." message)
        txn-no-re (re-find #"\. Txn no\. (\d+)\." message)
        available-balance-re (re-find #"Avl bal is Rs\. (\d+\.\d+)\." message)]
    (create-txn {:account (str "Assets:Checking:Sodexo:" (second card-re)),
                 :memo (str "Reference: " (second txn-no-re)
                               "\n"
                               "Available Balance - " (second available-balance-re)),
                 :commodity "INR",
                 :date (->
                        datetime-re
                        second
                        (util/fix-date {:input "MMM dd yyyy HH:mm:ss"})),
                 :payee (second payee-re),
                 :quantity (-> amount-spent-re
                               second
                               util/parse-currency
                               -)
                 :source message})))

(defn pluxee-refund [message]
  (let [card-re (re-find #"Pluxee Card xx(\d+)" message)
        credited-amount-re (re-find #"credited with INR (\d+\.\d+)" message)
        credit-datetime-re (re-find #"on \w+ (\w+ \d+ \d+ \d+:\d+:\d+)" message)]
    (create-txn {:account (str "Assets:Checking:Sodexo:" (second card-re)),
                 :commodity "INR",
                 :date (->
                        credit-datetime-re
                        second
                        (util/fix-date {:input "MMM dd yyyy HH:mm:ss"})),
                 :payee nil,
                 :quantity (-> credited-amount-re second util/parse-currency)
                 :source message})))

(defn pluxee-topup [message]
  (create-txn {:date (-> (re-find #"on \w+ (\w+ \d+ \d+ \d+:\d+:\d+)" message)
                         second
                         (util/fix-date {:input "MMM dd yyyy HH:mm:ss"})),
               :payee "Sumeru",
               :account "Assets:Checking:Sodexo:6102",
               :quantity 3500.0,
               :commodity "INR"
               :source message}))

(def extractor-map
  {"spent on ICICI Bank Card" icici-card
   "ICICI Bank Acct XX016 debited" icici-bank
   "spent from Pluxee" pluxee-spent
   "spent from Meal Card Wallet linked to your Pluxee Card" pluxee-spent-old
   "Pluxee Card xx6102 has been credited" pluxee-refund
   "Pluxee Card has been successfully credited with Rs.3500" pluxee-topup})
