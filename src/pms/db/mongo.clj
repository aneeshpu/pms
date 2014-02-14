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

(defn get-patient-by-id
  "Retrieves a single patient by id"
  [documents id]
  (connect-db)
  (println "new version")
  (monger-coll/find-one-as-map documents {:id (ObjectId. id)}))

(defn get-patient-by-name
  "Retrieves a sequence of patients by name"
  [documents name]
  (connect-db)
  (println "inside retrieve" name)
  (monger-coll/find-maps documents {:name name}))

(defn update [documents id complaint]
  (connect-db)
  (let [p (monger-coll/find-one-as-map documents {:id (ObjectId. id)})]
    (println "+++++++" p)
    (monger-coll/update documents {:id (ObjectId. id)}
      (assoc
        p
        :complaints
        (let [id (.toString (ObjectId.))]
          (assoc
            (:complaints p)
            id
            {:complaint complaint :id id})))) p))


(defn update-patient [documents id p]
  (connect-db)
  (monger-coll/update documents {:id (ObjectId. id)} p))