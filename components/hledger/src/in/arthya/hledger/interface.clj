(ns in.arthya.hledger.interface
 (:require [in.arthya.hledger.core :as core]))

(defn ->hledger-record
  "Converts common transaction structure to hledger notation"
  [record]
  (core/->hledger-record record))
