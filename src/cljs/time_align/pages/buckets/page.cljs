(ns time-align.pages.buckets.page
  (:require [time-align.mui :as mui]
            [time-align.components.misc :refer [fa-icon]]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn bucket-item [bucket]
  [mui/list-item
   [mui/list-item-avatar [mui/avatar
                          [fa-icon {:color (:color bucket)} "fab" "fa-bitbucket"]]]
   [mui/list-item-text {:primary   (:label bucket)
                        :secondary (str "last edited: " (:last-edited bucket))
                        ;; Why doesn't as-element work?
                        ;; (r/as-element
                        ;;  #([mui/typography
                        ;;     (str "last edited: " (str (:last-edited bucket)))]))
                        }]])

(defn root []
  [mui/grid {:container true
             :justify   "center"}
    [mui/paper
     (when-let [buckets @(rf/subscribe [:buckets])]
      [mui/list-element
       (->> buckets
            (map bucket-item))])]])
