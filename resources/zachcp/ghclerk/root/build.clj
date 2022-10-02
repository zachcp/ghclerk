(ns build
  (:require  [nextjournal.clerk :as clerk]
             [babashka.fs :as fs]))

;; default is to build all of the clj files in the namespace
;; output will be a single fully-compiled file `public/build/index.html`
(let [notebook-paths (map str (fs/glob "src" "**{.clj,cljc}"))]
  (clerk/build-static-app! {:paths notebook-paths}))
