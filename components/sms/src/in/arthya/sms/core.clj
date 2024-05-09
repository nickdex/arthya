(ns in.arthya.sms.core
  (:require
   [clojure.string :as str]
   [in.arthya.sms.extractor  :as extractor]
   [in.arthya.sms.imessage-date :as imessage-date]
   [in.arthya.util.interface :as util]
   [methodical.core :as m]
   [toucan2.core :as t2])
  (:import
   [java.nio.charset StandardCharsets]))

(def db-spec
  {:dbtype   "sqlite"
   :dbname   "/Users/nik/Library/Messages/chat.db"})

(m/defmethod t2/do-with-connection :default
  [_connectable f]
  (t2/do-with-connection db-spec f))

(def start-pattern (byte-array [0x01 0x2B]));; SOH, +
(def end-pattern (byte-array [0x86 0x84]));; SSA, IND

(defn find-pattern [bytes pattern]
  (let [len (alength bytes)
        pat-len (alength pattern)]
    (loop [i 0]
      (when (< (+ i pat-len) len)
        (if (java.util.Arrays/equals pattern (java.util.Arrays/copyOfRange bytes i (+ i pat-len)))
          i
          (recur (inc i)))))))

(defn parse [bytes]
  (let [start-idx (find-pattern bytes start-pattern)
        end-idx (find-pattern bytes end-pattern)]
    (if (and start-idx end-idx)
      (let [clean-bytes (java.util.Arrays/copyOfRange bytes (+ start-idx 3) end-idx)]
        (try
          (String. clean-bytes StandardCharsets/UTF_8)
          (catch IllegalArgumentException e
            ;; Handle invalid UTF-8 data
            (String. clean-bytes StandardCharsets/UTF_8))))
      "Error: Start or end pattern not found.")))

(defn to-text [bytes]
  (let [text (parse bytes)]
    (when text
      (str/replace text "�" ""))))

(defn messages [{:keys [after]}]
  (->>
   (t2/select
    [:model/message
     :attributedbody
     :date
     :rowid]
    {:where [:>= :date after]
     :order-by [[:date :desc]]})
   (remove #(nil? (:attributedbody %)))
   (map (fn [{:keys [attributedbody
                     rowid
                     date]}]
          {:text (try (to-text attributedbody)
                      (catch Exception _
                        (throw (ex-info "Error in parsing " rowid))
                        nil))
           :date (imessage-date/get-local-time date)}))))

(defn process-messages [messages]
  (for [message messages]
    (try
      (let [method-key (some (fn [[pattern extractor-fn]]
                               (when (util/includes-any? message [pattern])
                                 extractor-fn))
                             extractor/extractor-map)]
        (when method-key
          (extractor/extract message method-key)))
      (catch Exception e
        (prn "Error: " e message)))))

(defn read-sms [{:keys [after]}]
  (->>
   (messages {:after (imessage-date/->epoch after)})
   (filter #(util/includes-any? % ["spent"
                                   "debit"
                                          ;; "refund"
                                   "processed"
                                   "credit"]))
   (remove #(util/includes-any? % ["otp"]))
          ;; (filter #(util/includes-any? % [
          ;;                                 ;; "Rs"
          ;;                                ;; "USD"
          ;;                                "INR"
          ;;                                 ]))
   (sort-by :date)
   reverse
   (map :text)
   process-messages
   (remove nil?)))
