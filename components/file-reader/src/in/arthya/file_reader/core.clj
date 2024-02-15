(ns in.arthya.file-reader.core
  (:require
   [clojure.string :as str]
   [in.arthya.file-reader.ledger :as ledger]
   [meta-csv.core :as csv])
  (:import
   [java.io FileInputStream]
   [org.apache.poi.ss.usermodel CellType WorkbookFactory]))

(defn cell-value [cell]
  (cond
    (= CellType/STRING (.getCellTypeEnum cell)) (.getStringCellValue cell)
    (= CellType/NUMERIC (.getCellTypeEnum cell)) (.getNumericCellValue cell)
    (= CellType/BOOLEAN (.getCellTypeEnum cell)) (.getBooleanCellValue cell)
    :else nil))

(defn- row-values [row]
  (let [record (->> row
                    .cellIterator
                    iterator-seq
                    (map cell-value)
                    (into [])
                    (drop 1)
                    drop-last)]
    #_(when (some? (first record)))
    record))

(defn read-excel-file [file-path]
  (with-open [inp (FileInputStream. file-path)]
    (let [workbook (WorkbookFactory/create inp)
          sheet (.getSheetAt workbook 0)]
      (->> sheet
           .rowIterator
           iterator-seq
           (map row-values)))))

(defn trim-rows
  "Removes rows from file reader which are not transactions"
  ([rows] (trim-rows nil rows))
  ([{:keys [skip terminate-pred]
     :or {skip 0
          terminate-pred identity}}
    rows]
   (->> rows
        (drop skip)
        (take-while terminate-pred))))

(defn read-excel
  [file-path opts]
  (->> (read-excel-file file-path)
       (trim-rows opts)))

(defn read-csv
  [file-path {:keys [columns] :as opts}]
  (->> (csv/read-csv file-path opts)
       (map #(if columns
               (select-keys % columns)
               %))))

(defn read-ledger [file-path]
  (->> file-path
       slurp
       ledger/->ledger-records
       (map #(map str/trim %))
       (map ledger/->record)))
