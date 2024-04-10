(ns in.arthya.desktop.core
  (:require
   [cljfx.api :as fx]
   [clojure.edn :as edn]
   [clojure.set :as set]
   [clojure.string :as str]
   [datascript.core :as ds]
   [datascript.core :as d]
   [in.arthya.account-parser.interface :as parser]
   [in.arthya.database.interface :as db]
   [in.arthya.hledger.interface :as hledger]
   [in.arthya.inference-engine.interface :as ie])
  (:import
   [javafx.event ActionEvent]
   [javafx.scene Node]
   [javafx.stage FileChooser]))

(def *state
  (atom {:query "[:find [?t ...] \n :where [?t :date ?d]\n]"
         :entities nil
         :account nil
         :payee nil
         :accounts #{"Expenses:Education:Books"
                     "Expenses:Education"
                     "Expenses:Entertainment:Movies"
                     "Expenses:Food:Beverages"
                     "Expenses:Food:Groceries"
                     "Expenses:Food"
                     "Expenses:Health:Eye"
                     "Expenses:Health:Medicine"
                     "Expenses:Health"
                     "Expenses:Miscellaneous:AOL"
                     "Expenses:Miscellaneous:Business"
                     "Expenses:Miscellaneous"
                     "Expenses:Travel:Fuel"
                     "Expenses:Travel"
                     "Expenses:Utilities:Clothes"
                     "Expenses:Utilities:Digital"
                     "Expenses:Utilities:Housekeeping"
                     "Expenses:Utilities:Rent"
                     "Expenses:Utilities:Water"
                     "Expenses:Utilities"
                     "Income:Dividends"
                     "Income:Interest"
                     "Income:Salary:AOLD"
                     "Liabilities:Bike:Triumph-Speed-400"}
         :scratch "[(pull ?t [* {:postings [*]}]) ...] "}))

(def *edits (atom {}))
(def *debug (atom nil))


(:payee @*state)
(:query @*state)
(:entities @*state)
(:accounts @*state)
@*state
@*edits
@*debug

(def conn (ds/conn-from-db (db/disk-db "temp/db/icici-monthly.edn")))

;; (db/persist! "temp/db/icici-monthly.edn" @conn)
(->> (:postings (first (:entities @*state)))
     (map ds/touch))

(swap! *state update :accounts set/union
       (set
        (db/q '[:find [?a ...]
                :where [_ :account ?a]]
              @conn)))

(->> (:entities @*state)
     (filter #(= 1 (count (:postings %))))
     ;; (map #(assoc % :memo (str/join "\n" (:memo %))))
     #_(remove #(nil? (get-in % [:postings 0 :account]))))

(defn entity->map [entity] (into {:db/id (:db/id entity)} entity))

(->
 (d/entity @conn 68)
 entity->map
 (update :memo str/join)
 #_ie/infer-account)

(defmulti handle ::event)

(defn ->ledger-records [entities]
  (->> entities
       (map hledger/->hledger-entry)
       (str/join "\n\n")))

(defn ->entities [records]
  (if-not (vector? records)
    "Needs sequence of records"
    (try
      (->> records
           (map #(ds/entity @conn %)))
      (catch Exception e
        (str e)))))

(defn query-result []
  (db/q (-> @*state
            :query
            edn/read-string)
        @conn))

(defn update-records! []
  (->> (query-result)
       ->entities
       (sort-by :date)
       ;; (filter #(= 1 (count (:postings %))))
      ;; (take 1)
      ;;  (take 20)
       ;; ->ledger-records
       ;; (str/join "\n\n")
       (swap! *state assoc :entities)))

(update-records!)

(defmethod handle ::run [{:keys [^ActionEvent fx/event]}]
  (update-records!))

(defmethod handle ::save-all [{:keys [^ActionEvent fx/event]}]
  (->> @*edits
       (map (fn [[k v]]
              (merge {:db/id k} v)))
       (db/transact! conn ))
  (reset! *edits {})
  (update-records!))

(defmethod handle ::infer-all [{:keys [^ActionEvent fx/event]}]
  (let [payee-updates (->> (:entities @*state)
                          (filter #(nil? (:payee %)))
                          (map (fn [e]
                                 {:payee (-> e
                                             entity->map
                                             (update :memo str/join)
                                             ie/infer-payee)
                                  :db/id (:db/id e)}))
                          (remove #(nil? (:payee %))))
        account-updates (->> (:entities @*state)
                             (filter #(= 1 (count (:postings %))))
                             (map (fn [e]
                                    (merge
                                     {:postings [{:account (-> e
                                                               entity->map
                                                               (update :memo str/join)
                                                               ie/infer-account)}]
                                      :db/id (:db/id e)}
                                     #_(select-keys e [:date :payee :quantity :account]))))
                             (remove #(nil? (get-in % [:postings 0 :account]))))
        updates (concat payee-updates account-updates)]
    (reset! *debug updates)
    (db/transact! conn updates))
  (update-records!))

(defmethod handle ::payee [{:keys [^ActionEvent fx/event]}]
  (->> (query-result)
       ->entities
       (map (fn [e]
              {:db/id (:db/id e)
               :payee (:payee @*state)}))
       (db/transact! conn))
  (update-records!))

(defmethod handle ::account [{:keys [^ActionEvent fx/event]}]
  (->> (query-result)
       ->entities
       (map (fn [e]
              {:db/id (:db/id e)
               :postings [{:account (:account @*state)}]}))
       (db/transact! conn))
  (update-records!))

(defmethod handle ::add-posting
  [{:keys [^ActionEvent fx/event
           id]}]
  (db/transact! conn [{:db/id id
                       :postings [{:account "Unknown"}]}])
  (update-records!))

(defn update-txn-field [id val field]
  (swap! *edits (fn [atom-val & _]
                  (if (empty? val)
                    (update-in atom-val [id] dissoc field)
                    (assoc-in atom-val [id field] val)))))

(defn posting-view [ {:keys [posting accounts]} ]
  (let [account (:account posting)
        id (:db/id posting)
        quantity (:quantity posting)
        commodity (:commodity posting)]
    {:fx/type :h-box
     :spacing 10
     :children [{:fx/type :text-field
                 :max-width 80
                 :editable false
                 :text (str id)}
                {:fx/type :combo-box
                 :on-value-changed #(update-txn-field id % :account)
                 :editable true
                 ;; :on-text-changed #(update-txn-field id % :account)
                 :items accounts
                 :value account}
                {:fx/type :text-field
                 :prompt-text "Quantity"
                 :text (str quantity)}
                {:fx/type :text-field
                 :prompt-text "Commodity"
                 :text commodity}]}))

(defn transaction-view [{:keys [entity accounts]} ]
  (let [date (:date entity)
        payee (:payee entity)
        memo  (:memo entity)
        postings (:postings entity)
        id (:db/id entity)]
    {:fx/type :v-box
     :style {:-fx-border-style :solid
             :-fx-border-color :black}
     :spacing 10
     :padding 10
     :children
     [{:fx/type :h-box
       :spacing 10
       :children
       [{:fx/type :text-field
         :max-width 60
         :editable false
         :text (str id)}
        {:fx/type :text-field
         :prompt-text "Date"
         :text date}
        {:fx/type :text-field
         :prompt-text "Payee"
         :on-text-changed #(update-txn-field id % :payee)
         :text payee}
        {:fx/type :button
         :text "+"
         :on-action {::event ::add-posting
                     :id id}}
        {:fx/type :text-field
         :h-box/hgrow :always
         :prompt-text "Memo"
         :text (str/join "\n" memo)}]}
      {:fx/type :v-box
       :spacing 5
       :children
       (for [p postings]
         {:fx/type posting-view
          :posting    p
          :accounts accounts})
       }]}))

(defn query-console-tab [{:keys [query entities scratch accounts]}]
  {:fx/type :tab
   :text "Query Console"
   :content
   {:fx/type :h-box
    :padding 10
    :spacing 10
    :children [{:fx/type :v-box
                              ;; :padding 30
                :spacing 10
                :children [{:fx/type :text-area
                            :min-width 50
                            :max-width 250
                            :on-text-changed #(swap! *state assoc :query %)
                            :editable true
                            :text query}
                           {:fx/type :h-box
                            :spacing 5
                            :children [{:fx/type :button
                                        :text "Run"
                                        :on-action {::event ::run}}
                                       {:fx/type :button
                                        :text "Infer All"
                                        :on-action {::event ::infer-all}}
                                       {:fx/type :button
                                        :text "Save All"
                                        :on-action {::event ::save-all}}]}
                           {:fx/type :text-field
                            :on-text-changed #(swap! *state assoc :payee %)
                            :editable true}
                           {:fx/type :button
                            :text "Rename Payee"
                            :on-action {::event ::payee}}
                           {:fx/type :text-field
                            :on-text-changed #(swap! *state assoc :account %)
                            :editable true}
                           {:fx/type :button
                            :text "Add Account"
                            :on-action {::event ::account}}
                           {:fx/type :text-area
                            :max-width 250
                            :editable true
                            :text scratch}]}
               {:fx/type :scroll-pane
                :h-box/hgrow :always
                ;; :fit-to-height true
                :fit-to-width true
                :content
                {:fx/type :v-box
                 :spacing 5
                 :children
                 (for [txn entities]
                   {:fx/type transaction-view
                    :entity txn
                    :accounts accounts})}}]}})

(defn root-view [{:keys [query entities accounts]}]
  {:fx/type :stage
   :title "aaramb"
   :showing true
   :width 800
   :height 600
   :scene {:fx/type :scene
           :root {:fx/type :tab-pane
                  :tabs [{:fx/type query-console-tab
                          :query query
                          :entities entities
                          :accounts accounts}]}}})

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

(comment
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

  (defn file-transform-scene [files]
    {:fx/type :scene
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
                        (map tab-view files)}]}}))
