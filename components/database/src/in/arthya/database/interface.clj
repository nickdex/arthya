(ns in.arthya.database.interface
  (:require
   [clojure.edn :as edn]
   [datascript.core :as d]))

(def schema
  {:postings {:db/cardinality​ :db.cardinality/many
              :db/valueType :db.type/ref}})

(defn persist!
  "Write to disk"
  ([database] (persist! "temp/arthya.edn" database))
  ([file-path database]
   (->> database
        pr-str
        (spit file-path))))

(defn disk-db
  "Reads from disk"
  ([] (disk-db "temp/arthya.edn"))
  ([file-path]
   (->> file-path
        slurp
        (edn/read-string
         {:readers d/data-readers}))))

(def transact! d/transact!)

(defn q
  [query & inputs]
  (apply d/q query inputs))

(def create-conn d/create-conn)
