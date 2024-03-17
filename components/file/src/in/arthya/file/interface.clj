(ns in.arthya.file.interface
  (:require
   [in.arthya.file.core :as core]
   [in.arthya.file.pdf :as pdf]))

(defn base64url->file!
  "Converts base64url string to a file and save it to a given location"
  [base64url-string file-path]
  (core/base64url->file! base64url-string file-path))

(defn decrypt-pdf!
  "Given password, it will decrypt the file and save it in place"
  [file-path password]
  (pdf/decrypt! file-path password))

(defn extract-pdf-text
  ([file-path password]
   (pdf/extract-and-print-pdf-text file-path password)))

(defn read
  "Reads a large file lazily"
  [file-path]
  (core/read file-path))

(defn write
  "Write large list to a file"
  [file-path data]
  (core/write file-path data))
