(ns pms.controller.patient
  (:use ring.velocity.core))

(defn create
  [name age]
  (render "index.vm" :name name :age age))

(defn new-patient
  [params]
  (println "inside new-patient with params")
  (println params)
  (render "newpatient.vm" :name (:name params) :age (:age params)))
