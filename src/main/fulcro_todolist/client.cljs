(ns fulcro-todolist.client
  (:require
    [fulcro-todolist.application :refer [app]]
    [fulcro-todolist.ui :as ui]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.data-fetch :as df]))

(defn ^:export init []
  (app/mount! app ui/Root "app")
  (df/load! app :friends ui/PersonList)
  (df/load! app :enemies ui/PersonList)
  (df/load! app :people/types nil)
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app ui/Root "app")
  (js/console.log "Hot reload"))

(comment
  (fdn/db->tree [{:friends [:list/label]}] (comp/get-initial-state Root {}) {}) )


(comment
  (js/alert "olar")
  (aset js/window "nome" "lucas")

  (take 10 (range)))
