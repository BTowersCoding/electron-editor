(ns app.renderer.db
  (:require
   [cljs.reader]
   [re-frame.core :as re-frame]
   [cljs.spec.alpha :as s]))

(s/def ::cursor-pos int?)
(s/def ::ave-wpm int?)
(s/def ::text string?)
(s/def ::current-key string?)
(s/def ::time int?)
(s/def ::key string?)
(s/def ::press (s/map-of ::time ::key))
(s/def ::presses (s/coll-of ::press))

(def default-db
  {:current-key ""
   :cursor-pos 0
   :presses []
   :eval-result ""
   :last-press 0
   :key-map {}
   :ave-wpm 0
   :high-speed 0
   :prob-keys []
   :errors 0})

(def ls-key "presses-reframe")                         ;; localstore key

(defn presses->local-store
  "Puts keypresses into localStorage"
  [presses]
  (.setItem js/localStorage ls-key (str presses)))

;; -- cofx Registrations  -----------------------------------------------------

;; Use `reg-cofx` to register a "coeffect handler" which will inject the keypresses
;; stored in localstore.
;;
;; In `events.cljs` there must be an event handler with
;; the interceptor `(inject-cofx :local-store-presses)`
;; The function registered below will be used to fulfill that request.
;;
(re-frame/reg-cofx
 :local-store-presses
 (fn [cofx _]
      ;; put the localstore presses into the coeffect under :local-store-presses
   (assoc cofx :local-store-presses
             ;; read in presses from localstore, and process into vector of maps
          (into []
                (some->> (.getItem js/localStorage ls-key)
                         (cljs.reader/read-string)    ;; EDN vector -> vector
                         )))))