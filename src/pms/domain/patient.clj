(ns pms.domain.patient
  (:require [pms.db.mongo :as pms-mongo]))


(defn save
  [& {:keys [name age]}]
  (println "inside patient/save")
  (pms-mongo/connect-db)
  (pms-mongo/insert "patients" :name name :age age))