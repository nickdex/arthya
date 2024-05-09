(ns in.arthya.sms.interface
  (:require
   [in.arthya.sms.core :as core]))

(defn read-sms
  "Reads all messages from the database.
  Options:
  - after - Date string (yyyy-mm-dd). The records will be fetched after the date"
  [opts]
  (core/read-sms opts))
