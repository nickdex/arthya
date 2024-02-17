(ns in.arthya.payee.interface-test
  (:require [clojure.test :as test :refer [deftest is]]
            [in.arthya.payee.interface :as payee]))

(deftest infer-test
  (is (= "Cafe Vishala"
         (payee/infer {:memo "8555202565\nCAFE VISHALA, BANGALORE, IN"}))))
