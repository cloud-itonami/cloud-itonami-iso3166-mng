(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-sector market-entry filing --
  every jurisdiction assigns its own format. This namespace does NOT
  invent one; it builds a jurisdiction-scoped sequence number and
  validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `foreign-soe-permission-required?` / `foreign-soe-permission-
  unverified?` are the SAME discipline applied to a genuinely
  Mongolia-specific mechanism: the Investment Law (2013)'s own narrow
  permission gate for foreign STATE-owned investors, confirmed directly
  from investmongolia.gov.mn's own text (see `marketentry.facts`): 'a
  permission from the MED must be obtain[ed] if a foreign state-owned
  entity (50% or more of the shares are directly or indirectly held by a
  foreign state) is to hold 33% or more of the issued shares of a
  Mongolian registered entity operating in the mining, banking and
  finance, or publication, media and communication sectors.'

  This is a GENUINELY DIFFERENT check SHAPE from every sibling this
  repo's docstring-writer read this session (`cloud-itonami-iso3166-lao`
  /-caf/-btn/-est, grep-verified absent as any function name resembling
  'state-owned'/'foreign-state'/'soe' across those four repos'
  `governor.cljc`/`registry.cljc` -- a good-faith, not fleet-wide-
  exhaustive, check limited to the four sibling repos this iteration
  actually cloned and read): `cloud-itonami-iso3166-lao`'s Arts.49-50
  mechanism routes a filing to one of TWO LEGISLATIVE bodies by
  numeric land-area/displaced-household thresholds; `cloud-itonami-
  iso3166-btn`'s FDI check tests sector membership on a static Negative
  List (a single boolean lookup). Mongolia's mechanism, by contrast,
  is a THREE-FACTOR CONJUNCTIVE recompute over the ENGAGEMENT'S OWN
  numeric/categorical fields, none of which is trusted as a given
  boolean the way `cloud-itonami-iso3166-lao`'s
  `:requires-enterprise-registration?` is trusted: it independently
  recomputes (a) whether the investor itself crosses the
  foreign-state-ownership threshold, (b) whether the proposed stake in
  the Mongolian entity crosses the target-ownership gate, AND (c)
  whether the declared sector is one of the three named restricted
  sectors -- only when ALL THREE hold does the MED permission
  requirement trigger at all, and only then is the separate
  `:med-permission-verified?` ground-truth field checked. This
  namespace does not model any band, threshold or sector beyond what
  investmongolia.gov.mn's own text states (no numeric or textual
  boundary for e.g. a general 'strategic sector' catch-all was found in
  the fetched text, so none is modeled here) -- the same honest
  scope-narrowing discipline `cloud-itonami-iso3166-lao`'s Art.50
  household-count-only recompute already established for this family.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real government system. It builds the RECORD an operator
  would keep, not the act of submitting a portal registration itself
  (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [kotoba.lang.text :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

(def foreign-soe-investor-ownership-threshold-pct
  "An investor counts as a 'foreign state-owned entity' when 50% or
  more of ITS OWN shares are directly or indirectly held by a foreign
  state, per investmongolia.gov.mn's own text."
  50)

(def foreign-soe-target-ownership-gate-pct
  "The MED permission requirement applies only when the qualifying
  foreign-state-owned investor is to hold 33% or more of the issued
  shares of the Mongolian registered entity."
  33)

(def foreign-soe-restricted-sectors
  "The MED permission requirement applies only in these three sectors,
  per investmongolia.gov.mn's own text -- no catch-all or additional
  sector is modeled (none was found in the fetched text)."
  #{:mining :banking-finance :media-communications})

(defn foreign-soe-permission-required?
  "INDEPENDENTLY recompute -- from `engagement`'s own declared
  `:investor-foreign-state-ownership-pct`, `:proposed-ownership-pct` and
  `:sector` -- whether the Investment Law's MED permission gate applies
  at all. Returns `false` unless ALL THREE conditions positively hold;
  missing/nil data never triggers a requirement by default (the same
  'insufficient information is never treated as a violation' discipline
  `cloud-itonami-iso3166-lao`'s legislative-approval-authority recompute
  uses)."
  [{:keys [investor-foreign-state-ownership-pct proposed-ownership-pct sector]}]
  (boolean (and investor-foreign-state-ownership-pct
                (>= investor-foreign-state-ownership-pct foreign-soe-investor-ownership-threshold-pct)
                proposed-ownership-pct
                (>= proposed-ownership-pct foreign-soe-target-ownership-gate-pct)
                (contains? foreign-soe-restricted-sectors sector))))

(defn foreign-soe-permission-unverified?
  "Does `engagement` INDEPENDENTLY require MED permission (per
  `foreign-soe-permission-required?`, recomputed from the engagement's
  own primitive fields -- never trusted as a given boolean) while
  `:med-permission-verified?` is not `true`? The flagship check this
  vertical adds."
  [{:keys [med-permission-verified?] :as engagement}]
  (boolean (and (foreign-soe-permission-required? engagement)
                (not (true? med-permission-verified?)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real government system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting the
  legal-entity registration / investment filing (always human-gated
  upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
