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
  (GET "/patients/:index" [index] (p-cont/get-all-patients index))
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

(defn remove-object-id [handler]
  (fn [request]
    (let [res (handler request)]
      (if (and ((complement nil?) res) (map? res) (seq? (:body res)))
        (assoc res :body (map #(dissoc % :_id :id) (:body res)))
        res))))

(def app
  (->
    (handler/site app-routes)
    (remove-object-id)
    (middleware/wrap-json-params)
    (handle-exception)
    (middleware/wrap-json-response)))