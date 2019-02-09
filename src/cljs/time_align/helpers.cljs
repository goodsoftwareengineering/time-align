(ns time-align.helpers
  (:require
   [time-align.db :as db :refer [period-data-spec]]
   [zprint.core :refer [zprint]]))

(defn print-data [data]
  (with-out-str (zprint data 40)))

(defn clean-period [period]
  (select-keys period (keys period-data-spec)))

(defn same-day? [date-a date-b]
  (and (= (.getFullYear date-a)
          (.getFullYear date-b))
       (= (.getMonth date-a)
          (.getMonth date-b))
       (= (.getDate date-a)
          (.getDate date-b))))

