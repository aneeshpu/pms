(ns pms.views
  (:use [hiccup core page]
        ring.velocity.core))

(defn index-page []
  (render "index.vm" :name "Doctor"))
