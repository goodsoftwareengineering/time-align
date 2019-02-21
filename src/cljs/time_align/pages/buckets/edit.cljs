(ns time-align.pages.buckets.edit
  (:require [re-frame.core :as rf]
            [time-align.mui :as mui]
            [time-align.components.color :refer [color-picker]]))


(defn root []
  [mui/grid {:container   true
             :justify     "center"
             :align-items "center"
             :direction   "column"
             :spacing     16
             :style       {:flex-grow "1"}}

   [mui/grid {:item true
              :xs   12}
    [mui/paper {:style {:padding "1em"
                        :width   "100%"}}
     (when-let [bucket-form @(rf/subscribe [:bucket-form])]
       [mui/grid {:container   true
                  :justify     "left"
                  :align-items "center"
                  :direction   "column"}
        [mui/text-field {:id     "bucket-id"
                         :label  "ID"
                         :value  (:id bucket-form)
                         :margin "normal" }]
        [mui/text-field {:id     "bucket-label"
                         :label  "Label"
                         :value  (:label bucket-form)
                         :margin "normal" }]
        [color-picker]]

       )]]])

;; {:id          uuid?
;;  :label       string?
;;  :created     ::moment
;;  :last-edited ::moment
;;  :data        map?
;;  :color       ::color
;;  :templates   (ds/maybe [template-spec])
;;  :periods     (ds/maybe [period-spec])}

;; :on-update (rf/dispatch [:update-bucket-form {:}])
