(ns in.arthya.parser.interface
  "Responsible for consuming all sources of transactions
and giving common structure for further processing"
  (:require
   [in.arthya.parser.core :as core]))

(defn parse
  "Converts transaction records from source to common transaction structure. Expects list of list"
  ([file-path]
   (core/autodetector file-path))
  ([source records]
   (core/parse source records)))
