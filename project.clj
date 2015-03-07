(defproject hotslogs-uploader "0.0.1-SNAPSHOT"
  :description "Replay uploader for hotslogs.com"
  :url "https://github.com/henrikolsson/hotslogs-uploader"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-aws-s3 "0.3.10" :exclusions [joda-time]]
                 [joda-time/joda-time "2.7"]
                 [http-kit "2.1.19"]
                 [clojure-watch "0.1.10"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.10"]
                 [org.slf4j/slf4j-api "1.7.10"]]
  :main hotslogs.core
  :aot [hotslogs.core])
 
