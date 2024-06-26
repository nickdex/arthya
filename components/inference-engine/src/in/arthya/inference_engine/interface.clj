(ns in.arthya.inference-engine.interface
  (:require
   [in.arthya.inference-engine.core :as core]))

(defn infer-payee
  "Find best possible match for payee provided by information from transaction. Return Unknown otherwise"
   [transaction]
   (core/infer-payee transaction))

(defn infer-account
  "Find best possible match for account provided by information from transaction. Return Unknown otherwise"
  [transaction]
  (core/infer-account transaction))
