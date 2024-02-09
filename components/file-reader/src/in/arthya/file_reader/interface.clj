(ns in.arthya.file-reader.interface
  (:require [in.arthya.file-reader.core :as core]))

(defn read-excel
  "Reads excel file and converts records to clojure list of lists. Take optional additional parameters to trim rows from reader

  ### Optional Parameters
  - :termiante-pred: Predicate to drop records at tail end of file
  - :skip: skip number of records from start of file"
  ([file-path] (read-excel file-path nil))
  ([file-path opts]
   (core/read-excel file-path opts)))

(defn read-csv
  "Reads csv file and converts records to clojure list of maps. CSV Columns are used for keys.

  ### Optional Parameters
  - :columns: vector of column names
  - :skip: skip number of records from start of file"
  ([file-path] (read-csv file-path nil))
  ([file-path opts]
   (core/read-csv file-path opts)))
