(ns in.arthya.inference-engine.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [in.arthya.inference-engine.core :as core]))

(deftest infer-payee-test
  (is (= "Amazon"
      (core/first-matched-val "amazon"
                         core/payee-inferrence-map))))

(deftest infer-account-test
  (is (= "Expenses:Food"
         (core/first-matched-val "chole bhature" core/account-inferrence-map))))
