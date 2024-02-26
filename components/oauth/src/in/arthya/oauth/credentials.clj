(ns in.arthya.oauth.credentials
  "Helpers for getting an OAuth 2.0 token.
  See https://developers.google.com/identity/protocols/OAuth2WebServer"
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [clojure.set :as set]
            [buddy.sign.jwt :as jwt]
            [buddy.core.keys :as keys])
  (:import (java.util Date)))

(defn- redirect-to-google
  "Step 2: Redirect to Google's OAuth 2.0 server.
  Builds the URL to send the user to for them to authorize your app.
  Do not call this function directly, it is called from set-authorization-parameters (step 1).
  For local testing you can paste this URL into your browser,
  or call (clojure.java.browse/browse-url (redirect-to-google my-config)).
  In your app you need to send your user to this URL, usually with a redirect response."
  [{:as config :keys [client_id redirect_uri redirect_uris]} scopes {:as optional :keys [state login_hint]}]
  {:pre [client_id (or redirect_uri redirect_uris) scopes]}
  (str "https://accounts.google.com/o/oauth2/v2/auth"
       "?client_id=" client_id
       "&state=" state
       "&response_type=code"
       "&access_type=offline"
       "&scope=" (str/join "%20" scopes)
       "&redirect_uri=" (or redirect_uri (last redirect_uris))))

(defn set-authorization-parameters
  "Step 1: Set authorization parameters.
  We tell Google to expect a visit from our user.
  Returns the URL to redirect the user to."
  ([config scopes] (set-authorization-parameters config scopes nil))
  ([{:as config :keys [client_id redirect_uri redirect_uris]} scopes {:as optional :keys [state login_hint]}]
   {:pre [client_id (or redirect_uri redirect_uris) scopes]}
   (assert
    (= 200
       (:status
        (http/get "https://accounts.google.com/o/oauth2/v2/auth"
                  {:query-params (merge {:client_id     client_id
                                         :redirect_uri  (or redirect_uri (last redirect_uris))
                                         :response_type "code"
                                         :scope         (str/join " " scopes)}
                                        optional)}))))
   (redirect-to-google config scopes optional)))

(defn with-timestamp
  "Google won't give us the time of day, so let's check our clock."
  [{:as credentials :keys [expires_in]}]
  {:pre [(int? expires_in)]}
  (assoc credentials
         :expires_at (Date. ^long (+ (* expires_in 1000) (System/currentTimeMillis)))))

;; Step 3: Google prompts user for consent.
;; Sit back and wait.
;; There should be a route in your app to handle the redirect from Google (see step 4).
;; happy.oauth2-capture-redirect shows how you could do this,
;; and is useful if you don't want to run a server.

(defn exchange-code
  "Step 5: Exchange authorization code for refresh and access tokens.
  When the user is redirected back to your app from Google with a short lived code,
  exchange the code for a long lived access token."
  [{:keys [client_id client_secret redirect_uri redirect_uris refresh_token]} code]
  {:pre [client_id client_secret (or redirect_uri redirect_uris) code]}
  (with-timestamp
    (:body (http/post "https://oauth2.googleapis.com/token"
                      {:form-params {:client_id     client_id
                                     :client_secret client_secret
                                     :code          code
                                     :grant_type    "authorization_code"
                                     :redirect_uri  (or redirect_uri (last redirect_uris))}
                       :accept      :json
                       :as          :json}))))

(defn redirect-from-google
  "Step 4: Handle the OAuth 2.0 server response.
  In your app server, create a route `:redirect-uri` receives the `code` parameter."
  [config {{:keys [code]} :params}]
  (exchange-code config code))

(defn refresh-credentials
  "Given a config map, and a credentials map containing either a refresh_token or private_key,
  fetches a new access token.
  Returns the response if successful, which is a map of credentials containing an access token.
  Refresh tokens eventually expire, and attempts to refresh will fail with 401.
  Therefore, calls that could cause a refresh should catch 401 exceptions,
  call set-authorization-parameters and redirect."
  [{:as config :keys [client_id client_secret client_email private_key]} scopes {:as credentials :keys [refresh_token]}]
  {:pre [(or (and client_id client_secret refresh_token)
             (and client_email private_key))]}
  (let [now (quot (.getTime (Date.)) 1000)
        params (cond private_key
                     {:grant_type "urn:ietf:params:oauth:grant-type:jwt-bearer"
                      :assertion  (jwt/sign
                                   {:iss   client_email,
                                    :scope (str/join " " scopes),
                                    :aud   "https://oauth2.googleapis.com/token",
                                    :exp   (+ now 3600)
                                    :iat   now}
                                   (keys/str->private-key private_key)
                                   {:alg    :rs256
                                    :header {:alg "RS256"
                                             :typ "JWT"}})}

                     refresh_token
                     {:client_id     client_id
                      :client_secret client_secret
                      :grant_type    "refresh_token"
                      :refresh_token refresh_token})]
    (with-timestamp
      (:body (http/post "https://oauth2.googleapis.com/token"
                        {:form-params params
                         :accept      :json
                         :as          :json})))))

(defn revoke-token
  "Given a credentials map containing either an access token or refresh token, revokes them."
  [{:keys [access_token refresh_token]}]
  {:pre [(or access_token refresh_token)]}
  (http/post "https://oauth2.googleapis.com/revoke"
             {:accept      :json
              :as          :json
              :form-params {"token" (or access_token refresh_token)}}))

(defn valid? [{:as credentials :keys [expires_at access_token]}]
  (boolean
   (and access_token expires_at
        (neg? (.compareTo (Date.) expires_at)))))

(defn refreshable? [{:as config :keys [private_key]} {:as credentials :keys [refresh_token]}]
  (boolean (or refresh_token private_key)))

(defn credential-scopes [credentials]
  (when (:scope credentials)
    (str/split (:scope credentials) #" ")))

(defn has-scopes? [credentials scopes]
  ;; TODO: scopes have a hierarchy
  (set/subset? (set scopes) (set (credential-scopes credentials))))

(defn auth-header
  "Given credentials, returns request header suitable for merging into a request."
  [{:as credentials :keys [access_token]}]
  {:pre [access_token]}
  {:headers {"Authorization" (str "Bearer " access_token)}})
