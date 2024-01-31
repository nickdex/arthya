(ns in.arthya.icici-parser.interface 
 "Parser to consume statements from ICICI bank accounts"
  (:require [in.arthya.icici-parser.core :as core]))

(defn ->hledger
  "Converts transaction records from list of lists to hledger notation"
  [records]
  (core/->hledger records))