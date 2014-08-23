(ns pms.middleware)

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
