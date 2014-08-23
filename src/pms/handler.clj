(ns pms.handler
  (:use compojure.core)

  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [pms.middleware :as pms-middleware]
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
    (pms-middleware/remove-object-id)
    (middleware/wrap-json-params)
    (pms-middleware/handle-exception)
    (middleware/wrap-json-response)))
