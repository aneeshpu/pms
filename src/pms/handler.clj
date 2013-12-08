(ns pms.handler
  (:use compojure.core
        pms.views
        pms.controller.patient)

  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] (create "Captain America" 120))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
