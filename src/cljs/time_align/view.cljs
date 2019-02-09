(ns time-align.view
  (:require [baking-soda.core :as b]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [reagent.core :as r]
            [camel-snake-kebab.core :as csk]
            [cljsjs.material-ui]
            [re-frame.core :as rf]))

;; TODO make a macro for these
;; https://learnxinyminutes.com/docs/clojure-macros/
(def app-bar (r/adapt-react-class (aget js/MaterialUI "AppBar")))
(def tool-bar (r/adapt-react-class (aget js/MaterialUI "Toolbar")))
(def typography (r/adapt-react-class (aget js/MaterialUI "Typography")))
(def paper (r/adapt-react-class (aget js/MaterialUI "Paper")))
(def card (r/adapt-react-class (aget js/MaterialUI "Card")))
(def button (r/adapt-react-class (aget js/MaterialUI "Button")))
(def mui-theme-provider (r/adapt-react-class (aget js/MaterialUI "MuiThemeProvider")))
(def create-mui-theme (aget js/MaterialUI "createMuiTheme"))
(def purple (-> js/MaterialUI (aget "colors") (aget "purple")))
(def blue (-> js/MaterialUI (aget "colors") (aget "blue")))
(def white (-> js/MaterialUI (aget "colors") (aget "white")))
(def theme-options-clj {:palette {:primary blue
                                  :type "dark"}})
(def theme-options (clj->js theme-options-clj
                            {:keyword-fn #(-> % csk/->camelCase name)}))

(def theme (create-mui-theme theme-options))
(def css-baseline (r/adapt-react-class (aget js/MaterialUI "CssBaseline")))

(defn navbar []
  [app-bar {:position "static"
            :color "primary"}
   [tool-bar [typography {:variant "h4"
                          :color white}
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
  [paper
   (when-let [buckets @(rf/subscribe [:buckets])]
     [typography {:color "accent"} (str "number of buckets: " (count buckets))])])

(defn root-component []
  [mui-theme-provider {:theme theme}
   [css-baseline] ;; this sets the body background
   [:div
    [navbar]
    [kf/switch-route (fn [route] (get-in route [:data :name]))
     :home    home-page
     :about   about-page
     :buckets buckets-page
     nil [:div ""]]]])
