(ns net.sekao.nightcode.core
  (:require [clojure.java.io :as io]
            [net.sekao.nightcode.boot]
            [net.sekao.nightcode.editors :as e]
            [net.sekao.nightcode.projects :as p]
            [net.sekao.nightcode.shortcuts :as shortcuts]
            [net.sekao.nightcode.state :refer [state]]
            [clojure.spec :as s])
  (:import [javafx.application Application]
           [javafx.fxml FXMLLoader]
           [javafx.stage Stage StageBuilder]
           [javafx.scene Scene])
  (:gen-class :extends javafx.application.Application))

(defn -start [^net.sekao.nightcode.core app ^Stage stage]
  (let [root (FXMLLoader/load (io/resource "main.fxml"))
        scene (Scene. root 1242 768)
        project-tree (.lookup scene "#project_tree")
        content (.lookup scene "#content")]
    (doto stage
      (.setTitle "Nightcode")
      (.setScene scene)
      (.show))
    (shortcuts/add-tooltips! scene [:project-tree :new-project :import-project :rename :remove])
    (shortcuts/set-shortcut-listeners! stage)
    (swap! state assoc :web-port (e/start-web-server!))
    (-> content .getChildren .clear)
    (p/update-project-tree! state project-tree)
    (p/update-project-buttons! @state scene)
    (p/set-selection-listener! state scene project-tree content)
    (p/set-focused-listener! state stage project-tree)))

(defn -main [& args]
  (Application/launch net.sekao.nightcode.core (into-array String args)))

(defn dev-main [& args]
  (s/instrument-all)
  (apply -main args))
