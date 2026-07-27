(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "MNG" 0)
        s (registry/register-submit "eng-1" "MNG" 0)]
    (is (= "MNG-DFT-000000" (get d "draft_number")))
    (is (= "MNG-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "MNG" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest foreign-soe-permission-not-required-when-investor-not-state-owned
  (testing "an investor below the 50% foreign-state-ownership threshold never triggers the gate, regardless of sector/stake"
    (is (false? (registry/foreign-soe-permission-required?
                 {:investor-foreign-state-ownership-pct 49
                  :proposed-ownership-pct 100 :sector :mining})))
    (is (false? (registry/foreign-soe-permission-required?
                 {:investor-foreign-state-ownership-pct 0
                  :proposed-ownership-pct 100 :sector :banking-finance})))))

(deftest foreign-soe-permission-not-required-below-target-ownership-gate
  (testing "a qualifying foreign-state-owned investor below the 33% target-ownership gate does not trigger"
    (is (false? (registry/foreign-soe-permission-required?
                 {:investor-foreign-state-ownership-pct 100
                  :proposed-ownership-pct 32 :sector :mining})))))

(deftest foreign-soe-permission-not-required-outside-restricted-sectors
  (testing "a qualifying foreign-state-owned investor above the ownership gate but outside the three named sectors does not trigger"
    (is (false? (registry/foreign-soe-permission-required?
                 {:investor-foreign-state-ownership-pct 100
                  :proposed-ownership-pct 90 :sector :tourism})))
    (is (false? (registry/foreign-soe-permission-required?
                 {:investor-foreign-state-ownership-pct 100
                  :proposed-ownership-pct 90 :sector nil})))))

(deftest foreign-soe-permission-required-when-all-three-conditions-hold
  (testing "ALL THREE conditions (investor state-ownership >= 50%, target ownership >= 33%, restricted sector) must jointly hold"
    (is (true? (registry/foreign-soe-permission-required?
                {:investor-foreign-state-ownership-pct 50
                 :proposed-ownership-pct 33 :sector :mining})))
    (is (true? (registry/foreign-soe-permission-required?
                {:investor-foreign-state-ownership-pct 100
                 :proposed-ownership-pct 40 :sector :banking-finance})))
    (is (true? (registry/foreign-soe-permission-required?
                {:investor-foreign-state-ownership-pct 75
                 :proposed-ownership-pct 100 :sector :media-communications})))))

(deftest foreign-soe-permission-missing-data-is-honestly-not-required
  (testing "missing/nil primitive fields never trigger a requirement by default -- insufficient information is never treated as a violation"
    (is (false? (registry/foreign-soe-permission-required? {})))
    (is (false? (registry/foreign-soe-permission-required? {:sector :mining})))
    (is (false? (registry/foreign-soe-permission-required? {:investor-foreign-state-ownership-pct 100})))))

(deftest foreign-soe-permission-unverified-is-gated-on-recomputed-requirement
  (testing "no permission-verified violation is raised unless the requirement itself independently recomputes to true"
    (is (false? (registry/foreign-soe-permission-unverified?
                 {:investor-foreign-state-ownership-pct 0
                  :proposed-ownership-pct 100 :sector :mining
                  :med-permission-verified? false}))))
  (testing "a triggered requirement with no verification on file -> unverified"
    (is (true? (registry/foreign-soe-permission-unverified?
                {:investor-foreign-state-ownership-pct 100
                 :proposed-ownership-pct 40 :sector :mining
                 :med-permission-verified? false}))))
  (testing "a triggered requirement WITH verification on file -> not flagged"
    (is (false? (registry/foreign-soe-permission-unverified?
                 {:investor-foreign-state-ownership-pct 100
                  :proposed-ownership-pct 40 :sector :mining
                  :med-permission-verified? true})))))

;; ---------------------------------------------------------------------------
;; Money is compared at money precision, not at double precision
;; ---------------------------------------------------------------------------

(deftest whole-unit-fees-were-already-correct-and-stay-correct
  (testing "the seeded shape: base + rate x months in whole currency units"
    (is (registry/engagement-fee-matches-claim?
         {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
           :claimed-fee 860000.0}))))

(deftest cent-denominated-fees-are-no-longer-rejected-while-correct
  (testing "`(== (double claimed) (+ (double base) (* (double rate) (double months))))`
            rejected CORRECT totals once an amount carried cents -- 40,989 of
            327,060 combinations (12.5%), against 0 of 327,060 in whole units"
    (let [bad (for [m (range 1 37)
                    bc (range 10000 90000 2100)
                    rc (range 500 6000 210)
                    :let [truth (/ (+ bc (* rc m)) 100.0)]
                    :when (not (registry/engagement-fee-matches-claim?
                                {:base-fee (/ bc 100.0) :monthly-rate (/ rc 100.0)
                                  :monitoring-months m :claimed-fee truth}))]
                [m (/ bc 100.0) (/ rc 100.0) truth])]
      (is (empty? bad) (str "false rejections: " (count bad) " e.g. " (first bad))))))

(deftest a-genuinely-wrong-fee-is-still-caught
  (testing "rounding to money precision must not blunt the check"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.01})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 859999.99})))))

(deftest an-unverifiable-fee-never-matches
  (testing "un-verifiable is not the same as correct, and not a crash"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee "500000" :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.0})))
    (is (nil? (registry/compute-engagement-fee {:base-fee 500000 :monthly-rate 30000})))))
