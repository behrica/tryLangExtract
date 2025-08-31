(ns extract
  (:require 
            [libpython-clj2.python :as py]
            [libpython-clj2.require :refer [require-python]]
            )
  )
(require-python '[langextract :as lx]
                '[langextract.data :as lx-data]
                '[langextract.io :as lx.io]
                '[textwrap])

(comment
  (py/run-simple-string "import sys; print(sys.executable)")
  (py/run-simple-string "import sys; print(sys.path)")

  )

(def prompt "Extract 
             food, 
             averse effect, 
             assesment, 
             organisation, 
             country,
             conclusion,
             risk group,
             chemicals,
             crop,
             substances,
             procedure
    in order of appearance.
    Use exact text for extractions. Do not paraphrase or overlap entities.
    Extrcat the closest entity for every word of the text.
    Provide meaningful attributes for each entity to add context." )

(def examples [ (lx-data/ExampleData
                 :text "Eating fish might cause headaches. Fish is save for consumption by elderly says EFSA.
                        Glyphosate and oxygen is bad.
                        
                        ",
                 :extractions [(lx-data/Extraction
                                :extraction_class "food"
                                :extraction_text "fish"
                                ;:attributes {"emotional_state" "wonder"}
                                )
                               (lx-data/Extraction
                                :extraction_class "averse effect"
                                :extraction_text "headaches"
                                )
                               (lx-data/Extraction
                                :extraction_class "assesment"
                                :extraction_text "is safe for consumption")
                               (lx-data/Extraction
                                :extraction_class "organisation"
                                :extraction_text "EFSA")
                               (lx-data/Extraction
                                :extraction_class "conclusion"
                                :extraction_text "is safe")
                               (lx-data/Extraction
                                :extraction_class "conclusion"
                                :extraction_text "is present")
                               (lx-data/Extraction
                                :extraction_class "risk group"
                                :extraction_text "elderly")
                               (lx-data/Extraction
                                :extraction_class "substance"
                                :extraction_text "Glyphosate")
                               (lx-data/Extraction
                                :extraction_class "chemical"
                                :extraction_text "oxygen")
                               (lx-data/Extraction
                                :extraction_class "country"
                                :extraction_text "Germany")
                               (lx-data/Extraction
                                :extraction_class "crop"
                                :extraction_text "wheat")
                               
                               
                              ;;  (lx-data/Extraction
                              ;;   :extraction_class "emotion"
                              ;;   :extraction_text "But soft!"
                              ;;   :attributes {"feeling" "gentle awe"})
                              ;;  (lx-data/Extraction
                              ;;   :extraction_class "relationship"
                              ;;   :extraction_text "Juliet is the sun"
                              ;;   :attributes {"type" "metaphor"})
                               ]
                 


                 )])


;(def input_text "Lady Juliet gazed longingly at the stars, her heart aching for Romeo")
(def input_text "Following a request from the European Commission, the EFSA Panel on Nutrition, Novel Foods and Food Allergens (NDA) was asked to deliver an opinion on rhamnogalacturonan-I enriched carrot fibre (cRG-I) as a novel food (NF) pursuant to Regulation (EU) 2015/2283. The NF is a high molecular weight polysaccharide derived from carrot pomace. The Panel considers that the production process is sufficiently described and does not raise safety concerns. The novel food is intended for use as ingredient in various food products targeting the general population, in food for special medical purposes, meal replacement for weight control and food supplements targeting the general population excluding infants, and in total diet replacement for weight control targeting the adult population. Taking into account the composition of the NF and the proposed conditions of use, the consumption of the NF is not nutritionally disadvantageous. Based on the data provided the Panel considers that there are no concerns regarding genotoxicity. Results from the 90-day study did not show effects of toxicological relevance for humans up to the highest dose tested (7753 mg/kg bw per day). The NF may retain the allergenic potential of carrots and allergic reactions to the NF may occur, but they will not be dissimilar from those triggered by the consumption of carrots. The Panel considers the margins of exposure to be sufficient considering that the source (i.e. carrots), nature, composition and production of the NF do not raise safety concerns. The Panel concludes that the NF, cRG-I, a rhamnogalacturonan-rich polysaccharide fraction derived from carrot pomace, is safe under the proposed conditions of use.")
;(def input_text_2 "The conclusions of the European Food Safety Authority (EFSA) following the peer review of the initial risk assessments carried out by the competent authorities of the rapporteur Member State, the United Kingdom and Poland, after Brexit and co-rapporteur Member State, France, for the pesticide active substance prothioconazole are reported. The context of the peer review was that required by Commission Implementing Regulation (EU) No 844/2012, as amended by Commission Implementing Regulation (EU) No 2018/1659. The conclusions were reached on the basis of the evaluation of the representative uses of prothioconazole as a fungicide on barley, wheat, oats, spelt, rye and triticale field crops and seeds. The reliable end points, appropriate for use in regulatory risk assessment, are presented. Missing information identified as being required by the regulatory framework is listed. Concerns are reported where identified.")


(def result  (lx/extract
              :text_or_documents input_text
              :prompt_description prompt
              :examples examples
              
              :model_id "gemma3:4b"
              :model_url "http://localhost:11434"
              :fence_output false
              :use_schema_constraints false)
  )

 (lx.io/save_annotated_documents
  [result], 
  :output_name "extraction_results.jsonl", 
  :output_dir ".")

(def html_content (lx/visualize "extraction_results.jsonl"))

(spit "output.html" html_content)

(shutdown-agents)