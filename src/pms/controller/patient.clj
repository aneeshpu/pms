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
  {:body params})

(defn replace-object-id-with-string
  [obj]
  (assoc (dissoc obj :_id ) :id (.toString (:id obj))))

(defn strip-ids
  [patient]
  (replace-object-id-with-string patient))

(defn retrieve-patient
  [name]
  (println "get-patient " name)
  (let [patients (patient/retrieve name)]
    (println "------------>Inside get-patient with .toString. Found patients " patients)
    {:body (map replace-object-id-with-string patients)}))

(defn add-problem
  [complaint]
  (pms-mongo/update "patients" (:id complaint) (:complaint complaint)))

(defn add-session-to-complaint [complaint session]
  (assoc
    complaint
    :sessions (conj (:sessions complaint) {:date (java.util.Date.) :diagnosis (:diagnosis session) :medicine (:medicine session)})))

(defn associate-complaint-with-session [p complaint session]
  (let [c (add-session-to-complaint complaint session)]
    (println "complaint after adding session" c)
    (assoc (:complaints p) (keyword (:id complaint)) (add-session-to-complaint complaint session))))

(defn add-session
  [session]
  (println "------------------------------params" session)
  (println "add-session with patient-id " (:id session) ", complaint-id " (:complaint-id session) ", diagnosis" (:diagnosis session) ", medicine:" (:medicine session))
  (let [p (pms-mongo/get-patient-by-id "patients" (:id session))]
    (let [c (patient/find-complaint (:complaint-id session) p)]
      (println "------------->Found-complaint:" c)
      (let [complaints (associate-complaint-with-session p c session)]
        (println "Complaints after associating complaint with session" complaints)
        (pms-mongo/update-patient "patients" (:id session) (assoc p :complaints (associate-complaint-with-session p c session)))))))