(ns pms.domain.patient
  (:require  [monger.core :as monger]
             [monger.collection :as monger-coll])
  (:import [org.bson.types ObjectId]))

(defn save
  [& {:keys [name age]}]
  (println "inside patient/save")
  (monger/connect!)
  (monger/set-db! (monger/get-db "pms"))
  (monger-coll/insert "patients" {:id (ObjectId.) :name name :age age}))