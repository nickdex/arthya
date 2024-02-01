(ns in.arthya.icici-parser.interface 
 "Parser to consume statements from ICICI bank accounts"
  (:require [in.arthya.icici-parser.core :as core]))

(defn parse
  "Converts transaction records from bank statement to common transaction structure. Expects list of list"
  [records]
  (core/parse records))
