(ns in.arthya.sms.imessage-date
  (:require
   [clj-time.coerce :as c]
   [clj-time.format :as f]
   [clj-time.core :as time]))

(def timestamp-factor 1000000000) ;; Nanoseconds

(defn get-offset []
  (/
   (c/to-long "2001-01-01")
   1000))

(defn get-local-time
  ([date-stamp] (get-local-time date-stamp (get-offset)))
  ([date-stamp offset]
   (try
     (let [adjusted-stamp (+ (/ date-stamp timestamp-factor) offset)
           instant (c/from-epoch (long adjusted-stamp))
           formatter (f/with-zone
                       (f/formatter  "yyyy-MM-dd HH:mm:ss")
                       (time/default-time-zone))]
       (f/unparse formatter instant))
     (catch Exception e
       (throw (ex-info "Invalid timestamp" {:date-stamp date-stamp}))))))
