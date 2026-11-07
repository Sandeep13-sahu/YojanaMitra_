package com.example.data.repository

import com.example.data.model.CriterionOperator
import com.example.data.model.EligibilityCriterion
import com.example.data.model.Scheme
import com.example.data.model.SchemeDocument
import com.example.data.model.ServiceCentre

object SchemeData {

    val verifiedSchemes: List<Scheme> = listOf(
        Scheme(
            id = "pm_kisan",
            schemeName = "PM Kisan Samman Nidhi",
            schemeNameHindi = "प्रधानमंत्री किसान सम्मान निधि योजना (PM-KISAN)",
            category = "Agriculture",
            department = "Ministry of Agriculture & Farmers Welfare",
            governmentLevel = "Central Government",
            applicableState = "All India",
            shortDescription = "Financial assistance of ₹6,000 per year in three equal installments of ₹2,000 to landholding farmer families.",
            shortDescriptionHindi = "भूमिधारक किसान परिवारों को प्रति वर्ष ₹6,000 की वित्तीय सहायता, तीन समान किस्तों (₹2,000 प्रत्येक) में सीधे बैंक खाते में प्रदान की जाती है।",
            benefitSummary = "₹6,000 annually via Direct Benefit Transfer (DBT)",
            benefitSummaryHindi = "₹6,000 प्रति वर्ष सीधे बैंक खाते में (डीबीटी द्वारा)",
            officialLink = "https://pmkisan.gov.in",
            sourceReference = "Ministry of Agriculture & Farmers Welfare, GoI (Verified Portal)",
            lastVerifiedDate = "2026-11-01",
            version = 3,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "pmk_occ",
                    attribute = "occupation",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Farmer,किसान",
                    isMandatory = true,
                    labelHindi = "व्यवसाय: किसान",
                    labelEnglish = "Occupation: Farmer",
                    failureReasonHindi = "यह योजना केवल किसान परिवारों के लिए है।",
                    failureReasonEnglish = "This scheme is restricted to farmer families."
                ),
                EligibilityCriterion(
                    id = "pmk_land",
                    attribute = "landholdingAcres",
                    operator = CriterionOperator.LESS_THAN_OR_EQUAL_LAND,
                    expectedValue = "5.0",
                    isMandatory = false,
                    labelHindi = "कृषि भूमि: 5 एकड़ तक (लघु एवं सीमांत किसान)",
                    labelEnglish = "Landholding: Up to 5 acres",
                    failureReasonHindi = "भूमि सीमा 5 एकड़ से अधिक है।",
                    failureReasonEnglish = "Landholding exceeds priority threshold."
                ),
                EligibilityCriterion(
                    id = "pmk_age",
                    attribute = "age",
                    operator = CriterionOperator.GTE,
                    expectedValue = "18",
                    isMandatory = true,
                    labelHindi = "न्यूनतम आयु: 18 वर्ष",
                    labelEnglish = "Minimum Age: 18 years",
                    failureReasonHindi = "आवेदक की आयु 18 वर्ष या उससे अधिक होनी चाहिए।",
                    failureReasonEnglish = "Applicant must be at least 18 years of age."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "बैंक खाते से लिंक आधार संख्या"),
                SchemeDocument("जमीन के दस्तावेज (LPC / Khatian / Rasid)", "Land Records (LPC)", "Land", true, "भू-स्वामित्व प्रमाण पत्र अथवा अद्यतन लगान रसीद"),
                SchemeDocument("बैंक पासबुक विवरण (Bank Passbook)", "Bank Passbook", "Financial", true, "डीबीटी और एनपीसीआई सक्रिय बैंक खाता"),
                SchemeDocument("मोबाइल नंबर (Aadhaar Linked Mobile)", "Mobile Number", "Contact", true, "ओटीपी ई-केवाईसी सत्यापन हेतु")
            )
        ),

        Scheme(
            id = "ayushman_bharat",
            schemeName = "Ayushman Bharat PM-JAY",
            schemeNameHindi = "आयुष्मान भारत - प्रधानमंत्री जन आरोग्य योजना (PM-JAY)",
            category = "Healthcare",
            department = "National Health Authority, Ministry of Health",
            governmentLevel = "Central Government",
            applicableState = "All India",
            shortDescription = "Health cover of up to ₹5 lakh per family per year for secondary and tertiary care hospitalization to vulnerable families.",
            shortDescriptionHindi = "गरीब और कमजोर परिवारों को द्वितीयक और तृतीयक अस्पताल में भर्ती के लिए प्रति परिवार प्रति वर्ष ₹5 लाख तक का कैशलेस स्वास्थ्य बीमा।",
            benefitSummary = "₹5,00,000 cashless health insurance cover per year",
            benefitSummaryHindi = "प्रति परिवार प्रति वर्ष ₹5 लाख का मुफ्त कैशलेस इलाज",
            officialLink = "https://pmjay.gov.in",
            sourceReference = "National Health Authority, GoI (Verified Portal)",
            lastVerifiedDate = "2026-11-01",
            version = 4,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "ayush_bpl",
                    attribute = "hasBplRationCard",
                    operator = CriterionOperator.BOOLEAN_TRUE,
                    expectedValue = "true",
                    isMandatory = true,
                    labelHindi = "राशन कार्ड / बीपीएल पात्रता",
                    labelEnglish = "Ration Card / BPL / SECC status",
                    failureReasonHindi = "राष्ट्रीय खाद्य सुरक्षा कानून या एसईसीसी सूची में नाम होना अनिवार्य है।",
                    failureReasonEnglish = "Applicant must be registered in NFSA / SECC eligible database."
                ),
                EligibilityCriterion(
                    id = "ayush_income",
                    attribute = "householdIncome",
                    operator = CriterionOperator.MAX_INCOME,
                    expectedValue = "250000",
                    isMandatory = true,
                    labelHindi = "वार्षिक आय सीमा: ₹2.5 लाख",
                    labelEnglish = "Annual Income Ceiling: ₹2.5 Lakh",
                    failureReasonHindi = "पारिवारिक आय ₹2,50,000 से अधिक है।",
                    failureReasonEnglish = "Household income exceeds eligibility ceiling."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "सत्यापन हेतु"),
                SchemeDocument("राशन कार्ड (Ration Card)", "Ration Card", "Residence", true, "पात्रता एवं परिवार के सदस्यों की सूची"),
                SchemeDocument("सक्रिय मोबाइल नंबर", "Active Mobile Number", "Contact", true, "गोल्डन कार्ड सत्यापन हेतु")
            )
        ),

        Scheme(
            id = "pmay_gramin",
            schemeName = "Pradhan Mantri Awas Yojana - Gramin",
            schemeNameHindi = "प्रधानमंत्री आवास योजना - ग्रामीण (PMAY-G)",
            category = "Housing",
            department = "Ministry of Rural Development",
            governmentLevel = "Central Government",
            applicableState = "All India",
            shortDescription = "Financial assistance of ₹1,20,000 (plains) to ₹1,30,000 (hilly areas) for the construction of pucca houses for homeless rural households.",
            shortDescriptionHindi = "बेघर और कच्चे मकान में रहने वाले ग्रामीण परिवारों को पक्का मकान बनाने हेतु ₹1,20,000 की वित्तीय सहायता एवं मनरेगा के तहत मजदूरी।",
            benefitSummary = "₹1,20,000 cash grant + ₹12,000 toilet grant + 90 days MGNREGA wages",
            benefitSummaryHindi = "₹1,20,000 मकान निर्माण अनुदान + ₹12,000 शौचालय + 90 दिन मनरेगा मजदूरी",
            officialLink = "https://pmayg.nic.in",
            sourceReference = "Ministry of Rural Development, GoI",
            lastVerifiedDate = "2026-11-01",
            version = 2,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "pmay_income",
                    attribute = "householdIncome",
                    operator = CriterionOperator.MAX_INCOME,
                    expectedValue = "180000",
                    isMandatory = true,
                    labelHindi = "वार्षिक आय सीमा: ₹1.8 लाख",
                    labelEnglish = "Annual Income: Up to ₹1.8 Lakh",
                    failureReasonHindi = "वार्षिक आय सीमा ₹1,80,000 से अधिक है।",
                    failureReasonEnglish = "Income exceeds rural housing ceiling."
                ),
                EligibilityCriterion(
                    id = "pmay_age",
                    attribute = "age",
                    operator = CriterionOperator.GTE,
                    expectedValue = "18",
                    isMandatory = true,
                    labelHindi = "मुखिया की न्यूनतम आयु: 18 वर्ष",
                    labelEnglish = "Head of Family Age: 18+ years",
                    failureReasonHindi = "मुखिया की आयु 18 वर्ष से कम है।",
                    failureReasonEnglish = "Applicant must be adult."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "परिवार के सभी वयस्क सदस्यों का"),
                SchemeDocument("जॉब कार्ड (MGNREGA Job Card)", "Job Card", "Employment", true, "मजदूरी भुगतान हेतु"),
                SchemeDocument("बैंक खाता विवरण (Bank Details)", "Bank Account", "Financial", true, "सीधे खाते में किस्तों के भुगतान हेतु"),
                SchemeDocument("कच्चे मकान का फोटो व भूमि विवरण", "Land / Old House Photo", "Residence", true, "भौतिक सत्यापन हेतु")
            )
        ),

        Scheme(
            id = "bihar_student_credit_card",
            schemeName = "Bihar Student Credit Card Yojana",
            schemeNameHindi = "बिहार स्टूडेंट क्रेडिट कार्ड योजना (MNSSBY)",
            category = "Education",
            department = "Education Department, Government of Bihar",
            governmentLevel = "Bihar Government",
            applicableState = "Bihar",
            shortDescription = "Education loan of up to ₹4 Lakh at very low interest rates (1% for girls/disabled/transgender, 4% for boys) for higher education.",
            shortDescriptionHindi = "12वीं पास बिहार के विद्यार्थियों को उच्च शिक्षा (इंजीनियरिंग, मेडिकल, बीए, बीएससी, आदि) हेतु ₹4 लाख तक का शिक्षा ऋण, बेहद कम ब्याज दर पर।",
            benefitSummary = "Up to ₹4 Lakh education loan with government guarantee",
            benefitSummaryHindi = "उच्च शिक्षा हेतु ₹4,00,000 तक का रियायती ऋण (लड़कियों हेतु मात्र 1% ब्याज)",
            officialLink = "https://www.7nishchay-yuvaupmission.bihar.gov.in",
            sourceReference = "Bihar Vikas Mission & Education Dept, Bihar",
            lastVerifiedDate = "2026-11-01",
            version = 2,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "bscc_state",
                    attribute = "state",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Bihar",
                    isMandatory = true,
                    labelHindi = "बिहार का मूल निवासी",
                    labelEnglish = "Permanent resident of Bihar",
                    failureReasonHindi = "आवेदक का बिहार राज्य का मूल निवासी होना अनिवार्य है।",
                    failureReasonEnglish = "Must be a permanent resident of Bihar."
                ),
                EligibilityCriterion(
                    id = "bscc_age",
                    attribute = "age",
                    operator = CriterionOperator.LTE,
                    expectedValue = "25",
                    isMandatory = true,
                    labelHindi = "अधिकतम आयु: 25 वर्ष",
                    labelEnglish = "Maximum Age: 25 years",
                    failureReasonHindi = "आवेदक की आयु 25 वर्ष से कम या बराबर होनी चाहिए।",
                    failureReasonEnglish = "Applicant must be 25 years of age or younger."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("10वीं एवं 12वीं की अंकतालिका (Marksheet)", "10th & 12th Marks Sheet", "Education", true, "शैक्षणिक योग्यता प्रमाण"),
                SchemeDocument("उच्च शिक्षण संस्थान में नामांकन प्रमाण (Admission Proof)", "Admission Offer Letter", "Education", true, "संस्थान से फीस संरचना सहित"),
                SchemeDocument("आवासीय प्रमाण पत्र (Residence Certificate)", "Domicile Certificate", "Residence", true, "बिहार का स्थायी निवासी प्रमाण"),
                SchemeDocument("आधार कार्ड व पैन कार्ड (Aadhaar & PAN)", "Aadhaar & PAN", "Identity", true, "विद्यार्थी व अभिभावक दोनों का"),
                SchemeDocument("बैंक खाता पासबुक (Bank Passbook)", "Bank Passbook", "Financial", true, "विद्यार्थी का व्यक्तिगत खाता")
            )
        ),

        Scheme(
            id = "mukhyamantri_kanya_utthan",
            schemeName = "Mukhyamantri Kanya Utthan Yojana",
            schemeNameHindi = "मुख्यमंत्री कन्या उत्थान योजना (बिहार)",
            category = "Women & Child",
            department = "Social Welfare Department, Bihar",
            governmentLevel = "Bihar Government",
            applicableState = "Bihar",
            shortDescription = "Incentive assistance from birth to graduation (up to ₹50,000 for graduate girls) to promote female education and eliminate child marriage.",
            shortDescriptionHindi = "लड़कियों को जन्म से स्नातक तक की शिक्षा हेतु प्रोत्साहन राशि। स्नातक (ग्रेजुएशन) उत्तीर्ण छात्राओं को ₹50,000 की एकमुश्त सहायता।",
            benefitSummary = "Up to ₹50,000 direct scholarship grant for girls",
            benefitSummaryHindi = "स्नातक उत्तीर्ण कन्याओं को ₹50,000 की प्रोत्साहन राशि",
            officialLink = "https://medhasoft.bih.nic.in",
            sourceReference = "Social Welfare Department, Government of Bihar",
            lastVerifiedDate = "2026-11-01",
            version = 3,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "mku_gender",
                    attribute = "gender",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Female",
                    isMandatory = true,
                    labelHindi = "लिंग: केवल महिला/कन्या",
                    labelEnglish = "Gender: Female only",
                    failureReasonHindi = "यह योजना केवल राज्य की बेटियों/छात्राओं के लिए है।",
                    failureReasonEnglish = "This scheme is exclusively for female students."
                ),
                EligibilityCriterion(
                    id = "mku_state",
                    attribute = "state",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Bihar",
                    isMandatory = true,
                    labelHindi = "बिहार की निवासी",
                    labelEnglish = "Resident of Bihar",
                    failureReasonHindi = "बिहार का मूल निवासी होना अनिवार्य है।",
                    failureReasonEnglish = "Must be a resident of Bihar."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("स्नातक/इंटर अंकतालिका एवं प्रवेश पत्र", "Graduation Marksheet", "Education", true, "मान्यता प्राप्त विश्वविद्यालय से उत्तीर्ण"),
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "छात्रा के नाम से"),
                SchemeDocument("बिहार का निवास प्रमाण पत्र", "Residential Certificate", "Residence", true, "अंचल अधिकारी द्वारा निर्गत"),
                SchemeDocument("बैंक पासबुक (Bank Passbook)", "Bank Passbook", "Financial", true, "छात्रा के नाम का एकल खाता (बिहार में स्थित बैंक)")
            )
        ),

        Scheme(
            id = "atal_pension_yojana",
            schemeName = "Atal Pension Yojana (APY)",
            schemeNameHindi = "अटल पेंशन योजना (APY)",
            category = "Pension",
            department = "PFRDA, Ministry of Finance",
            governmentLevel = "Central Government",
            applicableState = "All India",
            shortDescription = "Guaranteed monthly pension of ₹1,000 to ₹5,000 to subscribers after age 60, focusing on unorganized sector workers.",
            shortDescriptionHindi = "असंगठित क्षेत्र के कामगारों के लिए 60 वर्ष की आयु के बाद ₹1,000 से ₹5,000 प्रति माह की न्यूनतम गारंटीकृत पेंशन।",
            benefitSummary = "Guaranteed monthly pension between ₹1,000 and ₹5,000 from age 60",
            benefitSummaryHindi = "60 वर्ष की उम्र से आजीवन ₹1,000 से ₹5,000 मासिक पेंशन",
            officialLink = "https://www.npscra.nsdl.co.in",
            sourceReference = "Pension Fund Regulatory & Development Authority (PFRDA)",
            lastVerifiedDate = "2026-11-01",
            version = 2,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "apy_min_age",
                    attribute = "age",
                    operator = CriterionOperator.GTE,
                    expectedValue = "18",
                    isMandatory = true,
                    labelHindi = "न्यूनतम प्रवेश आयु: 18 वर्ष",
                    labelEnglish = "Min Entry Age: 18 years",
                    failureReasonHindi = "आयु कम से कम 18 वर्ष होनी चाहिए।",
                    failureReasonEnglish = "Applicant must be at least 18 years old."
                ),
                EligibilityCriterion(
                    id = "apy_max_age",
                    attribute = "age",
                    operator = CriterionOperator.LTE,
                    expectedValue = "40",
                    isMandatory = true,
                    labelHindi = "अधिकतम प्रवेश आयु: 40 वर्ष",
                    labelEnglish = "Max Entry Age: 40 years",
                    failureReasonHindi = "प्रवेश आयु 40 वर्ष से अधिक नहीं होनी चाहिए।",
                    failureReasonEnglish = "Entry age must not exceed 40 years."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "केवाईसी सत्यापन हेतु"),
                SchemeDocument("बचत बैंक खाता विवरण", "Savings Bank Account", "Financial", true, "मासिक अंशदान के ऑटो-डेबिट हेतु"),
                SchemeDocument("सक्रिय मोबाइल नंबर", "Mobile Number", "Contact", true, "पेंशन अलर्ट प्राप्त करने हेतु")
            )
        ),

        Scheme(
            id = "bihar_diesel_anudan",
            schemeName = "Bihar Krishi Diesel Anudan Yojana",
            schemeNameHindi = "बिहार कृषि डीजल अनुदान योजना (खरीफ/रबी)",
            category = "Agriculture",
            department = "Agriculture Department, Bihar",
            governmentLevel = "Bihar Government",
            applicableState = "Bihar",
            shortDescription = "Subsidy of ₹75 per liter (up to ₹750 per acre per irrigation) for farmers purchasing diesel for irrigation during dry spells.",
            shortDescriptionHindi = "सूखाग्रस्त स्थिति में खरीफ फसलों की सिंचाई हेतु डीजल पंपसेट चलाने के लिए ₹75 प्रति लीटर (अधिकतम ₹750 प्रति एकड़ प्रति सिंचाई) की सब्सिडी।",
            benefitSummary = "₹75/liter diesel subsidy up to ₹750/acre per irrigation cycle",
            benefitSummaryHindi = "₹75 प्रति लीटर (अधिकतम ₹750/एकड़ प्रति सिंचाई) का अनुदान",
            officialLink = "https://dbtagriculture.bihar.gov.in",
            sourceReference = "Direct Benefit Transfer in Agriculture, Bihar",
            lastVerifiedDate = "2026-11-01",
            version = 1,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "bda_state",
                    attribute = "state",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Bihar",
                    isMandatory = true,
                    labelHindi = "राज्य: बिहार का किसान",
                    labelEnglish = "State: Bihar Farmer",
                    failureReasonHindi = "यह योजना केवल बिहार के किसानों के लिए है।",
                    failureReasonEnglish = "This scheme is only for farmers in Bihar."
                ),
                EligibilityCriterion(
                    id = "bda_occ",
                    attribute = "occupation",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Farmer,किसान",
                    isMandatory = true,
                    labelHindi = "व्यवसाय: किसान (स्वयं या बटाईदार)",
                    labelEnglish = "Occupation: Farmer / Sharecropper",
                    failureReasonHindi = "आवेदक का किसान होना अनिवार्य है।",
                    failureReasonEnglish = "Applicant must be a farmer."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("डीजल खरीद की डिजिटल रसीद", "Diesel Purchase Receipt", "Receipt", true, "पंप संचालक द्वारा जारी रसीद जिस पर किसान पंजीकरण संख्या अंकित हो"),
                SchemeDocument("कृषि विभाग किसान पंजीकरण संख्या", "Farmer Registration No.", "Registration", true, "डीबीटी पोर्टल का 13 अंकों का पंजीकरण"),
                SchemeDocument("जमीन का विवरण / बटाईदार सत्यापन", "Land Document / Sharecropper Form", "Land", true, "किसान सलाहकार / वार्ड सदस्य द्वारा सत्यापित")
            )
        ),

        Scheme(
            id = "pm_svanidhi",
            schemeName = "PM Street Vendor's AtmaNirbhar Nidhi (PM SVANidhi)",
            schemeNameHindi = "पीएम स्वनिधि योजना (स्ट्रीट वेंडर आत्मनिर्भर निधि)",
            category = "Employment",
            department = "Ministry of Housing and Urban Affairs",
            governmentLevel = "Central Government",
            applicableState = "All India",
            shortDescription = "Collateral-free working capital micro-loans starting at ₹10,000, progressing to ₹20,000 and ₹50,000 on timely repayment for street vendors.",
            shortDescriptionHindi = "रेहड़ी-पटरी एवं ठेला विक्रेताओं के लिए बिना किसी गारंटी के ₹10,000 का प्रारंभिक कार्यशील पूंजी ऋण। समय पर चुकाने पर 7% ब्याज सब्सिडी।",
            benefitSummary = "Collateral-free loan of ₹10,000 - ₹50,000 with 7% interest subsidy",
            benefitSummaryHindi = "बिना गारंटी ₹10,000 से ₹50,000 का ऋण एवं 7% ब्याज सब्सिडी",
            officialLink = "https://pmsvanidhi.mohua.gov.in",
            sourceReference = "Ministry of Housing and Urban Affairs, GoI",
            lastVerifiedDate = "2026-11-01",
            version = 2,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "svan_occ",
                    attribute = "occupation",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Small Business,Daily Wage Laborer,Vendor,दुकानदार",
                    isMandatory = true,
                    labelHindi = "व्यवसाय: स्ट्रीट वेंडर / फेरीवाला / छोटा दुकानदार",
                    labelEnglish = "Occupation: Street Vendor / Micro Enterprise",
                    failureReasonHindi = "यह योजना केवल फेरीवालों और छोटे रेहड़ी-पटरी विक्रेताओं के लिए है।",
                    failureReasonEnglish = "This scheme is for street vendors and micro retailers."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "पहचान सत्यापन"),
                SchemeDocument("शहरी स्थानीय निकाय द्वारा जारी वेंडिंग प्रमाण पत्र / लेटर ऑफ रिकमेंडेशन (LoR)", "Vending Certificate / LoR", "Business", true, "नगर पालिका/पंचायत द्वारा जारी"),
                SchemeDocument("बैंक खाता पासबुक", "Bank Account Passbook", "Financial", true, "ऋण वितरण हेतु")
            )
        ),

        // --- ALL-INDIA STATE GOVERNMENT SCHEMES ---

        // 1. Uttar Pradesh
        Scheme(
            id = "up_kanya_sumangala",
            schemeName = "Mukhyamantri Kanya Sumangala Yojana",
            schemeNameHindi = "मुख्यमंत्री कन्या सुमंगला योजना (उत्तर प्रदेश)",
            category = "Women & Child",
            department = "Department of Women and Child Development",
            governmentLevel = "State Government",
            applicableState = "Uttar Pradesh",
            shortDescription = "Financial assistance of ₹25,000 in six progressive stages from birth to graduation for girl children born in Uttar Pradesh.",
            shortDescriptionHindi = "उत्तर प्रदेश की बालिकाओं के जन्म से लेकर स्नातक तक की शिक्षा हेतु छह चरणों में कुल ₹25,000 की वित्तीय सहायता।",
            benefitSummary = "₹25,000 financial support across 6 developmental milestones",
            benefitSummaryHindi = "बालिका के जन्म से स्नातक तक कुल ₹25,000 की सहायता",
            officialLink = "https://mksy.up.gov.in",
            sourceReference = "Government of Uttar Pradesh (Official Portal)",
            lastVerifiedDate = "2026-11-06",
            version = 2,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "up_ks_state",
                    attribute = "state",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Uttar Pradesh",
                    isMandatory = true,
                    labelHindi = "मूल निवास: उत्तर प्रदेश",
                    labelEnglish = "Domicile: Uttar Pradesh",
                    failureReasonHindi = "यह योजना केवल उत्तर प्रदेश के स्थायी निवासियों के लिए है।",
                    failureReasonEnglish = "Applicant must be a permanent resident of Uttar Pradesh."
                ),
                EligibilityCriterion(
                    id = "up_ks_gender",
                    attribute = "gender",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Female,महिला",
                    isMandatory = true,
                    labelHindi = "लिंग: बालिका / महिला",
                    labelEnglish = "Gender: Female",
                    failureReasonHindi = "यह योजना केवल बालिकाओं के लिए है।",
                    failureReasonEnglish = "Scheme applies only to girl children."
                ),
                EligibilityCriterion(
                    id = "up_ks_income",
                    attribute = "householdIncome",
                    operator = CriterionOperator.MAX_INCOME,
                    expectedValue = "300000",
                    isMandatory = true,
                    labelHindi = "वार्षिक आय सीमा: ₹3.00 लाख",
                    labelEnglish = "Income Ceiling: Up to ₹3 Lakh",
                    failureReasonHindi = "पारिवारिक आय ₹3 लाख से अधिक है।",
                    failureReasonEnglish = "Annual family income exceeds ₹3,00,000."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("निवास प्रमाण पत्र (UP Domicile)", "UP Domicile Certificate", "Residence", true, "उत्तर प्रदेश मूल निवासी प्रमाण पत्र"),
                SchemeDocument("बालिका का जन्म प्रमाण पत्र", "Birth Certificate", "Identity", true, "सक्षम प्राधिकारी द्वारा जारी"),
                SchemeDocument("अभिभावक का आय प्रमाण पत्र", "Income Certificate", "Income", true, "तहसीलदार द्वारा प्रदत्त (3 लाख तक)")
            )
        ),

        // 2. Maharashtra
        Scheme(
            id = "maha_ladki_bahin",
            schemeName = "Mukhyamantri Majhi Ladki Bahin Yojana",
            schemeNameHindi = "मुख्यमंत्री माझी लाडकी बहीण योजना (महाराष्ट्र)",
            category = "Women & Child",
            department = "Women and Child Development Department",
            governmentLevel = "State Government",
            applicableState = "Maharashtra",
            shortDescription = "Monthly financial allowance of ₹1,500 transferred directly to the bank accounts of economically weaker women aged 21-65 in Maharashtra.",
            shortDescriptionHindi = "महाराष्ट्र की 21 से 65 वर्ष आयु वर्ग की आर्थिक रूप से कमजोर पात्र महिलाओं को प्रति माह ₹1,500 की सीधी आर्थिक सहायता।",
            benefitSummary = "₹1,500 per month direct bank transfer (₹18,000 annually)",
            benefitSummaryHindi = "₹1,500 प्रति माह सीधे बैंक खाते में (वार्षिक ₹18,000)",
            officialLink = "https://ladakibahin.maharashtra.gov.in",
            sourceReference = "Government of Maharashtra (Official Portal)",
            lastVerifiedDate = "2026-11-06",
            version = 1,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "mh_lb_state",
                    attribute = "state",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Maharashtra",
                    isMandatory = true,
                    labelHindi = "मूल निवास: महाराष्ट्र",
                    labelEnglish = "Domicile: Maharashtra",
                    failureReasonHindi = "यह योजना केवल महाराष्ट्र की स्थायी निवासी महिलाओं के लिए है।",
                    failureReasonEnglish = "Restricted to permanent residents of Maharashtra."
                ),
                EligibilityCriterion(
                    id = "mh_lb_gender",
                    attribute = "gender",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Female,महिला",
                    isMandatory = true,
                    labelHindi = "लिंग: महिला",
                    labelEnglish = "Gender: Female",
                    failureReasonHindi = "यह योजना केवल महिलाओं के लिए है।",
                    failureReasonEnglish = "This scheme is exclusively for women."
                ),
                EligibilityCriterion(
                    id = "mh_lb_age",
                    attribute = "age",
                    operator = CriterionOperator.GTE,
                    expectedValue = "21",
                    isMandatory = true,
                    labelHindi = "आयु: 21 से 65 वर्ष",
                    labelEnglish = "Age: 21 to 65 years",
                    failureReasonHindi = "आयु 21 वर्ष से कम है।",
                    failureReasonEnglish = "Applicant must be at least 21 years of age."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "बैंक खाते से लिंक आधार संख्या"),
                SchemeDocument("महाराष्ट्र अधिवास प्रमाण पत्र / राशन कार्ड", "Maharashtra Domicile / Ration Card", "Residence", true, "सत्यापन हेतु"),
                SchemeDocument("डीबीटी सक्षम बैंक पासबुक", "DBT Enabled Bank Passbook", "Financial", true, "आधार से जुड़ा व्यक्तिगत बैंक खाता")
            )
        ),

        // 3. Madhya Pradesh
        Scheme(
            id = "mp_ladli_behna",
            schemeName = "Mukhyamantri Ladli Behna Yojana",
            schemeNameHindi = "मुख्यमंत्री लाड़ली बहना योजना (मध्य प्रदेश)",
            category = "Women & Child",
            department = "Department of Women & Child Development",
            governmentLevel = "State Government",
            applicableState = "Madhya Pradesh",
            shortDescription = "Monthly financial support of ₹1,250 to married, widowed, divorced and abandoned women aged 21-60 years residing in Madhya Pradesh.",
            shortDescriptionHindi = "मध्य प्रदेश की 21 से 60 वर्ष आयु वर्ग की पात्र विवाहित, विधवा, तलाकशुदा एवं परित्यक्ता बहनों को प्रतिमाह ₹1,250 की राशि।",
            benefitSummary = "₹1,250 per month (₹15,000 annually) via DBT",
            benefitSummaryHindi = "₹1,250 प्रति माह सीधे बैंक खाते में (वार्षिक ₹15,000)",
            officialLink = "https://cmladlibahna.mp.gov.in",
            sourceReference = "Government of Madhya Pradesh",
            lastVerifiedDate = "2026-11-06",
            version = 3,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "mp_lb_state",
                    attribute = "state",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Madhya Pradesh",
                    isMandatory = true,
                    labelHindi = "मूल निवास: मध्य प्रदेश",
                    labelEnglish = "Domicile: Madhya Pradesh",
                    failureReasonHindi = "यह योजना केवल मध्य प्रदेश की निवासी महिलाओं के लिए है।",
                    failureReasonEnglish = "Applicable strictly to Madhya Pradesh residents."
                ),
                EligibilityCriterion(
                    id = "mp_lb_gender",
                    attribute = "gender",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Female,महिला",
                    isMandatory = true,
                    labelHindi = "लिंग: महिला",
                    labelEnglish = "Gender: Female",
                    failureReasonHindi = "यह योजना केवल महिलाओं के लिए है।",
                    failureReasonEnglish = "Only female applicants are eligible."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("समग्र आईडी (Samagra ID)", "Samagra ID", "Identity", true, "परिवार एवं सदस्य समग्र आईडी"),
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "आधार ई-केवाईसी"),
                SchemeDocument("व्यक्तिगत बैंक खाता", "Bank Account", "Financial", true, "डीबीटी सक्रिय बैंक खाता")
            )
        ),

        // 4. Rajasthan
        Scheme(
            id = "raj_chiranjeevi_health",
            schemeName = "Mukhyamantri Ayushman Arogya Yojana",
            schemeNameHindi = "मुख्यमंत्री आयुष्मान आरोग्य योजना (राजस्थान)",
            category = "Healthcare",
            department = "Medical, Health & Family Welfare Department",
            governmentLevel = "State Government",
            applicableState = "Rajasthan",
            shortDescription = "Universal health cover providing up to ₹25 lakh cashless treatment per family per year in empanelled government and private hospitals across Rajasthan.",
            shortDescriptionHindi = "राजस्थान के परिवारों को संबद्ध सरकारी एवं निजी अस्पतालों में प्रति परिवार प्रति वर्ष ₹25 लाख तक का कैशलेस उपचार।",
            benefitSummary = "₹25 Lakh cashless healthcare insurance per family annually",
            benefitSummaryHindi = "प्रति परिवार प्रति वर्ष ₹25 लाख तक का कैशलेस इलाज",
            officialLink = "https://health.rajasthan.gov.in",
            sourceReference = "Government of Rajasthan",
            lastVerifiedDate = "2026-11-06",
            version = 2,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "rj_state",
                    attribute = "state",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Rajasthan",
                    isMandatory = true,
                    labelHindi = "मूल निवास: राजस्थान",
                    labelEnglish = "Domicile: Rajasthan",
                    failureReasonHindi = "यह योजना केवल राजस्थान के निवासियों के लिए है।",
                    failureReasonEnglish = "Exclusively for residents of Rajasthan with Jan Aadhaar."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("जन आधार कार्ड (Jan Aadhaar Card)", "Jan Aadhaar Card", "Identity", true, "राजस्थान परिवार पहचान पत्र"),
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "सत्यापन हेतु")
            )
        ),

        // 5. Karnataka
        Scheme(
            id = "kar_gruha_lakshmi",
            schemeName = "Gruha Lakshmi Scheme",
            schemeNameHindi = "गृह लक्ष्मी योजना (कर्नाटक)",
            category = "Women & Child",
            department = "Women and Child Development Department",
            governmentLevel = "State Government",
            applicableState = "Karnataka",
            shortDescription = "Financial assistance of ₹2,000 per month given to the woman head of the family in Karnataka via Direct Benefit Transfer.",
            shortDescriptionHindi = "कर्नाटक में परिवार की महिला मुखिया को प्रतिमाह ₹2,000 की वित्तीय सहायता सीधे बैंक खाते में प्रदान की जाती है।",
            benefitSummary = "₹2,000 per month (₹24,000 annually) DBT",
            benefitSummaryHindi = "₹2,000 प्रति माह सीधे महिला मुखिया के खाते में",
            officialLink = "https://sevasindhu.karnataka.gov.in",
            sourceReference = "Government of Karnataka",
            lastVerifiedDate = "2026-11-06",
            version = 1,
            isPublished = true,
            criteria = listOf(
                EligibilityCriterion(
                    id = "kar_state",
                    attribute = "state",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Karnataka",
                    isMandatory = true,
                    labelHindi = "मूल निवास: कर्नाटक",
                    labelEnglish = "Domicile: Karnataka",
                    failureReasonHindi = "यह योजना केवल कर्नाटक के निवासियों के लिए है।",
                    failureReasonEnglish = "Applicant must be a permanent resident of Karnataka."
                ),
                EligibilityCriterion(
                    id = "kar_gender",
                    attribute = "gender",
                    operator = CriterionOperator.EQ,
                    expectedValue = "Female,महिला",
                    isMandatory = true,
                    labelHindi = "लिंग: महिला (परिवार की मुखिया)",
                    labelEnglish = "Gender: Female Head of Family",
                    failureReasonHindi = "यह योजना परिवार की महिला मुखिया के लिए है।",
                    failureReasonEnglish = "Restricted to the female head of the family."
                )
            ),
            requiredDocuments = listOf(
                SchemeDocument("राशन कार्ड (Ration Card - BPL/APL/AAY)", "Ration Card", "Residence", true, "महिला मुखिया दर्शाने वाला राशन कार्ड"),
                SchemeDocument("आधार कार्ड (Aadhaar Card)", "Aadhaar Card", "Identity", true, "बैंक से लिंक आधार")
            )
        )
    )

    val sampleServiceCentres: List<ServiceCentre> = listOf(
        ServiceCentre(
            id = "csc_nohsa_01",
            name = "कॉमन सर्विस सेन्टर (CSC) - नोहसा",
            type = "CSC Center",
            address = "मुख्य बाजार, नोहसा पंचायत भवन के समीप",
            district = "Patna",
            block = "Phulwari Sharif",
            panchayat = "Nohsa",
            contactPhone = "+91 94310 12345",
            openingHours = "सुबह 9:00 बजे से शाम 6:00 बजे तक",
            distanceKm = 0.8,
            isVerified = true
        ),
        ServiceCentre(
            id = "csc_phulwari_02",
            name = "डिजिटल सेवा केंद्र (CSC) - फुलवारी शरीफ",
            type = "CSC Center",
            address = "थाना रोड, ब्लॉक परिसर के सामने",
            district = "Patna",
            block = "Phulwari Sharif",
            panchayat = "Phulwari",
            contactPhone = "+91 98350 67890",
            openingHours = "सुबह 9:30 बजे से शाम 5:30 बजे तक",
            distanceKm = 2.4,
            isVerified = true
        ),
        ServiceCentre(
            id = "panchayat_bhawan_nohsa",
            name = "ग्राम पंचायत भवन - नोहसा",
            type = "Panchayat Bhawan",
            address = "ग्राम पंचायत कार्यालय, नोहसा",
            district = "Patna",
            block = "Phulwari Sharif",
            panchayat = "Nohsa",
            contactPhone = "+91 94318 00012",
            openingHours = "सोमवार से शनिवार (10:00 AM - 4:00 PM)",
            distanceKm = 0.5,
            isVerified = true
        ),
        ServiceCentre(
            id = "block_office_phulwari",
            name = "प्रखंड एवं अंचल कार्यालय (Block Office)",
            type = "Block Office",
            address = "प्रखंड विकास पदाधिकारी कार्यालय, फुलवारी शरीफ",
            district = "Patna",
            block = "Phulwari Sharif",
            panchayat = "Town",
            contactPhone = "+91 0612 2555123",
            openingHours = "10:00 AM - 5:00 PM (सरकारी कार्यदिवस)",
            distanceKm = 3.1,
            isVerified = true
        )
    )
}
