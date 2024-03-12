(ns in.arthya.inference-engine.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [in.arthya.inference-engine.core :as core]))

(deftest infer-payee-test
  (is (= "Amazon"
         (core/first-matched-val ["amazon"]
                                 core/payee-inferrence-map)))
  (is (= "Swiggy"
         (core/infer-payee {:memo "swiggy"})))

  (is (= nil
         (core/infer-payee {:memo ";d"}))))

(deftest infer-account-test
  (is (= "Expenses:Travel:Fuel"
         (core/infer-account {:payee "shell"})))
  (is (= "Expenses:Food"
         (core/first-matched-val ["Unknown" "pav bhaji"] core/account-inferrence-map)))
  (is (= "Expenses:Food"
         (core/first-matched-val ["chole bhature"] core/account-inferrence-map))))
