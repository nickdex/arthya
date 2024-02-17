(ns in.arthya.hledger.interface
  (:require [in.arthya.hledger.core :as core]))

(defn ->hledger-entry
  "Create hledger plain text entry from hlegder transaction map"
  [transaction]
  (core/transaction->str transaction))

(defn ->hledger-transaction
  "Converts record from transaction adapter to hledger transaction"
  [record]
  (core/->transaction record))
