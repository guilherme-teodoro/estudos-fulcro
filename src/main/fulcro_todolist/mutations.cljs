(ns fulcro-todolist.mutations
  (:require
   [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defmutation friend->enemy
  [{person-id :person/id
    list-id :list/id}]
  (action [{:keys [state]}]
    (swap! state update :list/id #(-> %
                                    (merge/remove-ident* [:person/id person-id] [:friends :list/people])
                                    (targeting/integrate-ident* [:person/id person-id] :append [:enemies :list/people]))))
  (remote [_] true))

(defmutation delete-person
  [{person-id :person/id
    list-id :list/id}]
  (action [{:keys [state]}]
            (swap! state merge/remove-ident* [:person/id person-id] [:list/id list-id :list/people]))
          #_(swap! state update-in [:list/id list-id :list/people] (fn [list]
                                                                   (into [] (remove #(= % ) list)))))
