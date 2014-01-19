(ns pms.controller.patient
  (:use ring.velocity.core)
  (:require [pms.domain.patient :as patient]
            [pms.db.mongo :as pms-mongo]))

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

(defn retrieve-patient
  [id]
  (println "get-patient " id)
  (let [{:keys [name age id]} (patient/retrieve id)]
    (format "-->Inside get-patient with .toString. Found patient with name:%s and age:%s" name age)
    {:body {:name name :age age :id (.toString id)}}))

(defn add-problem
  [complaint]
  (pms-mongo/update "patients" (:id complaint) (:complaint complaint)))