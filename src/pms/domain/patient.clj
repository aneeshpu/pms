(ns pms.domain.patient
  (:require [pms.db.mongo :as pms-mongo])

  (:import [org.bson.types ObjectId]))


(defn save
  [& {:keys [name age]}]
  (println "inside patient/save")
  (pms-mongo/insert "patients" :name name :age age))

(defn retrieve "Retrieves a patient by ObjectId"
  [id]
  (let [p (pms-mongo/retrieve "patients" id)]
    (println "inside pms.domain.patient.retrieve" p)
    p))

(defn find-complaint
  [complaint-id patient]
  (filter
    #(=
       (ObjectId. complaint-id)
       (:id %))
    (:complaints patient)))


