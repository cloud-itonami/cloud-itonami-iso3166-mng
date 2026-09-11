(ns culture.facts
  "Country-level regional-culture catalog for Mongolia (MNG) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"MNG"
   [{:culture/id "mng.dish.buuz"
     :culture/name "Buuz"
     :culture/country "MNG"
     :culture/kind :dish
     :culture/summary "Mongolian steamed dumpling filled with meat, traditionally eaten during Tsagaan Sar (Lunar New Year)."
     :culture/url "https://en.wikipedia.org/wiki/Buuz"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mng.dish.khorkhog"
     :culture/name "Khorkhog"
     :culture/country "MNG"
     :culture/kind :dish
     :culture/summary "Barbecue dish in Mongolian cuisine in which meat is cooked inside a sealed container together with hot stones and water."
     :culture/url "https://en.wikipedia.org/wiki/Khorkhog"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mng.dish.aaruul"
     :culture/name "Aaruul"
     :culture/country "MNG"
     :culture/kind :dish
     :culture/summary "Dried dairy product made from strained yogurt, buttermilk or sour milk, part of the broader Central/Inner Asian kashk family; in Mongolia it is called aaruul or khuruud."
     :culture/url "https://en.wikipedia.org/wiki/Kashk"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mng.beverage.airag"
     :culture/name "Airag"
     :culture/country "MNG"
     :culture/kind :beverage
     :culture/summary "Traditional fermented dairy product made from mare milk, with strong cultural significance in Mongolia as both a beverage and place name."
     :culture/url "https://en.wikipedia.org/wiki/Airag"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mng.product.cashmere"
     :culture/name "Mongolian cashmere"
     :culture/country "MNG"
     :culture/kind :product
     :culture/summary "Mongolia is the world's second-largest cashmere producer, supplying about 9,600 tons of raw cashmere per year as of 2016."
     :culture/url "https://en.wikipedia.org/wiki/Cashmere_wool"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mng.craft.mongolian-calligraphy"
     :culture/name "Mongolian calligraphy"
     :culture/country "MNG"
     :culture/kind :craft
     :culture/summary "Traditional art of decorative writing in the classical Mongolian script, inscribed on the UNESCO List of Intangible Cultural Heritage in Need of Urgent Safeguarding in 2013."
     :culture/url "https://en.wikipedia.org/wiki/Mongolian_calligraphy"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mng.festival.naadam"
     :culture/name "Naadam"
     :culture/country "MNG"
     :culture/kind :festival
     :culture/summary "Traditional festival celebrated in Mongolia involving wrestling, horse racing and archery, inscribed on the Representative List of the Intangible Cultural Heritage of Humanity in 2010."
     :culture/url "https://en.wikipedia.org/wiki/Naadam"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mng.heritage.orkhon-valley"
     :culture/name "Orkhon Valley Cultural Landscape"
     :culture/country "MNG"
     :culture/kind :heritage
     :culture/summary "UNESCO World Heritage Site along the Orkhon River in central Mongolia, inscribed in 2004 for representing nomadic pastoral traditions spanning over two millennia."
     :culture/url "https://en.wikipedia.org/wiki/Orkhon_Valley_Cultural_Landscape"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-mng culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "MNG"))
                 " MNG entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
