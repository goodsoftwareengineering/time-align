(ns time-align.core
  (:require [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [ajax.core :as http]
            [time-align.ajax :as ajax]
            [time-align.routing :as routing]
            [time-align.view :as view]
            [time-align.subscriptions]
            [time-align.handlers]
            [time-align.db :as db]))


(rf/reg-event-fx
  ::load-about-page
  (constantly nil))

(kf/reg-controller
  ::about-controller
  {:params (constantly true)
   :start  [::load-about-page]})

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(kf/reg-chain
  ::load-home-page
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (http/raw-response-format)
                  :on-failure      [:common/set-error]}})
  (fn [{:keys [db]} [_ docs]]
    {:db (assoc db :docs docs)}))

(kf/reg-controller
  ::home-controller
  {:params (constantly true)
   :start  [::load-home-page]})

(kf/reg-controller
 ::bucket-edit-controller
 {:params (fn [route-data]
            (when (-> route-data :data :name (= :bucket-edit))
              (-> route-data
                  :path-params
                  :id
                  uuid)))

  :start (fn [ctx id]
           [:load-bucket-form id])})

;; -------------------------
;; Initialize app
(defn get-width []
  (-> js/document (aget "documentElement") (aget "clientWidth")))

(.addEventListener js/window "resize"
                   #(rf/dispatch [:set-width (get-width)]))
(rf/dispatch [:set-width (get-width)])

(defn mount-components []
  (rf/clear-subscription-cache!)
  (kf/start! {:debug?         true
              :routes         routing/routes
              :hash-routing?  true
              :initial-db     db/app-db
              :root-component [view/root-component]}))

(defn init! []
  (ajax/load-interceptors!)
  (mount-components))
