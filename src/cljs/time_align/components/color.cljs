(ns time-align.components.color
  (:require [time-align.mui :as mui]
            [time-align.components.misc :refer [fa-icon]]))

(defn color-picker []
  [mui/grid {:container true
             :direction "row"
             :spacing   10}
   (->> mui/standard-colors
        (map (fn [color]
               [mui/grid {:item true}
                [fa-icon {:color     (:hex color)
                          :font-size "4em"} "fa" "fa-square"]])))])

