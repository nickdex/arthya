(ns in.arthya.sms.imessage-date
  (:require
   [clj-time.coerce :as c]
   [clj-time.format :as f]
   [clj-time.core :as time]
   [tick.core :as t]))

(def timestamp-factor 1000000000) ;; Nanoseconds

(defn get-offset []
  (/
   (c/to-long "2001-01-01")
   1000))

(defn get-local-time
  "Converts date-stamp to a local date string"
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

(defn ->epoch
  "Converts a local date string to a date stamp"
  [date-str]
  (-> date-str
      (str "T00:00:00")
      t/date-time
      t/instant
      t/long
      (- (get-offset))
      (* timestamp-factor)
      #_()))
