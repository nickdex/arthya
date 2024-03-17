(ns in.arthya.file.core
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str])
  (:import
   [java.nio.file Files Paths]
   [java.util Base64]))

(defn base64url-decode
  [base64url-string]
  (let [base64-string (-> base64url-string
                          (str/replace "-" "+")
                          (str/replace "_" "/"))
        decoder (Base64/getDecoder)]
    (.decode decoder base64-string)))

(defn base64url->file!
  [base64url-string file-path]
  (let [decoded-bytes (base64url-decode base64url-string)
        path (Paths/get file-path (into-array String []))
        options (into-array java.nio.file.OpenOption
                            [java.nio.file.StandardOpenOption/CREATE
                             java.nio.file.StandardOpenOption/TRUNCATE_EXISTING])]
    (Files/write path decoded-bytes options)))

(defn read [file]
  (letfn [(helper [rdr]
            (lazy-seq
             (if-let [line (.readLine rdr)]
               (cons line (helper rdr))
               (do (.close rdr) nil))))]
    (helper (io/reader file))))

(defn write [file-path lines]
  (with-open [w (io/writer file-path)]
    (doseq [line lines]
      (.write w line)
      (.newLine w))))
