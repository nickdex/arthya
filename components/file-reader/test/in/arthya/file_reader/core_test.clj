(ns in.arthya.file-reader.core-test
  (:require [clojure.test :refer [deftest is]]
            [in.arthya.file-reader.core :as core]
            [in.arthya.util.interface :as util]))

(deftest trim-no-opts-test
  (is (= (core/trim-rows
          '('(nil nil nil)
            '("34"
              "27/01/2024"
              "27/01/2024"
              "-"
              "UPI/439344021256/Deposit 1/9131161006@payt/State Bank Of I/ACD01HN4QG9TS2X69VSC"
              15000.0
              0.0
              5705.94)
            '("35"
              "27/01/2024"
              "27/01/2024"
              "-"
              "UPI/402784415982/bananas/q692910553@ybl/Yes Bank Ltd/ACD01HN53S95N29BXYZ70W0XRY"
              30.0
              0.0
              5675.94)
            '("Legends used in" "something")
            '("Legends used in" "something")))
         '('(nil nil nil)
           '("34"
             "27/01/2024"
             "27/01/2024"
             "-"
             "UPI/439344021256/Deposit 1/9131161006@payt/State Bank Of I/ACD01HN4QG9TS2X69VSC"
             15000.0
             0.0
             5705.94)
           '("35"
             "27/01/2024"
             "27/01/2024"
             "-"
             "UPI/402784415982/bananas/q692910553@ybl/Yes Bank Ltd/ACD01HN53S95N29BXYZ70W0XRY"
             30.0
             0.0
             5675.94)
           '("Legends used in" "something")
           '("Legends used in" "something")))))

(deftest trim-terminate-pred-test
  (is (= (core/trim-rows
          {:terminate-pred #(not (util/contains-partial? % "legends"))}
          '('(nil nil nil)
            '("34"
              "27/01/2024"
              "27/01/2024"
              "-"
              "UPI/439344021256/Deposit 1/9131161006@payt/State Bank Of I/ACD01HN4QG9TS2X69VSC"
              15000.0
              0.0
              5705.94)
            '("35"
              "27/01/2024"
              "27/01/2024"
              "-"
              "UPI/402784415982/bananas/q692910553@ybl/Yes Bank Ltd/ACD01HN53S95N29BXYZ70W0XRY"
              30.0
              0.0
              5675.94)
            '("Legends used in" "something")
            '("Legends used in" "something")))
         '('(nil nil nil)
           '("34"
             "27/01/2024"
             "27/01/2024"
             "-"
             "UPI/439344021256/Deposit 1/9131161006@payt/State Bank Of I/ACD01HN4QG9TS2X69VSC"
             15000.0
             0.0
             5705.94)
           '("35"
             "27/01/2024"
             "27/01/2024"
             "-"
             "UPI/402784415982/bananas/q692910553@ybl/Yes Bank Ltd/ACD01HN53S95N29BXYZ70W0XRY"
             30.0
             0.0
             5675.94)))))

(deftest trim-skip-start-test
  (is (= (core/trim-rows
          {:skip-start 1}
          '('(nil nil nil)
            '("34"
              "27/01/2024"
              "27/01/2024"
              "-"
              "UPI/439344021256/Deposit 1/9131161006@payt/State Bank Of I/ACD01HN4QG9TS2X69VSC"
              15000.0
              0.0
              5705.94)
            '("35"
              "27/01/2024"
              "27/01/2024"
              "-"
              "UPI/402784415982/bananas/q692910553@ybl/Yes Bank Ltd/ACD01HN53S95N29BXYZ70W0XRY"
              30.0
              0.0
              5675.94)
            '("Legends used in" "something")
            '("Legends used in" "something")))
         '('("34"
             "27/01/2024"
             "27/01/2024"
             "-"
             "UPI/439344021256/Deposit 1/9131161006@payt/State Bank Of I/ACD01HN4QG9TS2X69VSC"
             15000.0
             0.0
             5705.94)
           '("35"
             "27/01/2024"
             "27/01/2024"
             "-"
             "UPI/402784415982/bananas/q692910553@ybl/Yes Bank Ltd/ACD01HN53S95N29BXYZ70W0XRY"
             30.0
             0.0
             5675.94)
           '("Legends used in" "something")
           '("Legends used in" "something")))))
