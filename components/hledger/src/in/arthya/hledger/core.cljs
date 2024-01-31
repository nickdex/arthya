(ns in.arthya.hledger.core
  (:require [clojure.string :as str]))

(defn ->hledger-record [transaction]
  (let [{:keys [date payee tags memo postings]} transaction
        tag-line (when tags (str " ; "
                                 (str/join ", " tags)))]
    (->>
     (concat
      [(str date " " payee tag-line
            (when memo (str "\n    ; "  memo)))]
      (map (fn [{:keys [account comment amount]}]
             (str "    " account "  " amount " " "INR"
                  (when comment
                    (str "\n        ; " comment))))
           postings))
     (str/join "\n"))))
