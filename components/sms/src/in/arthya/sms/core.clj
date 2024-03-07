(ns in.arthya.sms.core
  (:require
   [clj-time.coerce :as c]
   [clojure.string :as str]
   [methodical.core :as m]
   [toucan2.core :as t2]
   [toucan2.execute :as t2exec]))

(def db-spec
  {:dbtype   "sqlite"
   :dbname   "/Users/nik/Repo/arthya/development/src/dev/resources/chat.db"})

(m/defmethod t2/do-with-connection :default
  [_connectable f]
  (t2/do-with-connection db-spec f))

(t2exec/query :conn db-spec ["Select text,date from message;"])

(t2/table-name :model/message)
(t2/select-one  :model/message)

(tap>
 (->>
  (t2/select :conn db-spec
             [:model/message
              :text
              :rowid]
             :text [:not= nil]
             {:order-by [[:date :desc]]})
  (map :text)
  (filter #(or (str/includes? % "Rs")
               (str/includes? % "USD")
               (str/includes? % "INR")
               (str/includes? % "debit")
               (str/includes? % "credit")))
  #_(filter #(str/includes? % "ICICI Bank Acct XX016"))))

(tap>
 ;; (c/to-long "2024-01-01")
  ;; => 1704067200000

 (c/from-long (long (/ 725799841330549 60))))
(t2/select "message" )

(defn read-sms
  [file-path])
