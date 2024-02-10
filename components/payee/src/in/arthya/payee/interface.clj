(ns in.arthya.payee.interface
 (:require [in.arthya.payee.core :as core]))

(defn infer
  "Find best possible match for payee provided by information from transaction. Return Unknown otherwise"
  [transaction]
  (core/infer transaction))
