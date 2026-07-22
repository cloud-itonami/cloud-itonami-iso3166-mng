(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Mongolia investment/company law, whether a claimed
  engagement fee actually equals base + months x rate, whether the
  engagement's own declared investor-ownership-structure/sector actually
  crosses the Investment Law (2013)'s own foreign-state-owned-entity
  permission gate (Ministry of Economy and Development, per
  investmongolia.gov.mn's own text), whether a legal-entity (LLC) state
  registration record (General Authority for State Registration / LERO)
  has been verified for a filing that requires it, or when a draft stops
  being a draft and becomes a real-world legal-entity-registration /
  investment filing, so this MUST be a separate system able to *reject*
  a proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual legal-entity registration filing or investment filing
  submission requires Market-Entry Compliance Governor clearance and
  always escalates to human sign-off'; 'a false or fabricated
  regulatory-requirement claim is a HARD hold') names exactly the checks
  below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Foreign-SOE permission      -- for `:filing/submit`,
       unverified                    INDEPENDENTLY recompute (never
                                      trust a given boolean) whether the
                                      engagement's own declared investor
                                      foreign-state-ownership%, proposed
                                      target-ownership% and sector
                                      jointly cross the Investment Law's
                                      MED permission gate, and if so
                                      whether `:med-permission-
                                      verified?` is on file. HARD-hold
                                      if the recomputed trigger fires
                                      but verification is absent.
                                      FLAGSHIP genuinely new check for
                                      the iso3166 family (grep-verified
                                      absent as a governor check
                                      function name across the four
                                      iso3166 siblings this iteration
                                      cloned and read) -- a THREE-FACTOR
                                      CONJUNCTIVE recompute over the
                                      engagement's own primitive fields,
                                      a check SHAPE genuinely different
                                      from every sibling read this
                                      session.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. Entity registration         -- for `:filing/submit`, when the
       unverified                     engagement declares
                                       `:requires-entity-
                                       registration? true`,
                                       INDEPENDENTLY check
                                       `:entity-registration-
                                       verified?`. CONDITIONAL on the
                                       engagement's own ground truth.
                                       Grounded in the General Authority
                                       for State Registration (LERO)
                                       legal-entity registration regime
                                       (see `marketentry.facts` --
                                       unlike cloud-itonami-iso3166-lao,
                                       this does NOT bundle a Tax
                                       Identification Number; TIN
                                       registration is a separate,
                                       honestly-undocumented-this-session
                                       step, see `marketentry.facts`
                                       namespace docstring).
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real legal-entity-registration/investment filing package
  and submitting a real filing are the two real-world actuation events
  this actor performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(法人設立登記/最低出資証明/税務・社会保険登録/MED許可書類等)が充足していない状態での提案"}]))))

(defn- foreign-soe-permission-unverified-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own declared investor-ownership-structure/sector jointly
  cross the Investment Law's MED permission gate -- the flagship check
  this vertical adds. HARD-hold when the recomputed trigger fires but
  the engagement has no `:med-permission-verified? true` on file."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/foreign-soe-permission-unverified? e)
        [{:rule :foreign-soe-permission-unverified
          :detail (str subject " は投資奨励法(2013年)の対外国家所有企業向け許可要件"
                      "(投資家自身の外国政府持分" (:investor-foreign-state-ownership-pct e) "%、"
                      "対象持分" (:proposed-ownership-pct e) "%、業種" (:sector e)
                      "の独立再計算)に該当するが、経済開発省(MED)の許可が確認されていない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- entity-registration-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-entity-registration? true`, INDEPENDENTLY check
  `:entity-registration-verified?` -- CONDITIONAL on the engagement's
  own ground truth. Grounded in the General Authority for State
  Registration (LERO) legal-entity registration regime -- does NOT
  bundle Tax Identification Number issuance (see `marketentry.facts`
  namespace docstring)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-entity-registration? e))
                 (not (true? (:entity-registration-verified? e))))
        [{:rule :entity-registration-unverified
          :detail (str subject " は法人設立登記証明書(General Authority for State Registration / LERO)の確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (foreign-soe-permission-unverified-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (entity-registration-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
