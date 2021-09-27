(ns fulcro-todolist.ui
 (:require
  ["date-fns" :as dateFns]
  [fulcro-todolist.mutations :as api]
  [com.fulcrologic.fulcro.application :as app]
  [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
  [com.fulcrologic.fulcro.networking.http-remote :as http]
  [com.fulcrologic.fulcro.dom :as dom]))

(defonce app-db (app/fulcro-app {:remotes {:remote (http/fulcro-http-remote {})}}))

(defsc Person [this {:person/keys [name age id created-at] :as props} {:keys [onDelete]}]
  {:query [:person/id :person/age :person/name :person/created-at]
   :ident :person/id}
  (dom/li
   (dom/h5 (str name " (age: " age ")"))
   (dom/h6 (dateFns/format created-at "dd/MM/yyyy"))
   (dom/button {:onClick #(onDelete id)} "Mover para enemies")))

(def ui-person (comp/factory Person {:keyfn :person/name}))

(defsc PersonList [this {:list/keys [label people id] :as props}]
 {:query [:list/id :list/label {:list/people (comp/get-query Person)}]
  :ident :list/id}
  (dom/div
    (dom/h4 label)
    (dom/ul
     (map #(ui-person (comp/computed % {:onDelete (fn [person-id]
                                                    (comp/transact! this [(api/friend->enemy {:list/id id
                                                                                              :person/id person-id})]))}))
          people))))

(def ui-person-list (comp/factory PersonList))

(defsc Root [this {:keys [friends enemies] :as props}]
  {:query [{:friends (comp/get-query PersonList)}
           {:enemies (comp/get-query PersonList)}]
   :initial-state {}}
  (dom/div
   (when friends
     (ui-person-list friends))
   (when enemies
     (ui-person-list enemies))))

