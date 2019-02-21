(ns time-align.mui
  (:require [cljsjs.material-ui]
            [camel-snake-kebab.core :as csk]
            [reagent.core :as r]))

;; TODO make a macro for these
;; https://learnxinyminutes.com/docs/clojure-macros/
(def app-bar (r/adapt-react-class (aget js/MaterialUI "AppBar")))
(def tool-bar (r/adapt-react-class (aget js/MaterialUI "Toolbar")))
(def typography (r/adapt-react-class (aget js/MaterialUI "Typography")))
(def paper (r/adapt-react-class (aget js/MaterialUI "Paper")))
(def card (r/adapt-react-class (aget js/MaterialUI "Card")))
(def button (r/adapt-react-class (aget js/MaterialUI "Button")))
(def icon-button (r/adapt-react-class (aget js/MaterialUI "IconButton")))
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
(def link (r/adapt-react-class (aget js/MaterialUI "Link")))
(def list-element (r/adapt-react-class (aget js/MaterialUI "List")))
(def list-item (r/adapt-react-class (aget js/MaterialUI "ListItem")))
(def list-item-text (r/adapt-react-class (aget js/MaterialUI "ListItemText")))
(def list-item-avatar (r/adapt-react-class (aget js/MaterialUI "ListItemAvatar")))
(def list-item-secondary-action (r/adapt-react-class (aget js/MaterialUI "ListItemSecondaryAction")))
(def avatar (r/adapt-react-class (aget js/MaterialUI "Avatar")))
(def grid (r/adapt-react-class (aget js/MaterialUI "Grid")))
(def text-field (r/adapt-react-class (aget js/MaterialUI "TextField")))
(def mui-colors (aget js/MaterialUI "colors"))
(def standard-colors (->> mui-colors
                          (js->clj)
                          (map (fn [[color-name variants]]
                                 {:name color-name
                                  :hex  (get variants "500")}))))
