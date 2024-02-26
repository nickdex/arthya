 (ns in.arthya.oauth.core
   (:require
    [cheshire.core :as json]
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [in.arthya.oauth.credentials :as creds]
    [in.arthya.oauth.capture-redirect :as ocr]))

(def *secret
  (atom {}))

(def *scopes
  (atom []))

(def *credentials-cache
  (atom nil))

(defn fetch-credentials [user]
  (or (get @*credentials-cache user)
      (let [credentials-file (io/file "tokens" (str user ".edn"))]
        (when (.exists credentials-file)
          (edn/read-string (slurp credentials-file))))))

(defn save-credentials [user new-credentials]
  (when (not= @*credentials-cache new-credentials)
    (swap! *credentials-cache assoc user new-credentials)
    (spit (io/file (doto (io/file "tokens") (.mkdirs))
                   (str user ".edn"))
          new-credentials)))

(def *fetch (atom fetch-credentials))
(def *save (atom save-credentials))

(defn auth!
  ([] (auth! "user"))
  ([user]
   (let [credentials (@*fetch user)
         new-credentials (ocr/update-credentials @*secret credentials @*scopes nil)]
     (@*save user new-credentials)
     (creds/auth-header new-credentials))))

(defn init!
  ([]
   (init! ["https://www.googleapis.com/auth/gmail.readonly"]))
  ([scopes]
   (init! (merge
            ;; If you download secret.json or service.json from your Google Console,
            ;; do not add them to source control.
           (let [secret-file (io/file "secret.json")]
             (when (.exists secret-file)
               (first (vals (json/parse-string (slurp secret-file) true)))))
           (let [service-file (io/file "service.json")]
             (when (.exists service-file)
               (json/parse-string (slurp service-file) true))))
          scopes))
  ([config scopes]
   (init! config scopes fetch-credentials save-credentials))
  ([config scopes fetch-credentials save-credentials]
   (reset! *secret config)
   (reset! *scopes scopes)
   (reset! *fetch fetch-credentials)
   (reset! *save save-credentials)))
