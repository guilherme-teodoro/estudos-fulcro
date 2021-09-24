(ns fulcro-todolist.client
 (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
    [com.fulcrologic.fulcro.networking.http-remote :as http]
    [com.fulcrologic.fulcro.dom :as dom]))

(defonce app (app/fulcro-app {:remotes {:remote (http/fulcro-http-remote {})}}))

(defsc Person [this {:person/keys [name age id] :as props} {:keys [onDelete]}]
  {:query [:person/id :person/age :person/name]
   :ident :person/id
   :initial-state (fn [{:keys [name age id]}]
                    {:person/name name :person/age age :person/id id})}
  (dom/li
   (dom/h5 (str name " (age: " age ")"))
   (dom/button {:onClick #(onDelete id)} "Mover para enemies")))

(def ui-person (comp/factory Person {:keyfn :person/name}))

(defmutation friend->enemy
  [{person-id :person/id
    list-id :list/id}]
  (action [{:keys [state]}]
    (swap! state update :list/id #(-> %
                                    (merge/remove-ident* [:person/id person-id] [:friends :list/people])
                                    (targeting/integrate-ident* [:person/id person-id] :append [:enemies :list/people])))))

(defmutation delete-person
  [{person-id :person/id
    list-id :list/id}]
  (action [{:keys [state]}]
            (swap! state merge/remove-ident* [:person/id person-id] [:list/id list-id :list/people]))
          #_(swap! state update-in [:list/id list-id :list/people] (fn [list]
                                                                   (into [] (remove #(= % ) list)))))

(defsc PersonList [this {:list/keys [label people id] :as props}]
 {:query [:list/id :list/label {:list/people (comp/get-query Person)}]
  :ident :list/id
  :initial-state (fn [{:keys [label id]}]
                   {:list/label label
                    :list/id id
                    :list/people (if (= label "Friends")
                                   [(comp/get-initial-state Person {:id 1 :name "Sally" :age 32})
                                    (comp/get-initial-state Person {:id 2 :name "Joe" :age 22})]
                                   [(comp/get-initial-state Person {:id 3 :name "Fred" :age 11})
                                    (comp/get-initial-state Person {:id 4 :name "Bobby" :age 55})])})}
  (dom/div
    (dom/h4 label)
    (dom/ul
     (map #(ui-person (comp/computed % {:onDelete (fn [person-id]
                                                    (comp/transact! this [(friend->enemy {:list/id id
                                                                                          :person/id person-id})]))}))
          people))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [this {:keys [friends enemies] :as props}]
  {:query [{:friends (comp/get-query PersonList)}
           {:enemies (comp/get-query PersonList)}]
   :initial-state (fn [_]
                    {:friends (comp/get-initial-state PersonList {:id :friends :label "Friends"})
                     :enemies (comp/get-initial-state PersonList {:id :enemies :label "Enemies"})})}
  (dom/div
      (ui-person-list friends)
      (ui-person-list enemies)))

(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/mount! app Root "app")
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app Root "app")
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload"))

(comment
  (fdn/db->tree [{:friends [:list/label]}] (comp/get-initial-state Root {}) {}) )


(comment
  (js/alert "olar")
  (aset js/window "nome" "lucas")

  (take 10 (range)))
