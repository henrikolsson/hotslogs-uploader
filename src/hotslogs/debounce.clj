(ns hotslogs.debounce)

(defn debounce
  [f wait]
  (let [timer (long-array [wait])]
    (fn [& args]
      (let [started (System/currentTimeMillis)]
        (aset timer 0 started)
        (loop []
          (when (= (aget timer 0) started)
            (let [waited (- (System/currentTimeMillis) started)]
              (if (< waited wait)
                (do
                  (Thread/sleep (- wait waited))
                  (recur))
                (do 
                  (aset timer 0 (System/currentTimeMillis))
                  (apply f args))))))))))
