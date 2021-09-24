(ns fulcro-todolist.parser
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))

(pc/defresolver person-resolver [_ {:person/keys [id]}]
  {::pc/input #{:person/id}
   ::pc/output [:person/name :person/age :person/id :person/created-at]}
  {:person/name (str "filipe muito universo " id) :person/age 33 :person/id id :person/created-at #inst "2020-01-01"})

(pc/defresolver all-kind-of-people-resolver [_ _]
  {::pc/output [:people/types]}
  {:people/types ["legal" "chata" "muito legal" "muito chata"]})

(def resolvers [person-resolver all-kind-of-people-resolver])

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
