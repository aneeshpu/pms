(ns pms.controller.patient
  (:use ring.velocity.core)
  (:require [pms.domain.patient :as patient]
            [pms.db.mongo :as pms-mongo]))

(defn welcome
  []
  (render "/welcome.vm"))

(defn create
  [name age]
  (render "index.vm" :name name :age age))

(defn save-patient
  [& {:keys [name age address]}]
  (println "saving patient with name " name " and age " age)
  (patient/save :name name :age age :address address))

(defn replace-object-id-with-string
  [obj]
  (assoc (dissoc obj :_id ) :id (.toString (:id obj))))

(defn validate-patient [patient]
  (if (or (nil? (:name patient))
        (nil? (:age patient))
        (nil? (:address patient)))
    (throw (IllegalArgumentException. "Please enter name, age and address"))))

(defn get-all-patients [index]
  (pms-mongo/get-patients index))

(defn new-patient
  [params]
  (println "inside new-patient with params" params)
  (validate-patient params)
  (->> (save-patient :name (:name params) :age (:age params) :address (:address params))
      replace-object-id-with-string
      (hash-map :body)))

(defn strip-ids
  [patient]
  (replace-object-id-with-string patient))

(defn retrieve-patient
  [name]
  (println "get-patient " name)
  (let [patients (patient/retrieve name)]
    (println "------------>Inside get-patient with .toString. Found patients " patients)
    {:body (map replace-object-id-with-string patients)}))

(defn validate-complaint [params]
  (if (nil? (:complaint params))
    (throw (IllegalArgumentException. "Please enter a complaint"))))

(defn add-problem
  [complaint]
  (validate-complaint complaint)
  {:body (let [res (pms-mongo/update "patients" (:id complaint) (:complaint complaint))]
           (replace-object-id-with-string (:complaint res)))})

   (defn add-session-to-complaint [complaint session]
  (assoc
    complaint
    :sessions (conj (:sessions complaint) {:date (java.util.Date.) :diagnosis (:diagnosis session) :medicine (:medicine session)})))

(defn associate-complaint-with-session [p complaint session]
    (->> (add-session-to-complaint complaint session)
         (assoc (:complaints p) (keyword (:id complaint)))))
;    (assoc (:complaints p) (keyword (:id complaint)) (add-session-to-complaint complaint session)))

(defn validate-session [session]
  (println (or (nil? (:diagnosis session)) (nil? (:medicine session))))
  (if (or (nil? (:diagnosis session)) (nil? (:medicine session)))
    (throw (IllegalArgumentException. "Either diagnosis or medicine has not been entered"))))

(defn add-session
  [session]
  (println "------------------------------params" session)
  (println "add-session with patient-id " (:id session) ", complaint-id " (:complaint-id session) ", diagnosis" (:diagnosis session) ", medicine:" (:medicine session))
  (validate-session session)
  (let [p (pms-mongo/get-patient-by-id "patients" (:id session))]
    (let [c (patient/find-complaint (:complaint-id session) p)]
      (println "------------->Found-complaint:" c)
      (let [complaints (associate-complaint-with-session p c session)]
        (println "Complaints after associating complaint with session" complaints)
        (->> (associate-complaint-with-session p c session)
             (assoc p :complaints)
             (pms-mongo/update-patient "patients" (:id session))
             strip-ids
             (hash-map :body)))))) 
