# Business Model — Mongolia

## Offer

- Invest Mongolia / Investment and Trade Agency of Mongolia
  (`investmongolia.gov.mn`, under the Ministry of Economy and
  Development) -- administers investor-facing guidance for the
  Investment Law (2013). Company/legal-entity registration runs through
  the General Authority for State Registration ('LERO', 10 business
  days per investmongolia.gov.mn's own text), NOT a case-by-case
  executive-authorization pipeline (see `src/marketentry/facts.cljk`)
- Foreign-state-owned-entity permission gate (flagship check) -- bars a
  filing from proceeding when an engagement's own declared investor
  foreign-state-ownership%, target-ownership-stake% and sector jointly
  cross the Investment Law's Ministry of Economy and Development (MED)
  permission gate (50%+ foreign-state-owned investor, 33%+ target
  stake, in mining, banking and finance, or publication/media/
  communication) without MED permission verified on file. A narrow
  special case: most foreign investment (private investors, or below
  either threshold, or outside these three sectors) never reaches this
  gate at all -- genuinely narrower than Mongolia's own 2012-era
  Strategic Entities Foreign Investment Law (SEFIL) episode, and NOT a
  fully liberal no-screening regime either
- Tax/social-insurance registration is a SEPARATE mandatory
  post-registration step, NOT bundled into the legal-entity registration
  certificate (per investmongolia.gov.mn's own text) -- this iteration
  could not independently confirm Mongolia's specific tax-authority
  citation this session (honest gap, see `src/statute/facts.cljk`)
- market entry via legal-entity (LLC) registration + tax/social-insurance
  registration (+ MED permission when the foreign-SOE gate applies), not
  direct competitive bidding for most sectors

## Trust Controls

- Any actual legal-entity registration filing or investment filing
  submission requires Market-Entry Compliance Governor clearance and
  always escalates to human sign-off.
- A false or fabricated regulatory-requirement claim is a HARD hold.
- `:filing/submit` never automated
