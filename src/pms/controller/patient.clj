(ns pms.controller.patient
  (:use ring.velocity.core)
  (:require [pms.domain.patient :as patient]))

(defn welcome
  []
  (render "welcome.vm"))

(defn create
  [name age]
  (render "index.vm" :name name :age age))

(defn save-patient
  [& {:keys [name age]}]
  (println "saving patient with name " name " and age " age)
  (patient/save :name name :age age))


(defn new-patient
  [params]
  (println "inside new-patient with params")
  (println params)
  (save-patient :name (:name params) :age (:age params))
  (render "newpatient.vm" :name (:name params) :age (:age params)))
