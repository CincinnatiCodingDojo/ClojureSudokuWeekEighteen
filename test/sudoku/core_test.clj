(ns sudoku.core-test
  (:require [midje.sweet :refer :all]
            [sudoku.core :refer :all]))

(fact "first-of-column should return the top cell index"
  (first-of-column 0) => 0
  (first-of-column 1) => 1
  (first-of-column 40) => 4
  (first-of-column 80) => 8)

(fact "column-peers should return all indices of the column"
  (column-peers 40) => #{4 13 22 31 49 58 67 76})


(fact "first-of-row should return the left row index"
  (first-of-row 0) => 0
  (first-of-row 13) => 9
  (first-of-row 40) => 36
  (first-of-row 80) => 72)

(fact "row-peers should return all indexes of the row besides itself"
  (row-peers 7) => #{0 1 2 3 4 5 6 8})

(fact "first-of-region should return the index of the top-left cell of the region"
  (first-of-region 13) => 3
  (first-of-region 47) => 27
)

(fact "get-group-row-index returns zero 27 or 54 based on your index"
  (get-group-row-index 13 ) => 0
  (get-group-row-index 40 ) => 27)

(fact "region-peers should return the indicies of the cells in the region"
  (region-peers 40 ) => #{30 31 32 39 41 48 49 50}
  (region-peers 26 ) => #{ 6  7  8 15 16 17 24 25}
  )

(def tabula-rasa
  (->> (repeat 81 #{1 2 3 4 5 6 7 8 9})
    (apply vector)))

(def fake-solution
  (->> (repeat 81 #{1})
    (apply vector)))

(def contradictory-board
  (assoc fake-solution 40 #{}))

(def puzzle-board
  (-> tabula-rasa
    (set-value 0 6)
    (set-value 3 7)
    (set-value 5 9)
    (set-value 6 4)
    (set-value 7 2)
    (set-value 13 6)
    (set-value 15 5)
    (set-value 19 4)
    (set-value 20 9)
    (set-value 23 5)
    (set-value 25 8)
    (set-value 27 2)
    (set-value 31 5)
    (set-value 34 4)
    (set-value 41 1)
    (set-value 45 1)
    (set-value 46 5)
    (set-value 47 6)
    (set-value 48 4)
    (set-value 49 3)
    (set-value 50 2)
    (set-value 52 7)
    (set-value 55 7)
    (set-value 56 2)
    (set-value 58 9)
    (set-value 61 6)
    (set-value 65 1)
    (set-value 68 6)
    (set-value 72 4)
    (set-value 78 2)
    (set-value 79 9)
    (set-value 80 5)
    )
  )

(def board-contains-at-index (fn [index candidates]
                                  (fn [board]
                                    (= (board index) candidates))
                                  ))

(fact "get-peers should return all row, column, and region peers"
  (get-peers 40) => (contains (row-peers 40) :in-any-order :gaps-ok)
  (get-peers 40) => (contains (column-peers 40) :in-any-order :gaps-ok)
  (get-peers 40) => (contains (region-peers 40) :in-any-order :gaps-ok))

(fact "remove-candidate-value should eliminate a possible candidate from a cell"
  (remove-candidate-value 5 tabula-rasa 40) => (board-contains-at-index 40 #{1 2 3 4 6 7 8 9}))

(fact "setting a candidate's value sets it at the cell and removes the value from its peers"
  (set-value tabula-rasa 39 4) => (board-contains-at-index 39 #{4})
  (set-value tabula-rasa 39 4) => (board-contains-at-index 40 #{1 2 3 5 6 7 8 9})
  (set-value tabula-rasa 39 4) => (board-contains-at-index 3 #{1 2 3 5 6 7 8 9})
  )

(fact "a board with one value per cell is solved"
  (characterize-board fake-solution) => :solved
  (characterize-board tabula-rasa) =not=> :solved)

(fact "a board with an empty cell is contradictory"
  (characterize-board contradictory-board) => :contradictory)

(fact "a valid board is unsolved"
  (characterize-board puzzle-board) => :unsolved)
