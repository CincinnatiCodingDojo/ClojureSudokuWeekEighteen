(ns sudoku.core
  (:gen-class)
  (:require [clojure.set :refer :all]))



(defn first-of-column [index]
  (mod index 9))

(defn column-peers [index]
  (-> (range (first-of-column index) 81 9)
      set
      (disj index)))

(defn first-of-row [index]
  (* 9 (quot index 9))
)

(defn row-peers [index]
  (let [firstIndex (first-of-row index)]
    (-> (range firstIndex (+ firstIndex 9))
        set
        (disj index)) ; for the HM people.
  )
)

(defn get-group-row-index [index]
  (- index (mod index 27))
)

(defn first-of-region [index]
  (+ (get-group-row-index index) (* (quot ( first-of-column index) 3) 3) )
)

(defn region-peers [index]
  (let [grid-index (first-of-region index)]
    (-> (map (partial + grid-index) [0 1 2 9 10 11 18 19 20])
        set
        (disj index))))

(defn get-peers [index]
  (clojure.set/union (row-peers index)
    (column-peers index)
    (region-peers index)))

(defn remove-candidate-value [value board index]
  (let [cellValues (board index)]
    (assoc board index (disj cellValues value))))

(defn set-value [board index value]
  (let [board' (assoc board index #{value})
        peers (get-peers index)]
    (reduce (partial remove-candidate-value value) board' peers)))

(defn characterize-board [board]
  (let [set-has-one-value (fn [cell]
                            (= 1 (count cell)))
        set-has-no-valid-values (fn [cell]
                                  (= 0 (count cell)))]
    (if (every? set-has-one-value board)
      :solved
      (if (not-any? set-has-no-valid-values board)
      :unsolved
      :contradictory
      ))))
