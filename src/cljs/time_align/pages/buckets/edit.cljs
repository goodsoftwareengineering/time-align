(ns time-align.pages.buckets.edit
  (:require [re-frame.core :as rf]))


(defn root []
  [:div
   (when-let [bucket-form @(rf/subscribe [:bucket-form])]
     (:label bucket-form))])
