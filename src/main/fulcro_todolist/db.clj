(ns fulcro-todolist.db)

(def list-table
  (atom
   {:friends {:list/id     :friends
              :list/label  "Friends"
              :list/people [1 2]}
    :enemies {:list/id     :enemies
              :list/label  "Enemies"
              :list/people [4 3]}}))

(comment
  @list-table)
