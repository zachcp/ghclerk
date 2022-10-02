^{:nextjournal.clerk/visibility :hide-ns
  :nextjournal.clerk/toc true}
(ns {{top/ns}}.{{main/ns}}
   (:require
    [clojure.java.io :as io]
    [nextjournal.clerk :as clerk]
    [nextjournal.clerk.viewer :as v]
    [mundaneum.query :refer [search entity entity-data clojurized-claims describe label query *default-language*]]
    [mundaneum.properties :refer [wdt]])
   (:import [java.net URL]
             [java.nio.file Paths Files]
             [java.awt.image BufferedImage]
             [javax.imageio ImageIO]))
  

(comment  
  
   ;; start Clerk's built-in webserver on the default port 7777, opening the browser when done
  (clerk/serve! {:browse? true})
  ;; either call `clerk/show!` explicitly to show a given notebook.
  (clerk/show! "src/{{top/ns}}/{{main/ns}}.clj")
  )


;; ## Interactive Notebook Using Clerk
;;
;;  - Comment strings are interpreted as markdown. 
;;  - clojure code blocks can be evaluated to the REPL or viewed by the CLERK webserver.
;;  - clerk is highly extensible. see the [book-of-clerk](https://github.clerk.garden/nextjournal/book-of-clerk/commit/d74362039690a4505f15a61112cab7da0615e2b8/).
;;
;; 


;; ### 🧩 Clojure Data
;; The default set of viewers are able to render Clojure data.
(def clojure-data
  {:hello "world 👋"
   :tacos (map #(repeat % '🌮) (range 1 30))
   :zeta "The\npurpose\nof\nvisualization\nis\ninsight,\nnot\npictures."})

;; ### 🌐 Hiccup, HTML & SVG

;; The `html` viewer interprets `hiccup` when passed a vector.
(clerk/html [:div "As Clojurians we " [:em "really"] " enjoy hiccup"])


;; ### 🔢 Tables

;; Clerk provides a built-in data table viewer that supports the three
;; most common tabular data shapes out of the box: a sequence of maps,
;; where each map's keys are column names; a seq of seq, which is just
;; a grid of values with an optional header; a map of seqs, in with
;; keys are column names and rows are the values for that column.

(clerk/table [[1 2]
              [3 4]]) ;; seq of seqs

(clerk/table (clerk/use-headers [["odd numbers" "even numbers"]
                                 [1 2]
                                 [3 4]])) ;; seq of seqs with header

(clerk/table [{"odd numbers" 1 "even numbers" 2}
              {"odd numbers" 3 "even numbers" 4}]) ;; seq of maps

(clerk/table {"odd numbers" [1 3]
              "even numbers" [2 4]}) ;; map of seqs


;; ### 📊 Plotly

;; Clerk also has built-in support for Plotly's low-ceremony plotting:
(clerk/plotly {:data [{:z [[1 2 3] [3 2 1]] :type "surface"}]})


;; ### 🗺 Vega Lite

;; But Clerk also has Vega Lite for those who prefer that grammar.
(clerk/vl {:width 650 :height 400 :data {:url "https://vega.github.io/vega-datasets/data/us-10m.json"
                                         :format {:type "topojson" :feature "counties"}}
           :transform [{:lookup "id" :from {:data {:url "https://vega.github.io/vega-datasets/data/unemployment.tsv"}
                                            :key "id" :fields ["rate"]}}]
           :projection {:type "albersUsa"} :mark "geoshape" :encoding {:color {:field "rate" :type "quantitative"}}})


;; ### 🏞 Images

;; Clerk now has built-in support for the
;; `java.awt.image.BufferedImage` class, which is the native image
;; format of the JVM.
;;
;; When combined with `javax.imageio.ImageIO/read`, one can easily
;; load images in a variety of formats from a `java.io.File`, an
;; `java.io.InputStream`, or any resource that a `java.net.URL` can
;; address.
;;
;; For example, we can fetch a photo of De zaaier, Vincent van Gogh's
;; famous painting of a farmer sowing a field from Wiki Commons like
;; this:

(ImageIO/read (URL. "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/The_Sower.jpg/1510px-The_Sower.jpg"))



;; ### Wikidata Example
;;
;; Use the [mundaneum](https://github.com/jackrusher/mundaneum) Clojure DSL
;; to query wikidata.  Mundaneum uses a Datomic-like datalog syntax to 
;; convert all ID literals from their SPARQL shape (like `wd:Q451`) to 
;; Clojure namespaced keywords (like `:wd/Q451`), which gives us:

(query `{:select *
         :where [[:wd/Q451 ?p ?o]]})

