(ns pms.db.mongo
  (:require [monger.core :as monger]
            [monger.collection :as monger-coll])
  (:import [org.bson.types ObjectId]))

(defn connect-db
  []
  (monger/connect!)
  (monger/set-db! (monger/get-db "pms")))

(defn insert
  [documents & rest]
  (connect-db)
  (monger-coll/insert documents
    (assoc (apply hash-map rest)
      :id (ObjectId.))))