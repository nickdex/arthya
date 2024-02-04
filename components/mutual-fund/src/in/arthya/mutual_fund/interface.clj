(ns in.arthya.mutual-fund.interface
  (:require [in.arthya.mutual-fund.core :as core]))

(defn parse
  "Converts transaction records from mutual fund statement to common transaction structure. Expects list of list"
  [records]
  (core/parse records))
