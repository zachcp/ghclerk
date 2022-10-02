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
  (clerk/show! "src/main.clj")
  )


;; ## SPARQL through the lens of Wikidata

;; Wikidata uses a
;; [TripleStore](https://en.wikipedia.org/wiki/Triplestore) to encode
;; their data. This means that everything in the database is stored as
;; a logical assertion in the form of a (_subject_, _predicate_,
;; _object_) triple. In practice, triples look something like 
;; `("Paul Otlet" "born in year" "1868")`.

;; An extremely simple Wikidata SPARQL query might look like this:

;; ```sparql
;; SELECT * WHERE {
;;   wd:Q451 ?p ?o
;; }
;; ```

;; We would read this as "give me all (_predicate_, _object_) pairs
;; for the _subject_ (which they call _entity_) with Wikidata ID
;; `wd:Q451`". Note that entities in Wikidata have numerical IDs like
;; this because they refer to language neutral _concepts_ rather than
;; to _words_ in a given language. Happily, every entity has labels
;; and descriptions associated with it in a variety of languages.

;; To translate this query into our Clojure DSL and execute it, we use
;; a Datomic-like datalog syntax and convert all ID literals from
;; their SPARQL shape (like `wd:Q451`) to Clojure namespaced keywords
;; (like `:wd/Q451`), which gives us:

(query `{:select *
         :where [[:wd/Q451 ?p ?o]]})



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


;; ### 🎼 Code

;; The code viewer uses
;; [clojure-mode](https://nextjournal.github.io/clojure-mode/) for
;; syntax highlighting.
(clerk/code (macroexpand '(when test
                            expression-1
                            expression-2)))

(clerk/code '(ns foo "A great ns" (:require [clojure.string :as str])))

(clerk/code "(defn my-fn\n  \"This is a Doc String\"\n  [args]\n  42)")

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


;; ### 🔠 Grid Layouts

;; Layouts can be composed via `row`s and `col`s
;;
;; Passing `:width`, `:height` or any other style attributes to
;; `::clerk/opts` will assign them on the row or col that contains
;; your items. You can use this to size your containers accordingly.

^{::clerk/visibility {:code :hide :result :hide}}
(def image-1 (ImageIO/read (URL. "https://etc.usf.edu/clipart/62300/62370/62370_letter-a_lg.gif")))

^{::clerk/visibility {:code :hide :result :hide}}
(def image-2 (ImageIO/read (URL. "https://etc.usf.edu/clipart/72700/72783/72783_floral_b_lg.gif")))

^{::clerk/visibility {:code :hide :result :hide}}
(def image-3 (ImageIO/read (URL. "https://etc.usf.edu/clipart/72700/72787/72787_floral_c_lg.gif")))


