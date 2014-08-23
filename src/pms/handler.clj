(ns pms.handler
  (:use compojure.core)

  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as response]
            [pms.controller.patient :as p-cont]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
              [credentials :as creds])
            [ring.middleware.json :as middleware]))

(defroutes app-routes
  (GET "/" [] (friend/authorize #{::user} "" (p-cont/welcome)))
  (GET "/patient/:name" [name] (p-cont/retrieve-patient name))
  (GET "/patients/:index" [index] (p-cont/get-all-patients index))
  (POST "/patients" {params :params} (p-cont/new-patient params))
  (POST "/patients/:id/cases" {params :params} (p-cont/add-problem params))
  (POST "/patients/:id/cases/:complaint-id" {params :params} (p-cont/add-session params))

  (GET "/login" [] (response/redirect "login.html"))
  (route/resources "/")
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

(defn- remove-id-from-body
  [body]
  (map #(dissoc (assoc % :id (.toString (:id %))) :_id) body))

(defn- remove-id-from-response
  [res]
  (->> (:body res)
       (remove-id-from-body)
       (assoc res :body)))

(defn- has-response?
  [res]
  (and ((complement nil?) res) 
       (map? res) 
       (seq? (:body res))))

(defn remove-object-id 
  [handler]
  (fn [request]
    (let [res (handler request)]
      (if (has-response? res) 
        (remove-id-from-response res) 
        res))))


(def users {"root" {:username "root"
                    :password (creds/hash-bcrypt "admin_password")
                    :roles #{::admin}}

            "drkpbal" {:username "drkpbal"
                        :password (creds/hash-bcrypt "password")
                        :roles #{::user}}})

(def app
  (->
    (handler/site (friend/authenticate app-routes {:credential-fn (partial creds/bcrypt-credential-fn users)
                                                   :workflows [(workflows/interactive-form)]}))
    (remove-object-id)
    (middleware/wrap-json-params)
    (handle-exception)
    (middleware/wrap-json-response)))
