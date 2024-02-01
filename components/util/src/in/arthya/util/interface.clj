(ns in.arthya.util.interface
  (:require [clojure.string :as str]))

(defn includes-any?
  "Return true if any of the strings matches the attribute, false otherwise.
   In case attribute is nil, return false"
  [attribute strings]
  (if (nil? attribute)
    false
    (let [lower-category (str/lower-case attribute)]
      (some #(str/includes? lower-category (str/lower-case %)) strings))))

(defn contains-partial?
  "Return true if any of rows partially contains the search string"
  [rows search-str]
  (some #(includes-any? % [search-str]) rows))
