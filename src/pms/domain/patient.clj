(ns pms.domain.patient
  (:require [pms.db.mongo :as pms-mongo])

  (:import [org.bson.types ObjectId]))


(defn save
  [& {:keys [name age address]}]
  (println "inside patient/save")
  (pms-mongo/insert "patients" :name name :age age :address address))

(defn retrieve "Retrieves a patient by name"
  [name]
  (let [p (pms-mongo/get-patient-by-name "patients" name)]
    (println "inside pms.domain.patient.retrieve" p)
    p))

(defn find-complaint
  "Finds a complaint that matches complaint-id"
  [complaint-id patient]
  (get (:complaints patient) (keyword complaint-id)))


