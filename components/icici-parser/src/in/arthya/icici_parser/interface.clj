(ns in.arthya.icici-parser.interface 
 "Parser to consume statements from ICICI bank accounts"
  (:require [in.arthya.icici-parser.core :as core]))

(defn ->hledger
  "Converts a CSV record to hledger notation"
  [record]
  (core/->hledger record))