(ns pms.handler
  (:use compojure.core)

  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as response]
            [pms.controller.patient :as p-cont]
            [ring.middleware.json :as middleware]))

(defroutes app-routes
  (GET "/" [] (p-cont/welcome))
  (GET "/patient/:name" [name] (p-cont/retrieve-patient name))
  (POST "/patients" {params :params} (p-cont/new-patient params))
  (POST "/patients/:id/cases" {params :params} (p-cont/add-problem params))
  (POST "/patients/:id/cases/:complaint-id" {params :params} (p-cont/add-session params))

  (route/resources "/")
  (GET ["/:filename" :filename #".*"] [filename]
    (response/file-response filename {:root "./public"}))
  (route/not-found "Not Found"))

(defn handle-exception
  [handler]
  (fn [request]
    (try (let [res (handler request)]
      res)
      (catch IllegalArgumentException e
        {:body {:message (.getMessage e)}
         :status 500}
        ))))

(def app
  (->
    (handler/site app-routes)
    (middleware/wrap-json-params)
    (handle-exception)
    (middleware/wrap-json-response)))