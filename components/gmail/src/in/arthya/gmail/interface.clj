(ns in.arthya.gmail.interface
  (:require [in.arthya.gmail.core :as core]))

(defn search
  "Use gmail app query language to find the relevant emails"
  ([] (search nil))
  ([options]
   (core/search options)))

(defn save-attachment!
  "Downloads the attachments and save it to local file.

  Options -
  - :message-id - Id of the message the attachment is part of"
  [options]
  (core/save-attachment! options))
