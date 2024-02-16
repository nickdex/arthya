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

(deftest fix-date-test
  (is (= (util/fix-date "11/09/2024")
         "2024/09/11"))
  (is (= (util/fix-date "11/01/2024")
         "2024/01/11"))
  (is (= (util/fix-date "2 Jan 2024" {:input "d MMM yyyy"})
         "2024/01/02"))
  (is (= (util/fix-date "2 Sep 2024" {:input "d MMM yyyy"})
         "2024/09/02")))
