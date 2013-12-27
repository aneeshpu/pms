(ns pms.handler
  (:use compojure.core)

  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as response]
            [pms.controller.patient :as p-cont]))

(defroutes app-routes
  (GET "/" [] (p-cont/welcome))
  (GET "/patient" [] (p-cont/create "Captain America" 120))
  (POST "/wtf" {params :params} (p-cont/new-patient params))
  (route/resources "/")
  (GET ["/:filename" :filename #".*"] [filename]
    (response/file-response filename {:root "./public"}))
  (route/not-found "Not Found"))


(def app
  (handler/site app-routes))