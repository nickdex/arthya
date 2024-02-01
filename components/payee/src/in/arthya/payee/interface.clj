(ns in.arthya.payee.interface)

(defn infer
  "Find best possible match for payee provided by information from transaction. Return Unknown otherwise"
  [transaction]
  (core/infer transaction))
