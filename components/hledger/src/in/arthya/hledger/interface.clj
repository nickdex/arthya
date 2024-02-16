(ns in.arthya.hledger.interface
  (:require [in.arthya.hledger.core :as core]))

(defn ->hledger-entry
  "Converts record from transaction adapter to hledger transaction"
  [record]
  (core/->hledger-entry record))
