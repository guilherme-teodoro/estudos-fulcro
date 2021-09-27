(ns fulcro-todolist.mutations
  (:require
   [fulcro-todolist.db :as db]
   [com.wsscode.pathom.connect :as pc]
   [taoensso.timbre :as log]))

(pc/defmutation friend->enemy [env {list-id   :list/id
                                    person-id :person/id}]
  {::pc/sym `friend->enemy}
  (swap! db/list-table (fn [table] (-> table
                                       (update-in [:friends :list/people] (fn [o] (filterv #(not= person-id %) o)))
                                       (update-in [:enemies :list/people] conj person-id)))))

(def mutations [friend->enemy])
