(ns in.arthya.account-parser.interface
  "Responsible for consuming all sources of transactions
and giving common structure for further processing"
  (:require
   [in.arthya.account-parser.core :as core]))

(defn parse
  "Converts transaction records from source to common transaction structure.
  It take accept a file path and parse it if possible or it can take records as vector sequence.
  It support csv and xls files"
  ([file-path]
   (core/autodetector file-path))
  ([source records]
   (core/parse source records)))
