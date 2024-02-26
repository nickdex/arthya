(ns in.arthya.oauth.interface
  (:require [in.arthya.oauth.core :as core]))

(defn auth!
  "Authenticates current user with OAuth Client ID generated by Google.
  It saves the OAuth Access Token in memory for current session and in file for later use.
  Returns Authorization headers to be used in subsequent HTTP Requests to use Google API"
  []
  (when (empty? @core/*secret)
    (core/init!))
  (core/auth!))
