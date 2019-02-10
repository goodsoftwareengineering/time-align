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
(def drawer (r/adapt-react-class (aget js/MaterialUI "Drawer")))
(def swipeable-drawer (r/adapt-react-class (aget js/MaterialUI "SwipeableDrawer")))
(def hidden (r/adapt-react-class (aget js/MaterialUI "Hidden")))
(def menu-icon (r/adapt-react-class (aget js/MaterialUI "Menu")))



(def mobile-drawer (r/atom false))

(defn fa-icon [class style]
  [:i {:class ["fas" class]
       :style style}])

(defn navbar []
  [app-bar {:position "static"
            :color    "primary"}
   [tool-bar
    [button {:on-click #(swap! mobile-drawer not)
             :style {:background-color (-> theme
                                           (aget "palette")
                                           (aget "primary")
                                           (aget "dark"))}
             :variant  "contained"}
     [fa-icon "fa-bars" {:font-size    "2em"
                         :color  (-> theme
                                     (aget "palette")
                                     (aget "common")
                                     (aget "white"))}]]
    [typography {:variant "h4"
                 :style {:margin-left "1em"}}
     "Time Align"]]])

(defn drawer-content []
  [typography "I'm the drawer :)"])

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(defn home-page []
  [paper
   (let [t-shirt @(rf/subscribe [:get-width-t-shirt])
         pixels  @(rf/subscribe [:get-width-pixels])]
     [typography (str t-shirt " : " pixels)])])

(defn buckets-page []
  [paper
   (when-let [buckets @(rf/subscribe [:buckets])]
     [typography (str "number of buckets: " (count buckets))])])

(defn root-component []
  [mui-theme-provider {:theme theme}
   [css-baseline] ;; this sets the body background
   [swipeable-drawer {:open     @mobile-drawer
                      :on-close #(reset! mobile-drawer false)
                      :on-open  #(reset! mobile-drawer true)}
    [drawer-content]]
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :home    home-page
    :about   about-page
    :buckets buckets-page
    nil [:div ""]]])

