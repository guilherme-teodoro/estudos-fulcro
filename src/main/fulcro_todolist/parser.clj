(ns fulcro-todolist.parser
  (:require
   [fulcro-todolist.mutations]
   [com.wsscode.pathom.core :as p]
   [com.wsscode.pathom.connect :as pc]
   [fulcro-todolist.db :as db]
   [taoensso.timbre :as log]))

(pc/defresolver person-resolver [_ {:person/keys [id]}]
  {::pc/input #{:person/id}
   ::pc/output [:person/name :person/age :person/id :person/created-at]}
  {:person/name (str "filipe " id) :person/age 33 :person/id id :person/created-at #inst "2020-01-01"})

(pc/defresolver list-resolver [_ {:list/keys [id]}]
  {::pc/input #{:list/id}
   ::pc/output [:list/id :list/label {:list/people [:person/id]}]}
  (when-let [list (get @db/list-table id)]
    (assoc list
           :list/people (mapv (fn [id] {:person/id id}) (:list/people list)))))

(pc/defresolver all-kind-of-people-resolver [_ _]
  {::pc/output [:people/types]} {:people/types ["legal" "chata" "muito legal" "muito chata"]})

(pc/defresolver friends-resolver [env input]
  {::pc/output [{:friends [:list/id]}]}
  {:friends {:list/id :friends}})

(pc/defresolver enemies-resolver [env input]
  {::pc/output [{:enemies [:list/id]}]}
  {:enemies {:list/id :enemies}})

(def resolvers [friends-resolver enemies-resolver person-resolver all-kind-of-people-resolver list-resolver fulcro-todolist.mutations/mutations])

(def pathom-parser
  (p/parser {::p/env     {::p/reader                 [p/map-reader
                                                      pc/reader2
                                                      pc/ident-reader
                                                      pc/index-reader]
                          ::pc/mutation-join-globals [:tempids]}
             ::p/mutate  pc/mutate
             ::p/plugins [(pc/connect-plugin {::pc/register resolvers})
                          p/error-handler-plugin
                          ;; or p/elide-special-outputs-plugin
                          (p/post-process-parser-plugin p/elide-not-found)]}))

(defn api-parser [query]
  (log/info "Process" query)
  (pathom-parser {} query))


(comment
  (api-parser [{[:person/id 1] [:person/name :person/age :people/types :person/created-at]}])
  )
