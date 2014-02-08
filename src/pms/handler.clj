(ns pms.handler
  (:use compojure.core)

  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as response]
            [pms.controller.patient :as p-cont]
            [ring.middleware.json :as middleware]))

(defroutes app-routes
  (GET "/" [] (p-cont/welcome))
  (GET "/patient/:id" [id] (p-cont/retrieve-patient id))
  (POST "/wtf" {params :params} (p-cont/new-patient params))
  (POST "/patients/:id/cases" {params :params} (p-cont/add-problem params))
  (POST "/patients/:id/cases/:complaint-id" {params :params} (p-cont/add-session :id :complaint-id params))

  (route/resources "/")
  (GET ["/:filename" :filename #".*"] [filename]
    (response/file-response filename {:root "./public"}))
  (route/not-found "Not Found"))


(def app
  (->
    (handler/site app-routes)
    (middleware/wrap-json-body)
    (middleware/wrap-json-params)
    (middleware/wrap-json-response)))