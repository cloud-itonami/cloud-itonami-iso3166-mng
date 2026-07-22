(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest mng-has-spec-basis
  (let [sb (facts/spec-basis "MNG")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/foreign-soe-permission-spec-basis "MNG")))))

(deftest mng-corporate-number-spec-basis-is-honestly-absent
  (testing "this iteration could not independently reach or confirm Mongolia's tax authority this session (see namespace docstring) -- deliberately not claimed"
    (is (nil? (facts/corporate-number-spec-basis "MNG")))))

(deftest mng-rep-spec-basis-is-honestly-absent
  (testing "no verifiable Mongolia representative-exclusion-extension provision was located -- deliberately not claimed"
    (is (nil? (facts/rep-spec-basis "MNG")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "MNG")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "MNG" all)))
    (is (not (facts/required-evidence-satisfied? "MNG" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["MNG" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest foreign-soe-permission-spec-basis-criteria
  (let [aa (facts/foreign-soe-permission-spec-basis "MNG")]
    (is (= 50 (get-in aa [:foreign-soe-permission-criteria :investor-foreign-state-ownership-threshold-pct])))
    (is (= 33 (get-in aa [:foreign-soe-permission-criteria :target-entity-ownership-gate-pct])))
    (is (= #{:mining :banking-finance :media-communications}
           (get-in aa [:foreign-soe-permission-criteria :restricted-sectors])))))
