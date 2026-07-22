(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest mng-has-spec-basis
  (let [sb (facts/spec-basis "MNG")]
    (is (= 4 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["MNG" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= #{"mng.investment-law-2013" "mng.permits-law-2022"}
         (set (mapv :statute/id (facts/by-topic "MNG" :corporate-governance)))))
  (is (= #{"mng.investment-law-2013"}
         (set (mapv :statute/id (facts/by-topic "MNG" :foreign-investment)))))
  (is (= #{"mng.labour-law-2021"}
         (set (mapv :statute/id (facts/by-topic "MNG" :labor)))))
  (is (empty? (facts/by-topic "ATL" :foreign-investment))))

(deftest mng-tax-authority-citation-is-honestly-limited
  (let [tax-entry (first (facts/by-topic "MNG" :tax))]
    (is (some? tax-entry))
    (is (nil? (:statute/enacted-date tax-entry))
        "this iteration could not independently confirm the General Tax Law's own adoption date or a specific tax-authority citation this session -- see namespace docstring")))
