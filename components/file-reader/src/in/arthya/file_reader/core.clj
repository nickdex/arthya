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

(defn row-values [row]
  (let [record (->> row
                    .cellIterator
                    iterator-seq
                    (map cell-value)
                    (remove nil?)
                    (into []))]
    #_(when (some? (first record)))
    record))

(defn read-excel-file
  "Reads excel file for given sheet name, or first sheet. Returns list of lists for each row and column.
  It tries to map string, boolean and numeric data type if possible and returns nil otherwise"
  [file-path {:keys [sheet-name]}]
  (with-open [inp (FileInputStream. file-path)]
    (let [workbook (WorkbookFactory/create inp)
          sheet (if sheet-name
                  (.getSheet workbook sheet-name)
                  (.getSheetAt workbook 0))]
      (->> sheet
           .rowIterator
           iterator-seq
           (map row-values)))))

(defn trim-rows
  "Removes rows from file reader which are not transactions. No operation if options are not given"
  ([rows] rows)
  ([{:keys [skip terminate-pred]
     :or {skip 0
          terminate-pred identity}}
    rows]
   (->> rows
        (drop skip)
        (take-while terminate-pred))))

(defn read-excel
  [file-path opts]
  (->> (read-excel-file file-path opts)
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
       ledger/group-lines
       (map #(map str/trim %))
       (map ledger/->entry)))

(defn read-text [file-path opts]
  (->> file-path
       slurp
       str/split-lines
       (trim-rows opts)))
