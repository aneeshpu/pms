(ns pms.db.mongo
  (:require [monger.core :as monger]
            [monger.collection :as monger-coll]
            [monger.query :as monger-q]
            [monger.operators :refer :all])
  (:import [org.bson.types ObjectId]))

(defn connect-db
  []
  (monger/connect!)
  (monger/set-db! (monger/get-db "pms")))

(connect-db)

(defn insert
  [documents & rest]
    (let [p (assoc (apply hash-map rest) :id (ObjectId.))]
      (monger-coll/insert documents p)
      p))

(defn get-patient-by-id
  "Retrieves a single patient by id"
  [documents id]
  (monger-coll/find-one-as-map documents {:id (ObjectId. id)}))

(defn get-patient-by-name
  "Retrieves a sequence of patients by name"
  [documents name]
  (monger-coll/find-maps documents {:name {$regex name $options "i"}}))

(defn update [documents id complaint]
  "Updates the complaints array of a patient"
  (let [patient (monger-coll/find-one-as-map documents {:id (ObjectId. id)})
        new-complaint-id (.toString (ObjectId.))
        c {:complaint complaint :id new-complaint-id}]

    (->> (assoc (:complaints patient) new-complaint-id c)
         (assoc patient :complaints)
         (monger-coll/update documents {:id (ObjectId. id)}))

    {:patient patient :complaint c}))

(defn update-patient [documents id patient]
  (monger-coll/update documents {:id (ObjectId. id)} patient)
  patient)

(defn get-patients [index]
  (monger-q/with-collection "patients"
    (monger-q/sort (array-map :date -1))
    (monger-q/paginate :page (Integer/parseInt index) :per-page 5)))
