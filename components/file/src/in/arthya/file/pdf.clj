(ns in.arthya.file.pdf
  (:require
   [clojure.string :as str])
  (:import
   [org.apache.pdfbox Loader]
   [org.apache.pdfbox.io RandomAccessReadBufferedFile]
   [org.apache.pdfbox.text PDFTextStripper]))


(defn load
  ([file-path password]
   (->
    (RandomAccessReadBufferedFile. file-path)
    (Loader/loadPDF (when password password ))))
  )

(defn decrypt!
  [file-path password]
  (try
    (let [file (RandomAccessReadBufferedFile. file-path)
          pdd (Loader/loadPDF file password)]
      (.setAllSecurityToBeRemoved pdd true)
      (.save pdd "email-downloads/decrypted.pdf")
      (.close pdd)
      (println "Decryption Done...")
      #_())
    (catch Exception e
      (println "Error decrypting PDF:" (.getMessage e)))))

(defn extract-and-print-pdf-text [file-path password]
  (with-open [doc (load file-path password)]
    (let [stripper (PDFTextStripper.)]
      (.setSortByPosition stripper true)
      (let [texts (map (fn [p]
                         (.setStartPage stripper p)
                         (.setEndPage stripper p)
                         (let [text (.getText stripper doc)]
                           (.trim text)))
                       (range 1 (inc (.getNumberOfPages doc))))]
        {:all-text (str/join "\n\n" texts)
         :pages texts}))))
