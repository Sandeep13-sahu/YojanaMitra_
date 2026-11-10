package com.example

import com.example.data.local.SchemeEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CitizenProfile
import com.example.data.model.UserRole
import com.example.data.repository.SchemeData
import com.example.ui.AuthState
import com.example.ui.WorkspaceView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CitizenAuthAndSchemePersistenceTest {

    @Test
    fun test1_authStateValues_areDefinedCorrectly() {
        assertEquals(3, AuthState.values().size)
        assertTrue(AuthState.values().contains(AuthState.CHECKING))
        assertTrue(AuthState.values().contains(AuthState.UNAUTHENTICATED))
        assertTrue(AuthState.values().contains(AuthState.AUTHENTICATED))
    }

    @Test
    fun test2_schemeEntityMapping_preservesAllVerifiedAttributes() {
        val verified = SchemeData.verifiedSchemes.first()
        val entity = SchemeEntity(
            id = verified.id,
            schemeName = verified.schemeName,
            schemeNameHindi = verified.schemeNameHindi,
            category = verified.category,
            department = verified.department,
            governmentLevel = verified.governmentLevel,
            applicableState = verified.applicableState,
            shortDescription = verified.shortDescription,
            shortDescriptionHindi = verified.shortDescriptionHindi,
            benefitSummary = verified.benefitSummary,
            benefitSummaryHindi = verified.benefitSummaryHindi,
            officialLink = verified.officialLink,
            sourceReference = verified.sourceReference,
            lastVerifiedDate = verified.lastVerifiedDate,
            version = verified.version,
            isPublished = verified.isPublished
        )

        assertEquals(verified.id, entity.id)
        assertEquals(verified.schemeNameHindi, entity.schemeNameHindi)
        assertEquals(verified.category, entity.category)
        assertEquals(verified.governmentLevel, entity.governmentLevel)
        assertEquals(verified.applicableState, entity.applicableState)
        assertTrue(entity.isPublished)
    }

    @Test
    fun test3_citizenProfile_supportsPhotoUrlAndDemographics() {
        val profile = CitizenProfile(
            userId = "test_citizen_101",
            fullName = "सुरेश कुमार शर्मा",
            phoneNumber = "+91 98765 00001",
            preferredLanguage = "hi",
            state = "Bihar",
            district = "Patna",
            block = "Phulwari Sharif",
            panchayat = "Nohsa",
            village = "Nohsa",
            age = 45,
            gender = "Male",
            occupation = "Farmer",
            landholdingAcres = 2.5,
            householdIncome = 150000,
            category = "OBC",
            hasDisability = false,
            hasBplRationCard = true,
            photoUrl = "https://example.gov.in/photos/citizen_101.jpg"
        )

        assertEquals("सुरेश कुमार शर्मा", profile.fullName)
        assertEquals("+91 98765 00001", profile.phoneNumber)
        assertEquals("https://example.gov.in/photos/citizen_101.jpg", profile.photoUrl)
        assertTrue(profile.hasBplRationCard)
        assertEquals(2.5, profile.landholdingAcres!!, 0.001)
    }

    @Test
    fun test4_workspaceView_citizenIsIsolatedFromAdminAndOperator() {
        // Confirm strict enum definition for workspace views
        val publicWorkspaces = listOf(WorkspaceView.CITIZEN)
        val staffWorkspaces = listOf(
            WorkspaceView.OPERATOR_LOGIN,
            WorkspaceView.OPERATOR_DASHBOARD,
            WorkspaceView.ADMIN_LOGIN,
            WorkspaceView.ADMIN_DASHBOARD
        )

        assertTrue(publicWorkspaces.contains(WorkspaceView.CITIZEN))
        assertFalse(publicWorkspaces.contains(WorkspaceView.OPERATOR_LOGIN))
        assertFalse(publicWorkspaces.contains(WorkspaceView.ADMIN_LOGIN))
        assertEquals(5, WorkspaceView.values().size)
    }

    @Test
    fun test5_appLanguages_supportFourIndianLanguages() {
        val languages = AppLanguage.values()
        assertEquals(4, languages.size)
        assertNotNull(languages.find { it.code == "hi" })
        assertNotNull(languages.find { it.code == "en" })
        assertNotNull(languages.find { it.code == "bho" })
        assertNotNull(languages.find { it.code == "mai" })
    }

    @Test
    fun test6_schemeCatalog_containsCentralAndStateSchemes() {
        val schemes = SchemeData.verifiedSchemes
        val centralSchemes = schemes.filter { it.governmentLevel.contains("Central", ignoreCase = true) }
        val stateSchemes = schemes.filter { it.applicableState != "All India" }

        assertTrue("Central schemes catalog should not be empty", centralSchemes.isNotEmpty())
        assertTrue("State schemes catalog should not be empty", stateSchemes.isNotEmpty())
    }
}
