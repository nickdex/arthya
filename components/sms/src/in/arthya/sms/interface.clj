(ns in.arthya.sms.interface
  (:require
   [in.arthya.sms.core :as core]))

(defn read-sms
  "Reads all messages from the database"
  [file-path]
  (core/read-sms file-path))
