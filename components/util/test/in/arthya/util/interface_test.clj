(ns in.arthya.util.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.util.interface :as util]))

(deftest parse-currency-from-string-test
  (is (= nil
         (util/parse-currency "")))
  (is (= nil
         (util/parse-currency nil)))
  (is (= 0.0
         (util/parse-currency 0.0)))
  (is (= -9988.0
         (util/parse-currency -9988)))
  (is (= 129.50
         (util/parse-currency 129.50)))
  (is (= -9988.0
         (util/parse-currency "-9988.0")))
  (is (= 26.89
         (util/parse-currency "26.89 Dr.")))
  (is (= 1.80
         (util/parse-currency "1.80 Cr.")))
  (is (= 199.0
         (util/parse-currency "199.00 Dr.")))
  (is (= 11211.33
         (util/parse-currency "11,211.33 Dr.")))
  (is (= 11211.33
         (util/parse-currency "11,211.33 Cr."))))

(deftest fix-date-test
  (is (= "2024/09/11"
         (util/fix-date "11/09/2024")))
  (is (= "2024/01/11"
         (util/fix-date "11/01/2024")))
  (is (= "2024/01/01"
         (util/fix-date "01-JAN-2024" {:input "dd-MMM-yyyy"})))
  (is (= "2024/01/02"
         (util/fix-date "2 Jan 2024" {:input "d MMM yyyy"})))
  (is (= "2024/09/02"
         (util/fix-date "2 Sep 2024" {:input "d MMM yyyy"}))))

(deftest create-map-test
  (is (= {:a 1.01, :b 2}
         (util/create-map [:a :b]
                          [1.01 2])))
  (is (= {:b 2}
         (util/create-map [:a :b]
                          [nil 2])))
  (is (= {:b 2}
         (util/create-map [:a :b]
                          ['() 2])))
  (is (= {:b 2}
         (util/create-map [:a :b]
                          ['[] 2])))
  (is (= {:b 2}
         (util/create-map [:a :b]
                          ["" 2]))))
