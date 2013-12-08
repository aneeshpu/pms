(ns pms.views
  (:use [hiccup core page]))

(defn index-page []
  (html5
    [:head
     [:title "Hello Doctor"]
     (include-css "/css/style.css")]
    [:body
     [:h1 "Hello Doctor"]]))
