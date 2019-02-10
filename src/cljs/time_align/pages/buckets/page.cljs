(ns time-align.pages.buckets.page
  (:require [time-align.mui :as mui]
            [re-frame.core :as rf]))

(defn root []
  [mui/paper
   (when-let [buckets @(rf/subscribe [:buckets])]
     [mui/typography (str "number of buckets: " (count buckets))])])
