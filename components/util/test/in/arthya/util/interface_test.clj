(ns in.arthya.util.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.util.interface :as util]))

(deftest parse-currency-from-string-test
  (is (= (util/parse-currency-from-string "26.89 Dr.")
         26.89))
  (is (= (util/parse-currency-from-string "1.80 Cr.")
         1.80))
  (is (= (util/parse-currency-from-string "199.00 Dr.")
         199.0))
  (is (= (util/parse-currency-from-string "11,211.33 Dr.")
         11211.33))
  (is (= (util/parse-currency-from-string "11,211.33 Cr.")
         11211.33)))
