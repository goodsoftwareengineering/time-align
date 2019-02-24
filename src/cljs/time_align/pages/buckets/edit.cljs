(ns time-align.pages.buckets.edit
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [time-align.mui :as mui]
            [time-align.components.misc :refer [fa-icon]]
            [time-align.components.color :refer [color-picker-dialog]]))

;; TODO should this be in the url and app-db?
(def color-dialog-open (r/atom false))

(defn root []
  [mui/grid {:container   true
             :justify     "center"
             :align-items "center"
             :direction   "column"
             :spacing     16
             :style       {:flex-grow "1"}}

   [color-picker-dialog {:open         @color-dialog-open
                         :on-close-fn  #(reset! color-dialog-open false)
                         :on-select-fn #(rf/dispatch
                                         [:update-bucket-form {:color %}])}]

   [mui/grid {:item true
              :xs   12}
    [mui/paper {:style {:padding "1em"
                        :width   "100%"}}
     (when-let [bucket-form @(rf/subscribe [:bucket-form])]
       [mui/grid {:container true
                  :justify   "flex-start"
                  :direction "column"}

        [mui/card {:style {:width            "18em"
                           :height           "4em"
                           :background-color (:color bucket-form)}}
         [mui/grid {:container true :justify "flex-end"}
          [mui/grid {:item true}
           [mui/icon-button
            {:on-click #(swap! color-dialog-open (fn [old] (not old)))}
            [fa-icon {:font-size "1.5em"} "fas" "fa-paint-brush"]]]]]

        [mui/text-field {:id     "bucket-id"
                         :label  "ID"
                         :value  (:id bucket-form)
                         :margin "normal"}]
        [mui/text-field {:id     "bucket-label"
                         :label  "Label"
                         :value  (:label bucket-form)
                         :margin

                         "normal"}]])]]])

;; {:id          uuid?
;;  :label       string?
;;  :created     ::moment
;;  :last-edited ::moment
;;  :data        map?
;;  :color       ::color
;;  :templates   (ds/maybe [template-spec])
;;  :periods     (ds/maybe [period-spec])}

;; :on-update (rf/dispatch [:update-bucket-form {:}])
