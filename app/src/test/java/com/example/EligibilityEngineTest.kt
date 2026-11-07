package com.example

import com.example.data.model.CitizenProfile
import com.example.data.model.EligibilityStatus
import com.example.data.repository.SchemeData
import com.example.domain.engine.EligibilityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EligibilityEngineTest {

    private val pmKisanScheme = SchemeData.verifiedSchemes.first { it.id == "pm_kisan" }
    private val bsccScheme = SchemeData.verifiedSchemes.first { it.id == "bihar_student_credit_card" }
    private val kanyaUtthanScheme = SchemeData.verifiedSchemes.first { it.id == "mukhyamantri_kanya_utthan" }
    private val ayushmanScheme = SchemeData.verifiedSchemes.first { it.id == "ayushman_bharat" }

    @Test
    fun pmKisan_eligibleFarmer_returnsPotentiallyEligible() {
        val eligibleFarmer = CitizenProfile(
            userId = "test_user_01",
            fullName = "रामेश्वर कुमार",
            occupation = "Farmer",
            age = 40,
            landholdingAcres = 2.0,
            householdIncome = 100000,
            state = "Bihar"
        )

        val result = EligibilityEngine.evaluate(eligibleFarmer, pmKisanScheme)
        assertEquals(EligibilityStatus.POTENTIALLY_ELIGIBLE, result.status)
        assertTrue(result.satisfiedList.isNotEmpty())
        assertTrue(result.failedList.isEmpty())
    }

    @Test
    fun pmKisan_missingAge_returnsMoreInfoNeeded() {
        val missingProfile = CitizenProfile(
            userId = "test_user_02",
            fullName = "अशोक सिंह",
            occupation = "Farmer",
            age = null,
            landholdingAcres = 1.5
        )

        val result = EligibilityEngine.evaluate(missingProfile, pmKisanScheme)
        assertEquals(EligibilityStatus.MORE_INFO_NEEDED, result.status)
        assertTrue(result.missingList.any { it.criterion.attribute == "age" })
    }

    @Test
    fun pmKisan_nonFarmer_returnsCriteriaNotMet() {
        val nonFarmer = CitizenProfile(
            userId = "test_user_03",
            fullName = "विकास कुमार",
            occupation = "Small Business",
            age = 35,
            landholdingAcres = 0.0
        )

        val result = EligibilityEngine.evaluate(nonFarmer, pmKisanScheme)
        assertEquals(EligibilityStatus.CRITERIA_NOT_MET, result.status)
        assertTrue(result.failedList.any { it.criterion.attribute == "occupation" })
    }

    @Test
    fun bscc_ageBoundary_under25_returnsEligible() {
        val studentUnder25 = CitizenProfile(
            userId = "test_user_04",
            fullName = "अमित वर्मा",
            occupation = "Student",
            age = 22,
            state = "Bihar"
        )

        val result = EligibilityEngine.evaluate(studentUnder25, bsccScheme)
        assertEquals(EligibilityStatus.POTENTIALLY_ELIGIBLE, result.status)
    }

    @Test
    fun bscc_ageBoundary_over25_returnsCriteriaNotMet() {
        val studentOver25 = CitizenProfile(
            userId = "test_user_05",
            fullName = "सुनील राय",
            occupation = "Student",
            age = 28,
            state = "Bihar"
        )

        val result = EligibilityEngine.evaluate(studentOver25, bsccScheme)
        assertEquals(EligibilityStatus.CRITERIA_NOT_MET, result.status)
        assertTrue(result.failedList.any { it.criterion.attribute == "age" })
    }

    @Test
    fun kanyaUtthan_maleGender_returnsCriteriaNotMet() {
        val maleStudent = CitizenProfile(
            userId = "test_user_06",
            fullName = "रोहित कुमार",
            gender = "Male",
            state = "Bihar"
        )

        val result = EligibilityEngine.evaluate(maleStudent, kanyaUtthanScheme)
        assertEquals(EligibilityStatus.CRITERIA_NOT_MET, result.status)
        assertTrue(result.failedList.any { it.criterion.attribute == "gender" })
    }

    @Test
    fun ayushmanBharat_highIncome_returnsCriteriaNotMet() {
        val richFamily = CitizenProfile(
            userId = "test_user_07",
            fullName = "धनीराम",
            householdIncome = 500000,
            hasBplRationCard = true
        )

        val result = EligibilityEngine.evaluate(richFamily, ayushmanScheme)
        assertEquals(EligibilityStatus.CRITERIA_NOT_MET, result.status)
        assertTrue(result.failedList.any { it.criterion.attribute == "householdIncome" })
    }
}
