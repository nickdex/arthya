(ns in.arthya.file-reader.interface 
  (:require [in.arthya.file-reader.core :as core]))

(defn read-excel
  "Reads excel file and converts records to clojure list of lists"
  [file-path]
  (core/read-excel file-path))