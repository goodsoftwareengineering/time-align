(ns time-align.pages.buckets.list
  (:require [time-align.mui :as mui]
            [time-align.components.misc :refer [fa-icon]]
            [re-frame.core :as rf]
            [kee-frame.core :as kf]
            [oops.core :refer [oget oset! ocall oapply ocall! oapply!
                               oget+ oset!+ ocall+ oapply+ ocall!+ oapply!+]]
            [reagent.core :as r]))

(defn bucket-item [bucket]
  [mui/list-item {:key (str (:id bucket))}
   [mui/list-item-avatar [mui/avatar
                          [fa-icon {:color (:color bucket)} "fab" "fa-bitbucket"]]]
   [mui/list-item-text {:style     {:margin-right "4em"}
                        :primary   (:label bucket)
                        :secondary (r/as-element
                                    [mui/typography
                                     (str "edited: "
                                          (.toLocaleDateString
                                           (:last-edited bucket))
                                          " "
                                          (.toLocaleTimeString
                                           (:last-edited bucket)))])}]
   [mui/list-item-secondary-action
    [mui/icon-button
     {:href (kf/path-for [:bucket-edit {:id (:id bucket)}])}
     [fa-icon {} "fas" "fa-edit"]]]])

(defn root []
  [mui/grid {:container   true
             :justify     "center"
             :align-items "center"
             :direction   "column"
             :spacing     16}

   [mui/grid {:item true}
    [mui/paper
     (when-let [buckets @(rf/subscribe [:buckets])]
       [mui/list-element {:style {:width "100%"}}
        (->> buckets
             (map bucket-item))])]]

   [mui/grid {:item true}
    [mui/paper
     [mui/icon-button {:on-click #(js/alert "add something")}
      [fa-icon {} "fas" "fa-plus"]]]]])
