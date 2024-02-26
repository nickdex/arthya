(ns in.arthya.gmail.core
  (:require
   [happygapi.gmail.users :as gmail-api]
   [in.arthya.util.interface :as util]
   [in.arthya.oauth.interface :as oauth]
   [in.arthya.file.interface :as file]))

(defn search
  "Use gmail app query language to find the relevant emails"
  ([] (search nil))
  ([{:keys [query max-results]}]
   (gmail-api/messages-list$
    (oauth/auth!)
    (merge (util/create-map [:q :maxResults]
                            [query max-results])
           {:userId "me"}))))

(defn get-message
  [id]
  (gmail-api/messages-get$ (oauth/auth!)
                           {:userId "me"
                            :id id}))

(defn save-attachment!
  [{:keys [message-id id file-path]}]
  (->
   (gmail-api/messages-attachments-get$ (oauth/auth!)
                                        {:userId "me"
                                         :messageId message-id
                                         :id id})
   :data
   (file/base64url->file! file-path)))
