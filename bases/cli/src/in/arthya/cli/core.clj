(ns in.arthya.cli.core
  (:gen-class)
  (:require
   [in.arthya.hledger.interface :as hl]
   [in.arthya.inference-engine.interface :as ie]
   [in.arthya.sms.interface :as sms]))

(defn export-sms [{:keys [file-path]
                   :or {file-path  "/Users/nik/Downloads/sms.edn"}
                   :as opts}]
  (let [records (->>
                 (sms/read-sms opts)
                 (map #(if-let [payee (ie/infer-payee %)]
                         (assoc % :payee payee)
                         %))
                 (map #(if-let [account (ie/infer-account %)]
                         (assoc % :xaccount account)
                         %))
                 (map hl/->hledger-transaction))]
    (println (count records) " exported to " file-path)
    (->> records
         pr-str
         (spit file-path))))

(defn -main [& args]
  (println "Starting export of sms")
  (export-sms {:limit 500})
  (System/exit 0))
