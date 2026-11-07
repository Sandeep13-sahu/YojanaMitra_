package com.example.domain.security

import com.example.data.model.ApplicationCase
import com.example.data.model.AuditLog
import com.example.data.model.Jurisdiction
import com.example.data.model.OperatorAccount
import com.example.data.model.UserRole
import com.example.ui.WorkspaceView

/**
 * Domain-level Role-Based Access Control (RBAC) and Security Policy Manager.
 * Encapsulates backend authorization rules, jurisdiction filtering, and access control policies.
 */
object RBACSecurityManager {

    const val ADMIN_MASTER_PASSKEY: String = "Admin@2026!"

    /**
     * Authenticates an operator against active accounts.
     * Enforces:
     * - Existence of operator account
     * - Passcode verification
     * - Strict rejection if account status is SUSPENDED or DEACTIVATED (HTTP 403)
     */
    fun authenticateOperator(
        employeeId: String,
        passcode: String,
        accounts: List<OperatorAccount>
    ): Result<OperatorAccount> {
        val op = accounts.find {
            it.employeeId.trim().equals(employeeId.trim(), ignoreCase = true) ||
            it.email.trim().equals(employeeId.trim(), ignoreCase = true)
        } ?: return Result.failure(
            SecurityException("HTTP 403: ऑपरेटर खाता नहीं मिला या अधिकृत नहीं है (Forbidden: Operator account not found)")
        )

        if (passcode.trim() != "1234") {
            return Result.failure(
                SecurityException("HTTP 401: अमान्य सुरक्षा पासकोड (Unauthorized: Invalid Passcode)")
            )
        }

        if (op.status == "SUSPENDED") {
            return Result.failure(
                SecurityException("HTTP 403: यह खाता निलंबित है। कृपया सुपर एडमिन से संपर्क करें। (Forbidden: Account SUSPENDED)")
            )
        }

        if (op.status == "DEACTIVATED") {
            return Result.failure(
                SecurityException("HTTP 403: यह खाता निष्क्रिय कर दिया गया है। (Forbidden: Account DEACTIVATED)")
            )
        }

        return Result.success(op)
    }

    /**
     * Authenticates Super Admin using secure passkey.
     */
    fun authenticateAdmin(passkey: String): Result<Unit> {
        return if (passkey.trim() == ADMIN_MASTER_PASSKEY) {
            Result.success(Unit)
        } else {
            Result.failure(
                SecurityException("HTTP 403: अमान्य एडमिन सुरक्षा कुंजी (Forbidden: Invalid Admin Passkey)")
            )
        }
    }

    /**
     * Enforces Jurisdiction-level data isolation.
     * Operators can ONLY access application cases that match their assigned panchayat.
     */
    fun filterCasesForOperator(
        allCases: List<ApplicationCase>,
        operator: OperatorAccount?
    ): List<ApplicationCase> {
        if (operator == null) return emptyList()
        val assignedPanchayat = operator.jurisdiction.panchayat
        return allCases.filter { it.panchayat.equals(assignedPanchayat, ignoreCase = true) }
    }

    /**
     * Evaluates permission for sensitive operations.
     */
    fun canModifySchemeCatalog(role: UserRole): Boolean {
        return role == UserRole.SUPER_ADMIN
    }

    fun canManageOperators(role: UserRole): Boolean {
        return role == UserRole.SUPER_ADMIN
    }

    fun canAccessOperatorWorkspace(role: UserRole): Boolean {
        return role == UserRole.PANCHAYAT_OPERATOR || role == UserRole.SUPER_ADMIN
    }
}
