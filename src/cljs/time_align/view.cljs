(ns time-align.view
  (:require [baking-soda.core :as b]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [reagent.core :as r]
            [time-align.mui :as mui]
            [time-align.pages.buckets.page :as buckets-page]
            [time-align.components.misc :refer [fa-icon]]
            [re-frame.core :as rf]))

(def mobile-drawer (r/atom false))

(defn navbar []
  [mui/app-bar {:position "static"
            :color    "primary"}
   [mui/tool-bar
    [mui/button {:on-click #(swap! mobile-drawer not)
                 :style {:background-color (-> mui/theme
                                           (aget "palette")
                                           (aget "primary")
                                           (aget "dark"))}
             :variant  "contained"}
     [fa-icon {:font-size    "2em"
               :color  (-> mui/theme
                           (aget "palette")
                           (aget "common")
                           (aget "white"))}
      "fas" "fa-bars"]]
    [mui/typography {:variant "h4"
                 :style {:margin-left "1em"}}
     "Time Align"]]])

(defn drawer-content []
  (let [container-style {:display        "flex"
                         :flex-direction "row"
                         :align-items    "center"
                         :margin         "1em"}
        icon-style      {:margin           "0.25em"
                         :font-size        "2em"
                         :background-color (-> mui/theme
                                               (aget "palette")
                                               (aget "primary"))
                         :color            (-> mui/theme
                                               (aget "palette")
                                               (aget "common")
                                               (aget "white"))}]

    [mui/link {:href (kf/path-for [:buckets])}
     [:div {:style container-style}
      [fa-icon icon-style "fab" "fa-bitbucket"]
      [mui/typography "yo"]]]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(defn home-page []
  [mui/paper
   (let [t-shirt @(rf/subscribe [:get-width-t-shirt])
         pixels  @(rf/subscribe [:get-width-pixels])]
     [mui/typography (str t-shirt " : " pixels)])])

(defn root-component []
  [mui/mui-theme-provider {:theme mui/theme}
   [mui/css-baseline] ;; this sets the body background
   [mui/swipeable-drawer {:open @mobile-drawer
                          :on-close #(reset! mobile-drawer false)
                          :on-open  #(reset! mobile-drawer true)}
    [drawer-content]]
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :home    home-page
    :about   about-page
    :buckets buckets-page/root
    nil [:div ""]]])

