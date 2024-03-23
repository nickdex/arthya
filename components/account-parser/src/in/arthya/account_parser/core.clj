(ns in.arthya.account-parser.core
  (:require
   [clojure.string :as str]
   [in.arthya.file-reader.interface :as file-reader]
   [in.arthya.hledger.interface :as hledger]
   [in.arthya.account-parser.icici-bank :as icici-bank]
   [in.arthya.account-parser.icici-credit-card :as icici-cc]
   [in.arthya.account-parser.mutual-fund :as mf]
   [in.arthya.account-parser.sbi-bank :as sbi-bank]
   [in.arthya.account-parser.sodexo :as sodexo]
   [in.arthya.util.interface :as util]
   [in.arthya.inference-engine.interface :as ie]))

(defn parse
  "Converts transaction records from source to common transaction structure. Expects list of list"
  ([source records]
   (condp = source
     :icici-cc (icici-cc/parse records)
     :sbi-bank (sbi-bank/parse records)
     :sodexo (sodexo/parse records))))

(defn parse-icici-bank [file]
  (->>
   (file-reader/read-excel
    (.getAbsolutePath file)
    {:skip 13
     :terminate-pred #(not (util/includes-any? (first %) ["legends"]))})
   icici-bank/parse
   (map #(assoc % :account "Assets:Checking:ICICI"))))

(defn parse-mf [file]
  (->>
   (file-reader/read-csv
    file
    {:columns ["Amount" "Date" "Last recorded NAV" "Unit" "Status" "Fund Name" "Scheme Name"]})
   mf/parse
   (map #(assoc % :account "Assets:Checking:Demat"))))

(defn parse-icici-credit-card [file]
  (->>
   (file-reader/read-csv
    file
    {:skip 12
     :columns ["Transaction Date" "Amount (INR)" "Reference Number" "Details"]})
   icici-cc/parse
   (map #(assoc % :account "Liabilities:Credit-Card:ICICI-Amazon"))))

(defn autodetector
  "Auto detects parser based on file name heuristics"
  ([file-path]
   (->>
    (cond
      (util/includes-any? file-path ["OpTransaction"]) (parse-icici-bank file-path)
      (util/includes-any? file-path ["MFOrderBook"]) (parse-mf file-path)
      (util/includes-any? file-path ["CCLastStatement"]) (parse-icici-credit-card file-path))
    (sort-by :date)
    (map #(assoc % :payee (ie/infer-payee %))))))
