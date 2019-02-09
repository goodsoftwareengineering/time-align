(ns time-align.view
  (:require [baking-soda.core :as b]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [reagent.core :as r]
            [cljsjs.material-ui]
            [re-frame.core :as rf]))


(def app-bar (r/adapt-react-class (aget js/MaterialUI "AppBar")))
(def tool-bar (r/adapt-react-class (aget js/MaterialUI "Toolbar")))
(def typography (r/adapt-react-class (aget js/MaterialUI "Typography")))
(def paper (r/adapt-react-class (aget js/MaterialUI "Paper")))
(def card (r/adapt-react-class (aget js/MaterialUI "Card")))
(def button (r/adapt-react-class (aget js/MaterialUI "Button")))

(defn navbar []
  [app-bar {:position "static"
            :color "primary"}
   [tool-bar [typography {:variant "h6"
                          :color "secondary"}
              "Time Align"]]])

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(defn home-page []
  [:div.container
   (when-let [docs @(rf/subscribe [:docs])]
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])

(defn buckets-page []
  [:div
   (when-let [buckets @(rf/subscribe [:buckets])]
     [:div (str "number of buckets: " (count buckets))])])

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :home    home-page
    :about   about-page
    :buckets buckets-page
    nil [:div ""]]])
