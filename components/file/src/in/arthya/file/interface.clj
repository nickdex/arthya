(ns in.arthya.file.interface
  (:require
   [in.arthya.file.core :as core]))

(defn base64url->file!
  "Converts base64url string to a file and save it to a given location"
  [base64url-string file-path]
  (core/base64url->file! base64url-string file-path))
