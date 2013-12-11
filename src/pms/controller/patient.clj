(ns pms.controller.patient
  (:use ring.velocity.core))

(defn create
  [name age]
  (render "index.vm" :name name :age age))
