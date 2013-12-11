(ns pms.handler
  (:use compojure.core
        ;;pms.views
        pms.controller.patient)

  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/patient" [] (create "Captain America" 120))
  (POST "/wtf" {params :params} (new-patient params))
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (handler/site app-routes))