package com.example

import com.example.data.model.ApplicationCase
import com.example.data.model.Jurisdiction
import com.example.data.model.OperatorAccount
import com.example.data.model.UserRole
import com.example.domain.security.RBACSecurityManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pure JVM Unit Tests for Role-Based Access Control (RBAC), Jurisdiction Isolation,
 * and Workspace Security Policies in YojanaMitra.
 */
class RBACAndWorkspaceSecurityTest {

    private lateinit var sampleAccounts: List<OperatorAccount>
    private lateinit var sampleCases: List<ApplicationCase>

    @Before
    fun setUp() {
        sampleAccounts = listOf(
            OperatorAccount(
                id = "op_nohsa_01",
                operatorId = "operator_ramesh",
                fullName = "रमेश कुमार (Ramesh Kumar)",
                email = "operator.nohsa@csc.gov.in",
                phoneNumber = "+91 94310 11223",
                employeeId = "CSC-BR-PAT-042",
                jurisdiction = Jurisdiction(
                    state = "Bihar",
                    district = "Patna",
                    block = "Phulwari Sharif",
                    panchayat = "Nohsa"
                ),
                status = "ACTIVE",
                assignedCentreId = "csc_nohsa"
            ),
            OperatorAccount(
                id = "op_danapur_02",
                operatorId = "operator_suresh",
                fullName = "सुरेश यादव (Suresh Yadav)",
                email = "operator.danapur@csc.gov.in",
                phoneNumber = "+91 94310 44556",
                employeeId = "CSC-BR-PAT-089",
                jurisdiction = Jurisdiction(
                    state = "Bihar",
                    district = "Patna",
                    block = "Danapur",
                    panchayat = "Danapur_Cantonment"
                ),
                status = "ACTIVE",
                assignedCentreId = "csc_danapur"
            ),
            OperatorAccount(
                id = "op_suspended_03",
                operatorId = "operator_suspended",
                fullName = "अनिल कुमार (Anil Kumar)",
                email = "suspended.op@csc.gov.in",
                phoneNumber = "+91 94310 99887",
                employeeId = "CSC-BR-PAT-999",
                jurisdiction = Jurisdiction(
                    state = "Bihar",
                    district = "Patna",
                    block = "Phulwari Sharif",
                    panchayat = "Nohsa"
                ),
                status = "SUSPENDED",
                assignedCentreId = "csc_nohsa"
            )
        )

        sampleCases = listOf(
            ApplicationCase(
                id = "case_01",
                userId = "citizen_01",
                citizenName = "रामेश्वर कुमार",
                schemeId = "pm_kisan",
                schemeName = "PM Kisan Samman Nidhi",
                referenceNumber = "BR-PAT-2026-9812",
                submissionDate = "2026-10-15",
                channel = "CSC Center Nohsa",
                status = "Submitted",
                statusSource = "Operator Assisted",
                operatorNotes = "LPC uploaded",
                state = "Bihar",
                district = "Patna",
                block = "Phulwari Sharif",
                panchayat = "Nohsa"
            ),
            ApplicationCase(
                id = "case_02",
                userId = "citizen_02",
                citizenName = "सीता देवी",
                schemeId = "ayushman_bharat",
                schemeName = "Ayushman Bharat PM-JAY",
                referenceNumber = "PMJAY-BR-78192",
                submissionDate = "2026-10-28",
                channel = "Panchayat Bhawan",
                status = "Approved",
                statusSource = "Official Portal Verified",
                operatorNotes = "Golden Card issued",
                state = "Bihar",
                district = "Patna",
                block = "Phulwari Sharif",
                panchayat = "Nohsa"
            ),
            ApplicationCase(
                id = "case_03",
                userId = "citizen_danapur_99",
                citizenName = "अमित सिंह",
                schemeId = "pmay_gramin",
                schemeName = "Pradhan Mantri Awas Yojana Gramin",
                referenceNumber = "PMAY-BR-55412",
                submissionDate = "2026-11-02",
                channel = "CSC Danapur",
                status = "Under Review",
                statusSource = "CSC Verification",
                operatorNotes = "Site inspection complete",
                state = "Bihar",
                district = "Patna",
                block = "Danapur",
                panchayat = "Danapur_Cantonment"
            )
        )
    }

    @Test
    fun test1_operatorLogin_invalidIdentifier_returnsHttp403Forbidden() {
        val result = RBACSecurityManager.authenticateOperator(
            employeeId = "FAKE-EMP-000",
            passcode = "1234",
            accounts = sampleAccounts
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("403") == true)
    }

    @Test
    fun test2_operatorLogin_invalidPasscode_returnsHttp401Unauthorized() {
        val result = RBACSecurityManager.authenticateOperator(
            employeeId = "CSC-BR-PAT-042",
            passcode = "WRONG_PASS",
            accounts = sampleAccounts
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("401") == true)
    }

    @Test
    fun test3_operatorLogin_suspendedAccount_returnsHttp403Forbidden() {
        val result = RBACSecurityManager.authenticateOperator(
            employeeId = "CSC-BR-PAT-999",
            passcode = "1234",
            accounts = sampleAccounts
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("SUSPENDED") == true)
    }

    @Test
    fun test4_operatorLogin_validCredentials_succeedsWithJurisdiction() {
        val result = RBACSecurityManager.authenticateOperator(
            employeeId = "CSC-BR-PAT-042",
            passcode = "1234",
            accounts = sampleAccounts
        )
        assertTrue(result.isSuccess)
        val op = result.getOrNull()
        assertNotNull(op)
        assertEquals("Nohsa", op?.jurisdiction?.panchayat)
        assertEquals("Patna", op?.jurisdiction?.district)
        assertEquals("Phulwari Sharif", op?.jurisdiction?.block)
    }

    @Test
    fun test5_jurisdictionDataIsolation_operatorSeesOnlyAssignedPanchayat() {
        val nohsaOperator = sampleAccounts.first { it.employeeId == "CSC-BR-PAT-042" }
        val filteredCases = RBACSecurityManager.filterCasesForOperator(sampleCases, nohsaOperator)

        assertEquals(2, filteredCases.size)
        assertTrue(filteredCases.all { it.panchayat == "Nohsa" })
        assertFalse(filteredCases.any { it.panchayat == "Danapur_Cantonment" })
    }

    @Test
    fun test6_jurisdictionDataIsolation_danapurOperatorSeesOnlyDanapurCases() {
        val danapurOperator = sampleAccounts.first { it.employeeId == "CSC-BR-PAT-089" }
        val filteredCases = RBACSecurityManager.filterCasesForOperator(sampleCases, danapurOperator)

        assertEquals(1, filteredCases.size)
        assertEquals("Danapur_Cantonment", filteredCases.first().panchayat)
        assertFalse(filteredCases.any { it.panchayat == "Nohsa" })
    }

    @Test
    fun test7_adminLogin_invalidPasskey_returnsHttp403Forbidden() {
        val result = RBACSecurityManager.authenticateAdmin("WrongPasskey123")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("403") == true)
    }

    @Test
    fun test8_adminLogin_validPasskey_succeeds() {
        val result = RBACSecurityManager.authenticateAdmin("Admin@2026!")
        assertTrue(result.isSuccess)
    }

    @Test
    fun test9_rbacRolePermissions_superAdminCanModifyCatalogAndManageOperators() {
        assertTrue(RBACSecurityManager.canModifySchemeCatalog(UserRole.SUPER_ADMIN))
        assertFalse(RBACSecurityManager.canModifySchemeCatalog(UserRole.CITIZEN))
        assertFalse(RBACSecurityManager.canModifySchemeCatalog(UserRole.PANCHAYAT_OPERATOR))

        assertTrue(RBACSecurityManager.canManageOperators(UserRole.SUPER_ADMIN))
        assertFalse(RBACSecurityManager.canManageOperators(UserRole.PANCHAYAT_OPERATOR))
        assertFalse(RBACSecurityManager.canManageOperators(UserRole.CITIZEN))
    }

    @Test
    fun test10_rbacRolePermissions_operatorWorkspaceAccess() {
        assertTrue(RBACSecurityManager.canAccessOperatorWorkspace(UserRole.PANCHAYAT_OPERATOR))
        assertTrue(RBACSecurityManager.canAccessOperatorWorkspace(UserRole.SUPER_ADMIN))
        assertFalse(RBACSecurityManager.canAccessOperatorWorkspace(UserRole.CITIZEN))
    }
}
