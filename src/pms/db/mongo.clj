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

(defn get-next-patient-id
  []
  (monger-coll/find-and-modify "counters" {} {:$inc {:seq 1}} :return-new true))

(defn- new-patient
  [patient]
  (-> (assoc patient :id (ObjectId.))
      (assoc :pid (:seq (get-next-patient-id)))))

(defn insert
  [documents & rest]
    (println "-----------inside the brand new insert" rest)
    (let [p (new-patient (apply hash-map rest))]
      (println "this is p" p)
      (monger-coll/insert documents p)
      p))

(defn get-patient-by-id
  "Retrieves a single patient by id"
  [documents id]
  (monger-coll/find-one-as-map documents {:id (ObjectId. id)}))

(defn get-patient-by-name
  "Retrieves a sequence of patients by name"
  [documents name]
  (monger-coll/find-maps documents {:name name}))

(defn update 
  [documents id complaint]
  "Updates the complaints array of a patient"
  (let [patient (monger-coll/find-one-as-map documents {:id (ObjectId. id)})
        new-complaint-id (.toString (ObjectId.))
        c {:complaint complaint :id new-complaint-id}]

    (->> (assoc (:complaints patient) new-complaint-id c)
         (assoc patient :complaints)
         (monger-coll/update documents {:id (ObjectId. id)}))

    {:patient patient :complaint c}))

(defn update-patient 
  [documents id patient]
  (monger-coll/update documents {:id (ObjectId. id)} patient)
  patient)

(defn get-patients 
  [index]
  (monger-q/with-collection "patients"
    (monger-q/sort (array-map :name 1 :date -1))
    (monger-q/paginate :page (Integer/parseInt index) :per-page 5)))
