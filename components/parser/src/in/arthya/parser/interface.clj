(ns in.arthya.parser.interface
  "Responsible for consuming all sources of transactions
and giving common structure for further processing"
  (:require
   [in.arthya.parser.icici-bank :as icici-bank]
   [in.arthya.parser.icici-credit-card :as icici-cc]))

(defn parse
  "Converts transaction records from source to common transaction structure. Expects list of list"
  [source records]
  (condp = source
    :icici-bank (icici-bank/parse records)
    :icici-cc (icici-cc/parse records)))
