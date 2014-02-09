(ns pms.db.mongo
  (:require [monger.core :as monger]
            [monger.collection :as monger-coll])
  (:import [org.bson.types ObjectId]))

(defn connect-db
  []
  (monger/connect!)
  (monger/set-db! (monger/get-db "pms")))

(defn insert
  [documents & rest]
  (connect-db)
  (monger-coll/insert documents
    (assoc (apply hash-map rest)
      :id (ObjectId.))))

(defn retrieve "Retrieves a mongo object by Id"
  [documents name]
  (connect-db)
  (println "inside retrieve" name)
  (monger-coll/find-maps documents {:name name}))

(defn update [documents id complaint]
  (connect-db)
  (let [p (monger-coll/find-one-as-map documents {:id (ObjectId. id)})]
    (println "+++++++" p)
    (monger-coll/update documents {:id (ObjectId. id)}
      (assoc p :complaints
        (conj
          (:complaints p)
          {:complaint complaint :id (ObjectId.)})))
    p))

