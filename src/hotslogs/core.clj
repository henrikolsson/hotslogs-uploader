(ns hotslogs.core
  (:import [java.io File]
           [java.util UUID Date]
           [java.util.concurrent ConcurrentHashMap])
  (:require [aws.sdk.s3 :as s3]
            [org.httpkit.client :as http]
            [clojure-watch.core :refer [start-watch]]
            [clojure.tools.logging :as log])
  (:use [hotslogs.debounce])
  (:gen-class))

(def cred {:access-key "AKIAIESBHEUH4KAAG4UA",
           :secret-key "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"})
(def work (ConcurrentHashMap.))
(def watcher (atom nil))

(defn upload-replay [path]
  (let [filename (str (UUID/randomUUID) ".StormReplay")
        file (File. path)]
    (log/info (str "Uploading " (.getName file) "..."))
    (let [putres (s3/put-object cred "heroesreplays" filename file)
          result (:body @(http/get (str "https://www.hotslogs.com/UploadFile.aspx?FileName=" filename)))]
      (log/info (str "Uploaded " (.getName file) ": " result)))))

(defn event-handler [event filename]
  (try
    (if (= event :modify)
      (let [fd (delay (try
                        (upload-replay filename)
                        (catch Exception ex
                          (log/error ex "Upload failed")))
                      (.remove work filename))
            f (debounce #(deref fd) 2000)]
        (.putIfAbsent work filename f)
        (future ((.get work filename)))))
    (catch Exception ex
      (log/error ex "Failed to queue upload"))))

(defn start [directory]
  (log/info (str "Watching " directory "..."))
  (reset! watcher
          (start-watch [{:path directory
                         :event-types [:modify :create :delete]
                         :callback #'event-handler}])))
(defn stop []
  (log/info "Stopping")
  (@watcher))

(defn -main [& args]
  (let [directory (first args)]
    (if (not directory)
      (println "usage: java -jar <jarfile> <replay directory>")
      (start directory))))

