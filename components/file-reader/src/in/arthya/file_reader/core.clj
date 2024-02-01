(ns in.arthya.file-reader.core
  (:import [java.io FileInputStream]
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
  ([rows] (trim-rows rows nil))
  ([rows
    {:keys [skip-start terminate-pred]
     :or {skip-start 0
          terminate-pred identity}}]
   (->> rows
        (drop skip-start)
        (take-while terminate-pred))))

(defn read-excel
  [file-path opts]
  (-> file-path
      read-excel-file
      (trim-rows opts)))
