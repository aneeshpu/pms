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
  (let [patients (patient/retrieve name)]
    {:body (map replace-object-id-with-string patients)}))

(defn validate-complaint [params]
  (if (nil? (:complaint params))
    (throw (IllegalArgumentException. "Please enter a complaint"))))

(defn add-problem
  [complaint]
  (validate-complaint complaint)
  (->> (pms-mongo/update "patients" (:id complaint) (:complaint complaint))
       :complaint
       replace-object-id-with-string
       (hash-map :body)))

(defn add-session-to-complaint 
  [complaint session]

  (->> {:date (java.util.Date.) :diagnosis (:diagnosis session) :medicine (:medicine session)}
       (conj (:sessions complaint))
       (assoc complaint :sessions)))

(defn associate-complaint-with-session 
  [p complaint session]
    (->> (add-session-to-complaint complaint session)
         (assoc (:complaints p) (keyword (:id complaint)))))

(defn validate-session [session]
  (println (or (nil? (:diagnosis session)) (nil? (:medicine session))))
  (if (or (nil? (:diagnosis session)) (nil? (:medicine session)))
    (throw (IllegalArgumentException. "Either diagnosis or medicine has not been entered"))))

(defn add-session
  [session]
  (validate-session session)
  (let [p (pms-mongo/get-patient-by-id "patients" (:id session))]
    (let [c (patient/find-complaint (:complaint-id session) p)]
        (->> (associate-complaint-with-session p c session)
             (assoc p :complaints)
             (pms-mongo/update-patient "patients" (:id session))
             strip-ids
             (hash-map :body)))))
