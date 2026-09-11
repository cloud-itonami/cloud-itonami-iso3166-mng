# cloud-itonami-iso3166-mng

Open ISO 3166 Blueprint for **MNG**: Mongolia.

- Invest Mongolia / Investment and Trade Agency of Mongolia
  (`investmongolia.gov.mn`, under the Ministry of Economy and
  Development) -- administers investor-facing guidance for the
  Investment Law (2013), including a narrow foreign-state-owned-entity
  permission gate (MED permission required only when a 50%+
  foreign-state-owned investor is to hold 33%+ of a Mongolian entity in
  mining, banking and finance, or publication/media/communication) --
  NOT the old, broader 2012-era Strategic Entities Foreign Investment
  Law (SEFIL) regime, and NOT a fully liberal no-screening regime either
- General Authority for State Registration (GASR, `burtgel.gov.mn`) --
  legal-entity (LLC) registration ('LERO' in the Investment and Trade
  Agency's own English-language guide), 10 business days

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as `cloud-itonami-iso3166-btn`/`-caf`/`-cub`/`-lao`, investigated for
Mongolia's genuinely different market-entry surface (verified
2026-07-23, see the namespace docstrings for the full research trail,
including facts this iteration could NOT verify -- Mongolia's tax
authority's own citation, the Investment Law's exact adoption date/law
number, and whether a reported 2023 revision was enacted):

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Investment
  Law (2013), fetched and read directly from
  `investmongolia.gov.mn/legal-guide/` (the Investment and Trade Agency
  of Mongolia's own official site). This iteration specifically
  investigated whether Mongolia's regime resembles its own well-known
  2012-era Strategic Entities Foreign Investment Law (SEFIL) episode and
  found it does NOT -- Mongolia's CURRENT mechanism is a narrow permission
  gate scoped ONLY to investors that are themselves 50%+ foreign-state-
  owned, crossing a 33% ownership stake, in exactly three named sectors
  (mining, banking and finance, publication/media/communication) --
  neither SEFIL's broad case-by-case screening of ALL foreign investment
  above a threshold, nor a fully liberal no-screening regime.
  `governor.cljc`'s flagship check independently RECOMPUTES (never
  trusts a given boolean) whether an engagement's own declared
  investor-ownership-structure, target-ownership-stake and sector
  jointly cross this gate, then checks whether Ministry of Economy and
  Development (MED) permission is on file -- a three-factor conjunctive
  recompute, grep-verified absent (as a governor check function name)
  across the four iso3166 siblings this iteration cloned and read this
  session, a genuinely different shape from every one of them.
- `src/statute/facts.cljk` -- general-law catalog: the Investment Law
  (2013) itself (also catalogued here as an ongoing compliance statute),
  the Labor Law (revised/adopted July 2021, effective 1 January 2022),
  the Permits Law (2022, Articles 8.1/8.2 independently confirmed for
  license/permit classification), and the General Tax Law (title and
  purpose confirmed; no specific law number, adoption date, or
  tax-authority citation -- honestly disclosed gap, see namespace
  docstring).

Every citation is curl-verified against an official source
(`investmongolia.gov.mn`, `burtgel.gov.mn`). This iteration specifically
searched for Mongolia's tax authority (General Department of Taxation)
and could NOT independently reach or confirm its own official site this
session -- `mta.mn`/`www.mta.mn` failed DNS resolution outright;
`en.mta.mn` resolved in DNS but every connection attempt timed out;
`e-tax.mn`/`itax.mn` refused connections. `itc.gov.mn` DID load (own
text, read directly: 'Санхүүгийн мэдээллийн технологийн төв УТҮГ' /
'Financial Information Technology Center') but this is a DIFFERENT
agency, not conflated with the tax authority anywhere in this catalog --
`marketentry.facts/corporate-number-spec-basis` for 'MNG' is
deliberately `nil` as a result, an honestly-reported gap rather than a
fabricated citation. Similarly, this iteration did NOT independently
confirm this session whether the acronym 'LERO' (used in
`investmongolia.gov.mn`'s own English-language guide) formally names a
sub-office of `burtgel.gov.mn`'s General Authority for State
Registration or a distinct body -- both were independently confirmed to
exist and to perform legal-entity registration, but the exact
organizational linkage was not independently verified this session (see
`marketentry.facts` namespace docstring).

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Mongolia:

- `src/culture/facts.cljk` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
