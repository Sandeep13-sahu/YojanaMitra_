package com.example.domain.engine

import com.example.data.model.CitizenProfile
import com.example.data.model.CriterionEvaluation
import com.example.data.model.CriterionOperator
import com.example.data.model.EligibilityCriterion
import com.example.data.model.EligibilityEvaluationResult
import com.example.data.model.EligibilityStatus
import com.example.data.model.Scheme

object EligibilityEngine {

    fun evaluate(profile: CitizenProfile?, scheme: Scheme): EligibilityEvaluationResult {
        if (profile == null) {
            val missing = scheme.criteria.map { crit ->
                CriterionEvaluation(
                    criterion = crit,
                    isSatisfied = false,
                    isMissing = true,
                    messageHindi = "${crit.labelHindi}: जानकारी उपलब्ध नहीं है",
                    messageEnglish = "${crit.labelEnglish}: Information missing"
                )
            }
            return EligibilityEvaluationResult(
                schemeId = scheme.id,
                schemeName = scheme.schemeName,
                schemeNameHindi = scheme.schemeNameHindi,
                status = EligibilityStatus.MORE_INFO_NEEDED,
                satisfiedList = emptyList(),
                failedList = emptyList(),
                missingList = missing,
                summaryHindi = "कृपया अपनी पात्रता जानने के लिए अपना विवरण पूरा करें।",
                summaryEnglish = "Please complete your profile to check eligibility.",
                officialSource = scheme.sourceReference,
                ruleVersion = scheme.version,
                lastVerifiedDate = scheme.lastVerifiedDate
            )
        }

        val satisfied = mutableListOf<CriterionEvaluation>()
        val failed = mutableListOf<CriterionEvaluation>()
        val missing = mutableListOf<CriterionEvaluation>()

        for (criterion in scheme.criteria) {
            val eval = evaluateSingleCriterion(profile, criterion)
            when {
                eval.isMissing -> missing.add(eval)
                eval.isSatisfied -> satisfied.add(eval)
                else -> failed.add(eval)
            }
        }

        // Deterministic status resolution:
        // 1. If any mandatory criterion failed -> CRITERIA_NOT_MET
        // 2. Else if any mandatory criterion missing -> MORE_INFO_NEEDED
        // 3. Else -> POTENTIALLY_ELIGIBLE
        val hasMandatoryFailure = failed.any { it.criterion.isMandatory }
        val hasMandatoryMissing = missing.any { it.criterion.isMandatory }

        val status = when {
            hasMandatoryFailure -> EligibilityStatus.CRITERIA_NOT_MET
            hasMandatoryMissing -> EligibilityStatus.MORE_INFO_NEEDED
            else -> EligibilityStatus.POTENTIALLY_ELIGIBLE
        }

        val summaryHindi = when (status) {
            EligibilityStatus.POTENTIALLY_ELIGIBLE ->
                "आप इस योजना के सभी आवश्यक मानदंडों को पूरा करते प्रतीत होते हैं। आवश्यक दस्तावेज तैयार कर आवेदन करें।"
            EligibilityStatus.MORE_INFO_NEEDED ->
                "पात्रता की पुष्टि के लिए ${missing.size} जानकारी की आवश्यकता है: " +
                        missing.joinToString(", ") { it.criterion.labelHindi }
            EligibilityStatus.CRITERIA_NOT_MET ->
                "आप निम्नलिखित अनिवार्य शर्तों को पूरा नहीं करते हैं: " +
                        failed.filter { it.criterion.isMandatory }.joinToString("; ") { it.criterion.failureReasonHindi.ifBlank { it.criterion.labelHindi } }
        }

        val summaryEnglish = when (status) {
            EligibilityStatus.POTENTIALLY_ELIGIBLE ->
                "You appear to meet all required criteria for this scheme based on your profile."
            EligibilityStatus.MORE_INFO_NEEDED ->
                "Additional information needed for ${missing.size} field(s): " +
                        missing.joinToString(", ") { it.criterion.labelEnglish }
            EligibilityStatus.CRITERIA_NOT_MET ->
                "Mandatory criteria not met: " +
                        failed.filter { it.criterion.isMandatory }.joinToString("; ") { it.criterion.failureReasonEnglish.ifBlank { it.criterion.labelEnglish } }
        }

        return EligibilityEvaluationResult(
            schemeId = scheme.id,
            schemeName = scheme.schemeName,
            schemeNameHindi = scheme.schemeNameHindi,
            status = status,
            satisfiedList = satisfied,
            failedList = failed,
            missingList = missing,
            summaryHindi = summaryHindi,
            summaryEnglish = summaryEnglish,
            officialSource = scheme.sourceReference,
            ruleVersion = scheme.version,
            lastVerifiedDate = scheme.lastVerifiedDate
        )
    }

    private fun evaluateSingleCriterion(
        profile: CitizenProfile,
        criterion: EligibilityCriterion
    ): CriterionEvaluation {
        return when (criterion.attribute) {
            "age" -> {
                val age = profile.age
                if (age == null) {
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = false,
                        isMissing = true,
                        messageHindi = "आयु दर्ज नहीं है",
                        messageEnglish = "Age is not entered"
                    )
                } else {
                    val target = criterion.expectedValue.toIntOrNull() ?: 18
                    val passed = when (criterion.operator) {
                        CriterionOperator.GTE -> age >= target
                        CriterionOperator.LTE -> age <= target
                        CriterionOperator.EQ -> age == target
                        else -> true
                    }
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = passed,
                        isMissing = false,
                        messageHindi = if (passed) "आयु (${age} वर्ष) मान्य है" else criterion.failureReasonHindi,
                        messageEnglish = if (passed) "Age (${age} yrs) satisfies requirement" else criterion.failureReasonEnglish
                    )
                }
            }

            "landholdingAcres" -> {
                val land = profile.landholdingAcres
                if (land == null) {
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = false,
                        isMissing = true,
                        messageHindi = "कृषि भूमि का विवरण दर्ज नहीं है",
                        messageEnglish = "Landholding detail is not entered"
                    )
                } else {
                    val maxLand = criterion.expectedValue.toDoubleOrNull() ?: 5.0
                    val passed = land <= maxLand
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = passed,
                        isMissing = false,
                        messageHindi = if (passed) "भूमि (${land} एकड़) सीमा (${maxLand} एकड़) के अंतर्गत है" else criterion.failureReasonHindi,
                        messageEnglish = if (passed) "Landholding (${land} acres) is within limit (${maxLand} acres)" else criterion.failureReasonEnglish
                    )
                }
            }

            "householdIncome" -> {
                val income = profile.householdIncome
                if (income == null) {
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = false,
                        isMissing = true,
                        messageHindi = "पारिवारिक आय दर्ज नहीं है",
                        messageEnglish = "Household income is not entered"
                    )
                } else {
                    val maxIncome = criterion.expectedValue.toIntOrNull() ?: 250000
                    val passed = income <= maxIncome
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = passed,
                        isMissing = false,
                        messageHindi = if (passed) "आय (₹${income}) सीमा (₹${maxIncome}) के अंतर्गत है" else criterion.failureReasonHindi,
                        messageEnglish = if (passed) "Income (₹${income}) is within ceiling (₹${maxIncome})" else criterion.failureReasonEnglish
                    )
                }
            }

            "occupation" -> {
                val occ = profile.occupation.trim()
                if (occ.isBlank()) {
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = false,
                        isMissing = true,
                        messageHindi = "व्यवसाय दर्ज नहीं है",
                        messageEnglish = "Occupation is not entered"
                    )
                } else {
                    val expectedList = criterion.expectedValue.split(",").map { it.trim().lowercase() }
                    val passed = expectedList.contains(occ.lowercase()) ||
                            (occ.equals("Farmer", ignoreCase = true) && expectedList.contains("farmer")) ||
                            (occ.equals("किसान", ignoreCase = true) && expectedList.contains("farmer"))
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = passed,
                        isMissing = false,
                        messageHindi = if (passed) "व्यवसाय (${occ}) मान्य है" else criterion.failureReasonHindi,
                        messageEnglish = if (passed) "Occupation (${occ}) matches requirement" else criterion.failureReasonEnglish
                    )
                }
            }

            "state" -> {
                val state = profile.state.trim()
                if (state.isBlank()) {
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = false,
                        isMissing = true,
                        messageHindi = "राज्य की जानकारी दर्ज नहीं है",
                        messageEnglish = "State is not entered"
                    )
                } else {
                    val expected = criterion.expectedValue.trim()
                    val passed = expected.equals("All India", ignoreCase = true) || state.equals(expected, ignoreCase = true)
                    CriterionEvaluation(
                        criterion = criterion,
                        isSatisfied = passed,
                        isMissing = false,
                        messageHindi = if (passed) "राज्य (${state}) योजना क्षेत्र में है" else criterion.failureReasonHindi,
                        messageEnglish = if (passed) "State (${state}) is eligible" else criterion.failureReasonEnglish
                    )
                }
            }

            "gender" -> {
                val gender = profile.gender.trim()
                val expected = criterion.expectedValue.trim()
                val passed = expected.isBlank() || expected.equals("Any", ignoreCase = true) || gender.equals(expected, ignoreCase = true)
                CriterionEvaluation(
                    criterion = criterion,
                    isSatisfied = passed,
                    isMissing = false,
                    messageHindi = if (passed) "लिंग (${gender}) मान्य है" else criterion.failureReasonHindi,
                    messageEnglish = if (passed) "Gender (${gender}) satisfies condition" else criterion.failureReasonEnglish
                )
            }

            "hasBplRationCard" -> {
                val passed = profile.hasBplRationCard
                CriterionEvaluation(
                    criterion = criterion,
                    isSatisfied = passed,
                    isMissing = false,
                    messageHindi = if (passed) "बीपीएल/राशन कार्ड उपलब्ध है" else criterion.failureReasonHindi,
                    messageEnglish = if (passed) "BPL ration card condition satisfied" else criterion.failureReasonEnglish
                )
            }

            "hasDisability" -> {
                val passed = profile.hasDisability
                CriterionEvaluation(
                    criterion = criterion,
                    isSatisfied = passed,
                    isMissing = false,
                    messageHindi = if (passed) "दिव्यांगता प्रमाण पत्र उपलब्ध है" else criterion.failureReasonHindi,
                    messageEnglish = if (passed) "Disability criterion satisfied" else criterion.failureReasonEnglish
                )
            }

            else -> {
                // Generic true fallback if unknown criterion
                CriterionEvaluation(
                    criterion = criterion,
                    isSatisfied = true,
                    isMissing = false,
                    messageHindi = "${criterion.labelHindi} मान्य",
                    messageEnglish = "${criterion.labelEnglish} satisfied"
                )
            }
        }
    }
}
