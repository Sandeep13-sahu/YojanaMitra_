package com.example.domain.engine

/**
 * Universal National Jurisdiction Registry:
 * Contains the complete list of all 28 Indian States and 8 Union Territories,
 * along with major administrative districts and portal metadata.
 */
object NationalJurisdictionData {

    val STATES_28: List<String> = listOf(
        "Andhra Pradesh",
        "Arunachal Pradesh",
        "Assam",
        "Bihar",
        "Chhattisgarh",
        "Goa",
        "Gujarat",
        "Haryana",
        "Himachal Pradesh",
        "Jharkhand",
        "Karnataka",
        "Kerala",
        "Madhya Pradesh",
        "Maharashtra",
        "Manipur",
        "Meghalaya",
        "Mizoram",
        "Nagaland",
        "Odisha",
        "Punjab",
        "Rajasthan",
        "Sikkim",
        "Tamil Nadu",
        "Telangana",
        "Tripura",
        "Uttar Pradesh",
        "Uttarakhand",
        "West Bengal"
    )

    val UNION_TERRITORIES_8: List<String> = listOf(
        "Andaman and Nicobar Islands",
        "Chandigarh",
        "Dadra and Nagar Haveli and Daman and Diu",
        "Delhi",
        "Jammu and Kashmir",
        "Ladakh",
        "Lakshadweep",
        "Puducherry"
    )

    val ALL_JURISDICTIONS: List<String> = STATES_28 + UNION_TERRITORIES_8

    val STATE_HINDI_NAMES: Map<String, String> = mapOf(
        "Andhra Pradesh" to "आंध्र प्रदेश",
        "Arunachal Pradesh" to "अरुणाचल प्रदेश",
        "Assam" to "असम",
        "Bihar" to "बिहार",
        "Chhattisgarh" to "छत्तीसगढ़",
        "Goa" to "गोवा",
        "Gujarat" to "गुजरात",
        "Haryana" to "हरियाणा",
        "Himachal Pradesh" to "हिमाचल प्रदेश",
        "Jharkhand" to "झारखंड",
        "Karnataka" to "कर्नाटक",
        "Kerala" to "केरल",
        "Madhya Pradesh" to "मध्य प्रदेश",
        "Maharashtra" to "महाराष्ट्र",
        "Manipur" to "मणिपुर",
        "Meghalaya" to "मेघालय",
        "Mizoram" to "मिजोरम",
        "Nagaland" to "नागालैंड",
        "Odisha" to "ओडिशा",
        "Punjab" to "पंजाब",
        "Rajasthan" to "राजस्थान",
        "Sikkim" to "सिक्किम",
        "Tamil Nadu" to "तमिलनाडु",
        "Telangana" to "तेलंगाना",
        "Tripura" to "त्रिपुरा",
        "Uttar Pradesh" to "उत्तर प्रदेश",
        "Uttarakhand" to "उत्तराखंड",
        "West Bengal" to "पश्चिम बंगाल",
        "Delhi" to "दिल्ली (NCT)",
        "Jammu and Kashmir" to "जम्मू और कश्मीर",
        "Ladakh" to "लद्दाख",
        "Chandigarh" to "चंडीगढ़",
        "Puducherry" to "पुदुचेरी",
        "Andaman and Nicobar Islands" to "अंडमान और निकोबार",
        "Dadra and Nagar Haveli and Daman and Diu" to "दादरा एवं नगर हवेली",
        "Lakshadweep" to "लक्षद्वीप"
    )

    val SAMPLE_DISTRICTS: Map<String, List<String>> = mapOf(
        "Bihar" to listOf("Patna", "Gaya", "Muzaffarpur", "Bhagalpur", "Darbhanga", "Purnia", "Rohtas", "Nalanda", "Samastipur", "Saran"),
        "Uttar Pradesh" to listOf("Lucknow", "Varanasi", "Gorakhpur", "Kanpur", "Prayagraj", "Agra", "Meerut", "Bareilly", "Ayodhya"),
        "Maharashtra" to listOf("Mumbai", "Pune", "Nagpur", "Nashik", "Thane", "Aurangabad", "Solapur", "Amravati"),
        "Madhya Pradesh" to listOf("Bhopal", "Indore", "Jabalpur", "Gwalior", "Ujjain", "Sagar"),
        "Rajasthan" to listOf("Jaipur", "Jodhpur", "Kota", "Bikaner", "Ajmer", "Udaipur"),
        "Karnataka" to listOf("Bengaluru Urban", "Mysuru", "Hubballi-Dharwad", "Belagavi", "Mangaluru"),
        "Tamil Nadu" to listOf("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem"),
        "West Bengal" to listOf("Kolkata", "Howrah", "North 24 Parganas", "South 24 Parganas", "Darjeeling"),
        "Delhi" to listOf("Central Delhi", "New Delhi", "North Delhi", "South Delhi", "East Delhi")
    )

    /**
     * Evaluates whether a scheme is applicable to a given citizen jurisdiction.
     */
    fun isSchemeApplicable(
        schemeApplicableState: String,
        citizenState: String
    ): Boolean {
        if (schemeApplicableState.equals("All India", ignoreCase = true) ||
            schemeApplicableState.equals("National", ignoreCase = true) ||
            schemeApplicableState.equals("Central", ignoreCase = true)) {
            return true
        }

        return schemeApplicableState.equals(citizenState.trim(), ignoreCase = true)
    }
}
