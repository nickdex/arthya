(ns in.arthya.util.date-test
  (:require
   [in.arthya.util.date :refer [find-months-in-string
                                fix-month]]
   [clojure.test :refer [deftest is]]))

(deftest find-months-in-string-test
  (is (= nil (find-months-in-string "23-09-10")))
  (is (= "JAN" (find-months-in-string "10-JAN-23")))
  (is (= "Sep" (find-months-in-string "23-Sep-10")))
  (is (= "feb" (find-months-in-string "23/feb/10"))))

(deftest fix-month-test
  (is (= "2024/Feb/01" (fix-month "2024/feb/01")))
  (is (= "10-Sept-23" (fix-month "10-SEP-23")))
  (is (= "10-Jan-23" (fix-month "10-JAN-23"))))
