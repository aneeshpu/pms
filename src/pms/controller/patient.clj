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

(defn add-session
  [session]
  (println "------------------------------params" session)
  (println "add-session with patient-id " (:id session) ", complaint-id " (:complaint-id session) ", diagnosis" (:diagnosis session) ", medicine:" (:medicine session))
  (let [p (pms-mongo/get-patient-by-id "patients" (:id session))]
    (let [c (patient/find-complaint (:complaint-id session) p)]
      (println "-------------__>Found-complaint:" c)
      (pms-mongo/update
        "patients"
        (:id session)
        (assoc c :sessions
          (conj
            (:sessions c)
            {:date (java.util.Date.) :diagnosis (:diagnosis session) :medicine (:medicine session)})))))
  )