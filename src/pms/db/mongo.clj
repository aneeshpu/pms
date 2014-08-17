(ns pms.db.mongo
  (:require [monger.core :as monger]
            [monger.collection :as monger-coll]
            [monger.query :as monger-q])
  (:import [org.bson.types ObjectId]))

(defn connect-db
  []
  (monger/connect!)
  (monger/set-db! (monger/get-db "pms")))

(connect-db)

(defn insert
  [documents & rest]
  ;(connect-db)
    (let [p (assoc (apply hash-map rest) :id (ObjectId.))]
      (monger-coll/insert documents p)
      p))

(defn get-patient-by-id
  "Retrieves a single patient by id"
  [documents id]
  ;(connect-db)
  (monger-coll/find-one-as-map documents {:id (ObjectId. id)}))

(defn get-patient-by-name
  "Retrieves a sequence of patients by name"
  [documents name]
  ;(connect-db)
  (println "inside retrieve" name)
  (monger-coll/find-maps documents {:name name}))

(defn update [documents id complaint]
  ;(connect-db)
  (let [p (monger-coll/find-one-as-map documents {:id (ObjectId. id)})
        new-complaint-id (.toString (ObjectId.))
        c {:complaint complaint :id new-complaint-id}]

    (println "+++++++" p)
      (monger-coll/update documents {:id (ObjectId. id)}
        (assoc p :complaints
          (assoc (:complaints p) new-complaint-id c)))
    {:patient p :complaint c}))


(defn update-patient [documents id patient]
  ;(connect-db)
  (monger-coll/update documents {:id (ObjectId. id)} patient)
  patient)

(defn get-patients [index]
  ;(connect-db)
  (monger-q/with-collection "patients"
    (monger-q/sort (array-map :date -1))
    (monger-q/paginate :page (Integer/parseInt index) :per-page 5)))
