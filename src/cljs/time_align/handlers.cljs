(ns time-align.handlers
  (:require
   [cljs.reader :refer [read-string]]
   [re-frame.core :as rf]
   [clojure.spec.alpha :as s]
   [time-align.helpers :as helpers]
   [com.rpl.specter :as sp :refer-macros [select select-one setval transform]]))

(defn load-bucket-form [db [_ bucket-id]]
  (let [bucket      (select-one [:buckets sp/ALL #(= (:id %) bucket-id)] db)
        bucket-form (merge bucket {:data (helpers/print-data (:data bucket))})]
    (assoc-in db [:forms :bucket-form] bucket-form)))

(defn update-bucket-form [db [_ bucket-form]]
  (transform [:forms :bucket-form] #(merge % bucket-form) db))

(defn save-bucket-form [{:keys [db]} [_ date-time]]
  (let [bucket-form (get-in db [:forms :bucket-form])]
    (try
       (let [new-data (read-string (:data bucket-form))
             new-bucket (merge bucket-form {:data new-data
                                       :last-edited date-time})
            new-db (setval [:buckets sp/ALL #(= (:id %) (:id new-bucket))]
                           new-bucket
                           db)]
        {:db new-db
         ;; load bucket form so that the data string gets re-formatted prettier
         :dispatch [:load-bucket-form (:id new-bucket)]})
      (catch js/Error e
        {:db db
         :alert (str "Failed data json validation " e)}))))

(defn load-period-form [db [_ period-id]]
  (let [[sub-bucket period] (select-one
                             [:buckets sp/ALL
                              (sp/collect-one (sp/submap [:id :color :label]))
                              :periods sp/ALL #(= (:id %) period-id)] db)
        sub-bucket-remap    {:bucket-id    (:id sub-bucket)
                             :bucket-color (:color sub-bucket)
                             :bucket-label (:label sub-bucket)}
        period-form         (merge period
                                   {:data (helpers/print-data (:data period))}
                                   sub-bucket-remap)]
    (assoc-in db [:forms :period-form] period-form)))

(defn update-period-form [db [_ period-form]]
  (let [period-form (if (contains? period-form :bucket-id)
                      (merge period-form
                             {:bucket-label (:label
                                             (select-one
                                              [:buckets
                                               sp/ALL
                                               #(= (:id %) (:bucket-id period-form))]
                                              db))})
                      ;; ^ pulls out the label when selecting new parent
                      ;; because all that comes from the picker is id
                      period-form)]
    (transform [:forms :period-form] #(merge % period-form) db)))

(defn save-period-form [{:keys [db]} [_ date-time]]
  (let [period-form (get-in db [:forms :period-form])]
    (try
      (let [new-data          (read-string (:data period-form))
            keys-wanted       (->> period-form
                                   (keys)
                                   ;; TODO use spec to get only keys wanted
                                   (remove #(or (= :bucket-id %)
                                                (= :bucket-label %)
                                                (= :bucket-color %))))
            new-period        (-> period-form
                                  (merge {:data        new-data
                                          :last-edited date-time})
                                  (select-keys keys-wanted))
            [old-bucket
             old-period]      (select-one [:buckets sp/ALL
                                       (sp/collect-one (sp/submap [:id]))
                                       :periods sp/ALL
                                       #(= (:id %) (:id new-period))] db)
            removed-period-db (setval [:buckets sp/ALL
                                       #(= (:id %) (:id old-bucket))
                                       :periods sp/ALL
                                       #(= (:id %) (:id old-period))]
                                      sp/NONE db)
            new-db            (setval [:buckets sp/ALL
                                       ;; TODO should the bucket-id come from period form?
                                       #(= (:id %) (:bucket-id period-form))
                                       :periods
                                       sp/NIL->VECTOR
                                       sp/AFTER-ELEM]
                                      new-period removed-period-db)]

        {:db       new-db
         ;; load period form so that the data string gets re-formatted prettier
         :dispatch [:load-period-form (:id new-period)]})
      (catch js/Error e
        {:db    db
         :alert (str "Failed data json validation " e)}))))

(defn load-template-form [db [_ template-id]]
  (let [[sub-bucket template] (select-one
                             [:buckets sp/ALL
                              (sp/collect-one (sp/submap [:id :color :label]))
                              :templates sp/ALL #(= (:id %) template-id)] db)
        sub-bucket-remap    {:bucket-id    (:id sub-bucket)
                             :bucket-color (:color sub-bucket)
                             :bucket-label (:label sub-bucket)}
        template-form         (merge template
                                   {:data (helpers/print-data (:data template))}
                                   sub-bucket-remap)]
    (assoc-in db [:forms :template-form] template-form)))

(defn update-template-form [db [_ template-form]]
  (let [template-form (if (contains? template-form :bucket-id)
                      (merge template-form
                             {:bucket-label (:label
                                             (select-one
                                              [:buckets
                                               sp/ALL
                                               #(= (:id %) (:bucket-id template-form))]
                                              db))})
                      ;; ^ pulls out the label when selecting new parent
                      ;; because all that comes from the picker is id
                      template-form)]
    (transform [:forms :template-form] #(merge % template-form) db)))

(defn save-template-form [{:keys [db]} [_ date-time]]
  (let [template-form (get-in db [:forms :template-form])]
    (try
      (let [new-data          (read-string (:data template-form))
            keys-wanted       (->> template-form
                                   (keys)
                                   (remove #(or (= :bucket-id %)
                                                (= :bucket-label %)
                                                (= :bucket-color %))))
            new-template        (-> template-form
                                  (merge {:data        new-data
                                          :last-edited date-time})
                                  (select-keys keys-wanted))
            [old-bucket
             old-template]      (select-one [:buckets sp/ALL
                                       (sp/collect-one (sp/submap [:id]))
                                       :templates sp/ALL
                                       #(= (:id %) (:id new-template))] db)
            removed-template-db (setval [:buckets sp/ALL
                                       #(= (:id %) (:id old-bucket))
                                       :templates sp/ALL
                                       #(= (:id %) (:id old-template))]
                                      sp/NONE db)
            new-db            (setval [:buckets sp/ALL
                                       #(= (:id %) (:bucket-id template-form))
                                       :templates
                                       sp/NIL->VECTOR
                                       sp/AFTER-ELEM]
                                      new-template removed-template-db)]

        {:db       new-db
         ;; load template form so that the data string gets re-formatted prettier
         :dispatch [:load-template-form (:id new-template)]})
      (catch js/Error e
        {:db    db
         :alert (str "Failed data read validation " e)}))))

(defn load-filter-form [db [_ filter-id]]
  (let [filter     (select-one
                    [:filters sp/ALL #(= (:id %) filter-id)] db)
        filter-form (merge filter
                           {:predicates (helpers/print-data (:predicates filter))}
                           {:sort (helpers/print-data (:sort filter))})]
    (assoc-in db [:forms :filter-form] filter-form)))

(defn update-filter-form [db [_ filter-form]]
  (transform [:forms :filter-form] #(merge % filter-form) db))

(defn save-filter-form [{:keys [db]} [_ date-time]]
  (let [filter-form (get-in db [:forms :filter-form])]
    (try
      (let [new-predicates {:predicates (read-string (:predicates filter-form))}
            new-sort {:sort (read-string (:sort filter-form))}
            new-filter        (-> filter-form
                                  (merge {:last-edited date-time}
                                         new-predicates
                                         new-sort))
            old-filter        (select-one [:filters sp/ALL
                                           #(= (:id %) (:id new-filter))] db)
            removed-filter-db (setval [:filters sp/ALL
                                       #(= (:id %) (:id old-filter))]
                                      sp/NONE db)
            new-db            (setval [:filters
                                       sp/NIL->VECTOR
                                       sp/AFTER-ELEM]
                                      new-filter removed-filter-db)]

        {:db       new-db
         ;; load filter form so that the data string gets re-formatted prettier
         :dispatch [:load-filter-form (:id new-filter)]})
      (catch js/Error e
        {:db    db
         :alert (str "Failed predicate read validation " e)}))))

(defn update-active-filter [db [_ id]]
  (assoc db :active-filter id))

(defn add-new-bucket [{:keys [db]} [_ {:keys [id now]}]]
  {:db (setval [:buckets
                sp/NIL->VECTOR
                sp/AFTER-ELEM]
               {:id          id
                :label       ""
                :created     now
                :last-edited now
                :data        {}
                :color       "#ff1122"
                :templates   nil
                :periods     nil}
               db)
   :dispatch [:navigate-to {:current-screen :bucket
                            :params {:bucket-id id}}]})

(defn add-new-period [{:keys [db]} [_ {:keys [bucket-id id now]}]]
  {:db (setval [:buckets sp/ALL
                #(= (:id %) bucket-id)
                :periods
                sp/NIL->VECTOR
                sp/AFTER-ELEM]
               {:id id
                :created now
                :last-edited now
                :label ""
                :data {}
                :planned true
                :start now
                :stop (new js/Date (+ (.valueOf now) (* 1000 60)))}
               db)
   :dispatch [:navigate-to {:current-screen :period
                            :params {:period-id id}}]})

(defn add-template-period [{:keys [db]} [_ {:keys [template id now]}]]
  ;; template needs bucket-id
  ;; TODO refactor so that this function takes in a template id (maybe bucket id)
  ;; and then queries the db for the template
  (let [new-data       (merge (:data template)
                              {:template-id (:id template)})
        start-relative (:start template)
        duration       (:duration template)
        start          (if (some? start-relative)
                         (js/Date.
                              (.getFullYear now)
                              (.getMonth now)
                              (.getDate now)
                              (:hour start-relative)
                              (:minute start-relative))
                         now)
        stop           (if (some? duration)
                         (js/Date. (+ (.valueOf start) duration))
                         (js/Date. (+ (.valueOf start) (* 1000 60))))
        period         (merge template
                              {:id    id
                               :data  new-data
                               :created now
                               :last-edited now
                               :start start
                               :stop  stop})
        period-clean   (helpers/clean-period period)]

    {:db       (setval [:buckets sp/ALL
                        #(= (:id %) (:bucket-id template))
                        :periods
                        sp/NIL->VECTOR
                        sp/AFTER-ELEM]
                       period-clean
                       db)
     :dispatch [:navigate-to {:current-screen :period
                              :params         {:period-id id}}]}))

(defn add-new-template [{:keys [db]} [_ {:keys [bucket-id id now]}]]
  {:db       (setval [:buckets sp/ALL
                      #(= (:id %) bucket-id)
                      :templates
                      sp/NIL->VECTOR
                      sp/AFTER-ELEM]
                     {:id          id
                      :created     now
                      :last-edited now
                      :label       ""
                      :data        {}
                      :planned     true
                      :start       {:hour   (.getHours now)
                                    :minute (.getMinutes now)}
                      :stop        {:hour   (.getHours now)
                                    :minute (+ 5 (.getMinutes now))}
                      :duration    nil}
                     db)
   :dispatch [:navigate-to {:current-screen :template
                            :params         {:template-id id}}]})

(defn add-new-filter [{:keys [db]} [_ {:keys [id now]}]]
  {:db (setval [:filters
                sp/NIL->VECTOR
                sp/AFTER-ELEM]
               {:id          id
                :label       ""
                :created     now
                :last-edited now
                :compatible []
                :sort nil
                :predicates []}
               db)
   :dispatch [:navigate-to {:current-screen :filter
                            :params {:filter-id id}}]})

(defn delete-bucket [{:keys [db]} [_ id]]
  {:db (->> db
            (setval [:buckets sp/ALL #(= id (:id %))] sp/NONE)
            (setval [:forms :bucket-form] nil))
   ;; TODO pop stack when possible
   :dispatch [:navigate-to {:current-screen :buckets}]})

(defn delete-period [{:keys [db]} [_ id]]
  {:db (->> db
            (setval [:buckets sp/ALL :periods sp/ALL #(= id (:id %))] sp/NONE)
            (setval [:forms :period-form] nil)
            (setval [:selected-period] nil))
   ;; TODO pop stack when possible
   :dispatch [:navigate-to {:current-screen :periods}]})

(defn delete-template [{:keys [db]} [_ id]]
  {:db (->> db
            (setval [:buckets sp/ALL :templates sp/ALL #(= id (:id %))] sp/NONE)
            (setval [:forms :template-form] nil))
   ;; TODO pop stack when possible
   :dispatch [:navigate-to {:current-screen :templates}]})

(defn delete-filter [{:keys [db]} [_ id]]
  {:db (->> db
            (setval [:filters sp/ALL #(= id (:id %))] sp/NONE)
            (setval [:forms :filter-form] nil))
   ;; TODO pop stack when possible
   :dispatch [:navigate-to {:current-screen :filters}]})

(defn select-period [db [_ id]]
  (assoc-in db [:selected-period] id))

(defn update-period [db [_ {:keys [id update-map]}]]
  (transform [:buckets sp/ALL
              :periods sp/ALL
              #(= id (:id %))]
             #(merge % update-map)
             db))

(defn add-period [db [_ {:keys [period bucket-id]}]]
  (let [random-bucket-id (->> db
                              (select-one [:buckets sp/FIRST])
                              (:id))
        bucket-id (if (some? bucket-id)
                    bucket-id
                    random-bucket-id)]
    (->> db
         (setval [:buckets sp/ALL
                  #(= (:id %) bucket-id)
                  :periods
                  sp/NIL->VECTOR
                  sp/AFTER-ELEM]
                 (helpers/clean-period period)))))

(defn select-next-or-prev-period [db [_ direction]]
  (if-let [selected-period-id (get-in db [:selected-period])]
    (let [displayed-day (get-in db [:time-navigators :day])
          selected-period (select-one [:buckets sp/ALL :periods sp/ALL
                                       #(= selected-period-id (:id %))] db)
          sorted-periods (->> db
                              (select [:buckets sp/ALL :periods sp/ALL])
                              ;; Next period needs to be on this displayed day
                              (filter #(and (some? (:start %))
                                            (some? (:stop %))
                                            (or (helpers/same-day? (:start %) displayed-day)
                                                (helpers/same-day? (:stop %) displayed-day))))
                              ;; Next period needs to be visible on this track
                              (filter #(= (:planned selected-period) (:planned %)))
                              (sort-by #(.valueOf (:start %)))
                              (#(if (= direction :prev)
                                  (reverse %)
                                  %)))
          next-period    (->> sorted-periods
                              ;; Since they are sorted, drop them until you get to
                              ;; the current selected period.
                              ;; Then take the next one.
                              (drop-while #(not (= (:id %) selected-period-id)))
                              (second))]
      (if (some? next-period)
        (assoc-in db [:selected-period] (:id next-period))
        db))
    db))

(defn update-day-time-navigator [db [_ new-date]]
  (assoc-in db [:time-navigators :day] new-date))

(defn tick [db [_ date-time]]
  (let [period-in-play-id (get-in db [:period-in-play-id])]
    ;; Update period in play if there is one
    (-> (if (some? period-in-play-id)
          (transform [:buckets sp/ALL
                      :periods sp/ALL
                      #(= (:id %) period-in-play-id)]

                     #(merge % {:stop date-time})

                     db)
          db)
        ;; update now regardless
        (assoc-in [:now] date-time))))

(defn play-from-period [db [_ {:keys [id time-started new-id]}]]
  (let [[bucket-just-id
         period-to-play-from] (select-one [:buckets sp/ALL
                                           (sp/collect-one (sp/submap [:id]))
                                           :periods sp/ALL
                                           #(= (:id %) id)] db)
        new-period            (merge period-to-play-from
                                     {:id      new-id
                                      :planned false
                                      :start   time-started
                                      :stop    (->> time-started
                                                    (.valueOf)
                                                    (+ 1000)
                                                    (js/Date.))})]
    (->> db
         ;; Add new period
         (setval [:buckets sp/ALL
                  #(= (:id %) (:id bucket-just-id))
                  :periods
                  sp/NIL->VECTOR
                  sp/AFTER-ELEM]
                 new-period )
         ;; Set it as playing
         (setval [:period-in-play-id] new-id)
         ;; Set it as selected
         (setval [:selected-period] new-id))))

(defn stop-playing-period [db [_ _]]
  (assoc-in db [:period-in-play-id] nil))

(defn play-from-bucket [db [_ {:keys [bucket-id id now]}]]
  (let [new-period {:id          id
                    :planned     false
                    :start       now
                    :stop        (->> now
                                      (.valueOf)
                                      (+ 1000)
                                      (js/Date.))
                    :created     now
                    :last-edited now
                    :label       ""
                    :data        {}}]

    (->> db
         ;; Add new period
         (setval [:buckets sp/ALL
                  #(= (:id %) bucket-id)
                  :periods
                  sp/NIL->VECTOR
                  sp/AFTER-ELEM]
                 new-period )
         ;; Set it as playing
         (setval [:period-in-play-id] id)
         ;; Set it as selected
         (setval [:selected-period] id))))

(defn play-from-template [db [_ {:keys [template id now]}]]
  (let [new-period (merge template
                          {:id          id
                           :planned     false
                           :start       now
                           :stop        (->> now
                                             (.valueOf)
                                             (+ 1000)
                                             (js/Date.))
                           :created     now
                           :last-edited now})]
    (->> db
         ;; Add new period
         (setval [:buckets sp/ALL
                  #(= (:id %) (:bucket-id template))
                  :periods
                  sp/NIL->VECTOR
                  sp/AFTER-ELEM]
                 new-period )
         ;; Set it as playing
         (setval [:period-in-play-id] id)
         ;; Set it as selected
         (setval [:selected-period] id))))

(defn set-width [db [_ size]]
  (merge db {:width {:t-shirt (cond
                                (< size 1000) :sm
                                (< size 1920) :md
                                :else         :lg)
                     :pixels  size}}))

(rf/reg-event-db :set-width set-width)
(rf/reg-event-db :load-bucket-form load-bucket-form)
