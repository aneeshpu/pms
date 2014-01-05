(ns pms.domain.patient
  (:require [pms.db.mongo :as pms-mongo]))


(defn save
  [& {:keys [name age]}]
  (println "inside patient/save")
  (pms-mongo/insert "patients" :name name :age age))

(defn retrieve "Retrieves a patient by ObjectId"
  [id]
  (let [p (pms-mongo/retrieve "patients" id)]
    (println "inside pms.domain.patient.retrieve" p)
    p))