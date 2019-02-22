(ns time-align.components.color
  (:require [time-align.mui :as mui]
            [time-align.components.misc :refer [fa-icon]]))

(defn color-picker-dialog [{:keys [open on-close-fn on-select-fn]}]
  [mui/dialog {:open     open
               :on-close on-close-fn}
   [mui/grid {:container true
              :justify "flex-start"}
    (->> mui/standard-colors
         (map (fn [color]
                [mui/grid {:item true}
                 [mui/icon-button {:on-click (partial on-select-fn (:hex color))}
                  [fa-icon {:color     (:hex color)
                            :font-size "4em"} "fa" "fa-square"]]])))]])

