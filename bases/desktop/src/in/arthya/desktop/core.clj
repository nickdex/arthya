(ns in.arthya.desktop.core
  (:require
   [cljfx.api :as fx]
   [clojure.string :as str]
   [in.arthya.hledger.interface :as hledger]
   [in.arthya.account-parser.interface :as parser]
   [in.arthya.inference-engine.interface :as ie])
  (:import
   [javafx.event ActionEvent]
   [javafx.scene Node]
   [javafx.stage FileChooser]))

(def *state
  (atom {:files nil}))

@*state

(defmulti handle ::event)

(defn process [file]
  (->>
   (parser/parse file)
   (map hledger/->hledger-transaction)
   (map #(update % :postings conj {:account (ie/infer-account %)}))
   (map hledger/->hledger-entry)
   (str/join "\n\n")))

(defn filename-without-extension [file]
  (let [filename (.getName file)]
    (if (.contains filename ".")
      (subs filename 0 (.lastIndexOf filename "."))
      filename)))

(defmethod handle ::open-file [{:keys [^ActionEvent fx/event]}]
  (let [window (.getWindow (.getScene ^Node (.getTarget event)))
        chooser (doto (FileChooser.)
                  (.setTitle "Open File"))]
    (when-let [files (.showOpenMultipleDialog chooser window)]
      {:state {:selected-files files
               :files (map (fn [file]
                             {:file file
                              :content (process file)
                              :new-name (str (filename-without-extension file) ".ledger")}) files)}})))

(defmethod handle ::save [{:keys []}])

(defn tab-view [{:keys [file new-name content]}]
  {:fx/type :tab
   :text (.getName file)
   :content
   {:fx/type :v-box
    :children [{:fx/type :h-box
                :padding 10
                :spacing 10
                :children [{:fx/type :label
                            :text "New File Name"}
                           {:fx/type :text-field
                            :h-box/hgrow :always
                            :editable true
                            :text new-name}
                           {:fx/type :button
                            :text "Save"
                            :on-action {::event ::save}}]}
               {:fx/type :text-area
                :v-box/vgrow :always
                :editable false
                :text content}]}})

(defn root-view [{:keys [files]}]
  {:fx/type :stage
   :title "aaramb"
   :showing true
   :width 800
   :height 600
   :scene {:fx/type :scene
           :root {:fx/type :v-box
                  :padding 30
                  :spacing 15
                  :children [{:fx/type :h-box
                              :children [{:fx/type :button
                                          :text "Select files..."
                                          :on-action {::event ::open-file}}]}
                             {:fx/type :tab-pane
                              :v-box/vgrow :always
                              :tabs
                              (map tab-view files)}]}}})

(def renderer
  (fx/create-renderer
   :middleware (fx/wrap-map-desc #(root-view %))
   :opts {:fx.opt/map-event-handler
          (-> handle
              (fx/wrap-co-effects {:state (fx/make-deref-co-effect *state)})
              (fx/wrap-effects {:state (fx/make-reset-effect *state)
                                :dispatch fx/dispatch-effect}))}))

(defn start! []
  (fx/mount-renderer *state renderer))
