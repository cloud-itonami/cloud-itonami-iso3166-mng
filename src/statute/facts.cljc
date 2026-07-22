(ns statute.facts
  "General-law compliance catalog for Mongolia (MNG) -- extends this
  repo's existing `marketentry.facts` (public-sector market-entry/
  foreign-investment only, narrow scope) with a second, orthogonal
  catalog of national statutes a foreign investor operating in this
  jurisdiction must generally track for compliance. Mirrors
  cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/-arm/-atg/-ben/-btn/
  -caf/-cub/-lao's `statute.facts` (ADR-2607141700,
  cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL government-hosted URL -- never
  fabricated. All entries below were fetched directly (curl-verified,
  2026-07-23) from `investmongolia.gov.mn` -- the Investment and Trade
  Agency of Mongolia's own official site, under the Ministry of Economy
  and Development.

  - **Investment Law (2013)**: this is the SAME law `marketentry.facts`
    uses as its market-entry spec-basis; it is ALSO catalogued here as a
    general national-law reference per this task's own instructions,
    since a foreign investor tracks it both as a market-entry gate and
    as an ongoing compliance statute (incentives, protections, dispute
    resolution). Own text, read directly from
    `investmongolia.gov.mn/legal-guide/`: 'The key legislation regulating
    foreign investment in Mongolia is the Investment Law (2013)'. HONEST
    GAP: only the year is independently confirmed this session -- no
    specific adoption date or law number.
  - **Labour Law**: own text, read directly from the SAME source: 'The
    Labor Law was revised and adopted in July 2021 and become effective
    from 1 January 2022. The Labor Law is the key legislation regulating
    employment relationships in Mongolia.' HONEST GAP: only month/year
    is independently confirmed this session -- no specific day or law
    number.
  - **Permits Law (2022)**: own text, read directly from the SAME
    source: 'In Mongolia, a company may undertake business activities
    freely unless a certain activity is prohibited by law or subject to
    a license or permit according to the Permits Law (2022).' Business
    activities requiring a license or permit are 'listed under Articles
    8.1 and 8.2 of the Permits Law, respectively' (own text, read
    directly) -- this iteration independently confirmed these two
    article numbers directly from the source. HONEST GAP: only the year
    is independently confirmed for the law's own enactment; no specific
    adoption date.
  - **General Tax Law**: own text, read directly from the SAME source:
    'The purpose of the General Tax Law (the Tax Law) in Mongolia is to:
    Establish legal grounds for the introduction, establishment,
    imposition, reporting, payment, control and collection of taxes in
    Mongolia. Define the rights, duties and liabilities of taxpayers and
    tax authorities. Regulate relations arising between taxpayers and
    tax authorities.' HONEST GAP: this iteration specifically searched
    for Mongolia's tax authority (General Department of Taxation) and
    could NOT independently reach or confirm its own official site this
    session -- `mta.mn`/`www.mta.mn` failed DNS resolution outright;
    `en.mta.mn` resolved in DNS but every connection attempt timed out;
    `e-tax.mn`/`itax.mn` refused connections. `itc.gov.mn` DID load (own
    text, read directly: 'Санхүүгийн мэдээллийн технологийн төв УТҮГ' /
    'Financial Information Technology Center') but this is a DIFFERENT
    agency, not conflated with the tax authority here. No specific law
    number or adoption date for the General Tax Law was independently
    confirmed this session either -- title and purpose only.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"MNG"
   [{:statute/id "mng.investment-law-2013"
     :statute/title "Investment Law (2013)"
     :statute/jurisdiction "MNG"
     :statute/kind :law
     :statute/law-number "Year only independently confirmed this session ('Investment Law (2013)', per investmongolia.gov.mn's own 'Setting up in Mongolia' legal guide, own text, read directly) -- no specific adoption date or law number was independently confirmed; a revision was reported by the same source as 'proposed ... with the Parliament in June 2023', enactment status not confirmed this session"
     :statute/url "https://investmongolia.gov.mn/legal-guide/"
     :statute/url-provenance :official-investmongolia-gov-mn
     :statute/enacted-date "2013"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:foreign-investment :corporate-governance :incorporation}}
    {:statute/id "mng.labour-law-2021"
     :statute/title "Labor Law"
     :statute/jurisdiction "MNG"
     :statute/kind :law
     :statute/law-number "Month/year only independently confirmed this session ('revised and adopted in July 2021 ... effective from 1 January 2022', per investmongolia.gov.mn's own legal guide, own text, read directly) -- no specific day or law number was independently confirmed"
     :statute/url "https://investmongolia.gov.mn/legal-guide/"
     :statute/url-provenance :official-investmongolia-gov-mn
     :statute/enacted-date "2021-07"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:labor}}
    {:statute/id "mng.permits-law-2022"
     :statute/title "Permits Law"
     :statute/jurisdiction "MNG"
     :statute/kind :law
     :statute/law-number "Year only independently confirmed this session ('Permits Law (2022)', per investmongolia.gov.mn's own legal guide, own text, read directly); licensable/permit-requiring activities are 'listed under Articles 8.1 and 8.2 of the Permits Law, respectively' (own text, read directly, article numbers independently confirmed) -- no specific adoption date for the law itself was independently confirmed"
     :statute/url "https://investmongolia.gov.mn/legal-guide/"
     :statute/url-provenance :official-investmongolia-gov-mn
     :statute/enacted-date "2022"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:licensing :corporate-governance}}
    {:statute/id "mng.general-tax-law"
     :statute/title "General Tax Law"
     :statute/jurisdiction "MNG"
     :statute/kind :law
     :statute/law-number "No specific law number or adoption date independently confirmed this session -- investmongolia.gov.mn's own legal guide (own text, read directly) states only the law's purpose ('Establish legal grounds for the introduction, establishment, imposition, reporting, payment, control and collection of taxes in Mongolia...'); this iteration could NOT independently reach or confirm Mongolia's tax authority's own site this session (see namespace docstring) to obtain a more precise citation"
     :statute/url "https://investmongolia.gov.mn/legal-guide/"
     :statute/url-provenance :official-investmongolia-gov-mn
     :statute/enacted-date nil
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:tax}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-mng statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "MNG")) " MNG statute(s) seeded with an "
                 "official citation. Extend `statute.facts/catalog`, never "
                 "fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
