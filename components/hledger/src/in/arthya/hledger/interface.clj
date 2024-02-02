(ns in.arthya.hledger.interface
  (:require [in.arthya.hledger.core :as core]))

(defn ->hledger-record
  "Converts record from transaction adapter to hledger transaction"
  [record]
  (core/->hledger-record record))
