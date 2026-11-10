package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.ApplicationCase
import com.example.data.model.CitizenProfile
import com.example.data.model.SavedScheme
import com.example.data.model.Scheme
import com.example.data.model.ServiceCentre
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class YojanaRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth get() = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google to access Firestore.")
    }

    // --- Schemes ---

    fun getVerifiedSchemes(): List<Scheme> {
        return SchemeData.verifiedSchemes
    }

    // --- Citizen Profile ---

    fun observeCitizenProfile(userId: String = requireUserId()): Flow<CitizenProfile?> = flow {
        val docRef = db.collection("citizen_profiles").document(userId)
        docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(CitizenProfile::class.java)
                } else {
                    null
                }
            }
            .catch { error ->
                handleFirestoreError(error as Exception, OperationType.READ, docRef.path)
                emit(null)
            }
            .collect { emit(it) }
    }

    suspend fun saveCitizenProfile(profile: CitizenProfile): Result<Unit> {
        return try {
            val uid = requireUserId()
            val docRef = db.collection("citizen_profiles").document(uid)
            val data = hashMapOf<String, Any?>(
                "userId" to uid,
                "fullName" to profile.fullName,
                "phoneNumber" to profile.phoneNumber,
                "preferredLanguage" to profile.preferredLanguage,
                "state" to profile.state,
                "district" to profile.district,
                "block" to profile.block,
                "panchayat" to profile.panchayat,
                "village" to profile.village,
                "age" to profile.age,
                "gender" to profile.gender,
                "occupation" to profile.occupation,
                "landholdingAcres" to profile.landholdingAcres,
                "householdIncome" to profile.householdIncome,
                "category" to profile.category,
                "hasDisability" to profile.hasDisability,
                "hasBplRationCard" to profile.hasBplRationCard,
                "consentGiven" to profile.consentGiven,
                "operatorId" to profile.operatorId,
                "photoUrl" to profile.photoUrl,
                "createdAt" to (profile.createdAt ?: FieldValue.serverTimestamp()),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "citizen_profiles/${profile.userId}")
            Result.failure(e)
        }
    }

    // Assisted profile save by Panchayat Operator
    suspend fun saveAssistedCitizenProfile(profile: CitizenProfile, operatorId: String): Result<String> {
        return try {
            requireUserId()
            val docId = if (profile.id.isNotBlank()) profile.id else "cit_${System.currentTimeMillis()}"
            val docRef = db.collection("citizen_profiles").document(docId)
            val data = hashMapOf<String, Any?>(
                "userId" to (if (profile.userId.isNotBlank()) profile.userId else docId),
                "fullName" to profile.fullName,
                "phoneNumber" to profile.phoneNumber,
                "preferredLanguage" to profile.preferredLanguage,
                "state" to profile.state,
                "district" to profile.district,
                "block" to profile.block,
                "panchayat" to profile.panchayat,
                "village" to profile.village,
                "age" to profile.age,
                "gender" to profile.gender,
                "occupation" to profile.occupation,
                "landholdingAcres" to profile.landholdingAcres,
                "householdIncome" to profile.householdIncome,
                "category" to profile.category,
                "hasDisability" to profile.hasDisability,
                "hasBplRationCard" to profile.hasBplRationCard,
                "consentGiven" to profile.consentGiven,
                "operatorId" to operatorId,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.set(data).await()
            Result.success(docId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "citizen_profiles/assisted")
            Result.failure(e)
        }
    }

    // --- Application Cases ---

    fun observeUserApplications(userId: String = requireUserId()): Flow<List<ApplicationCase>> = flow {
        val query = db.collection("application_cases")
            .whereEqualTo("userId", userId)

        query.snapshots()
            .map { snap -> snap.toObjects(ApplicationCase::class.java) }
            .catch { error ->
                handleFirestoreError(error as Exception, OperationType.LIST, "application_cases")
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun createApplicationCase(
        schemeId: String,
        schemeName: String,
        citizenName: String,
        referenceNumber: String,
        channel: String,
        operatorNotes: String = ""
    ): Result<String> {
        return try {
            val uid = requireUserId()
            val collectionRef = db.collection("application_cases")
            val docRef = collectionRef.document()
            val data = hashMapOf<String, Any?>(
                "userId" to uid,
                "citizenName" to citizenName,
                "schemeId" to schemeId,
                "schemeName" to schemeName,
                "referenceNumber" to referenceNumber,
                "submissionDate" to "2026-11-06",
                "channel" to channel,
                "status" to "Submitted",
                "statusSource" to "Citizen Registered",
                "operatorNotes" to operatorNotes,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "application_cases")
            Result.failure(e)
        }
    }

    suspend fun updateCaseStatus(caseId: String, newStatus: String, notes: String = ""): Result<Unit> {
        return try {
            requireUserId()
            val docRef = db.collection("application_cases").document(caseId)
            docRef.update(
                mapOf(
                    "status" to newStatus,
                    "operatorNotes" to notes,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "application_cases/$caseId")
            Result.failure(e)
        }
    }

    // --- Saved Schemes ---

    fun observeSavedSchemes(userId: String = requireUserId()): Flow<List<SavedScheme>> = flow {
        val query = db.collection("saved_schemes").whereEqualTo("userId", userId)
        query.snapshots()
            .map { snap -> snap.toObjects(SavedScheme::class.java) }
            .catch { error ->
                handleFirestoreError(error as Exception, OperationType.LIST, "saved_schemes")
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun toggleSaveScheme(scheme: Scheme, currentlySaved: Boolean): Result<Unit> {
        return try {
            val uid = requireUserId()
            val docId = "${uid}_${scheme.id}"
            val docRef = db.collection("saved_schemes").document(docId)
            if (currentlySaved) {
                docRef.delete().await()
            } else {
                val data = hashMapOf(
                    "userId" to uid,
                    "schemeId" to scheme.id,
                    "schemeName" to scheme.schemeName,
                    "category" to scheme.category,
                    "savedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "saved_schemes")
            Result.failure(e)
        }
    }

    // --- Service Centres ---

    fun getServiceCentres(): List<ServiceCentre> {
        return SchemeData.sampleServiceCentres
    }

    // --- Role-Based Access Control (RBAC) & Authority Validation ---

    suspend fun getUserRole(userId: String): Result<com.example.data.model.UserRoleRecord?> {
        return try {
            val doc = db.collection("user_roles").document(userId).get().await()
            if (doc.exists()) {
                Result.success(doc.toObject(com.example.data.model.UserRoleRecord::class.java))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.READ, "user_roles/$userId")
            Result.failure(e)
        }
    }

    suspend fun setUserRole(
        userId: String,
        role: String,
        email: String,
        status: String = "ACTIVE",
        jurisdiction: com.example.data.model.Jurisdiction = com.example.data.model.Jurisdiction()
    ): Result<Unit> {
        return try {
            val docRef = db.collection("user_roles").document(userId)
            val data = hashMapOf(
                "userId" to userId,
                "role" to role,
                "status" to status,
                "email" to email,
                "jurisdictionState" to jurisdiction.state,
                "jurisdictionDistrict" to jurisdiction.district,
                "jurisdictionBlock" to jurisdiction.block,
                "jurisdictionPanchayat" to jurisdiction.panchayat,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "user_roles/$userId")
            Result.failure(e)
        }
    }

    // --- Operator Jurisdiction-Isolated Case Queries ---

    fun observeJurisdictionCases(panchayat: String): Flow<List<ApplicationCase>> = flow {
        val query = db.collection("application_cases")
            .whereEqualTo("panchayat", panchayat)

        query.snapshots()
            .map { snap -> snap.toObjects(ApplicationCase::class.java) }
            .catch { error ->
                handleFirestoreError(error as Exception, OperationType.LIST, "application_cases/jurisdiction")
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    // --- Super Admin: Operator Management ---

    fun observeOperatorAccounts(): Flow<List<com.example.data.model.OperatorAccount>> = flow {
        val query = db.collection("operator_accounts")
        query.snapshots()
            .map { snap -> snap.toObjects(com.example.data.model.OperatorAccount::class.java) }
            .catch { error ->
                handleFirestoreError(error as Exception, OperationType.LIST, "operator_accounts")
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun saveOperatorAccount(account: com.example.data.model.OperatorAccount): Result<Unit> {
        return try {
            val docRef = db.collection("operator_accounts").document(account.operatorId)
            val data = hashMapOf(
                "operatorId" to account.operatorId,
                "fullName" to account.fullName,
                "email" to account.email,
                "phoneNumber" to account.phoneNumber,
                "employeeId" to account.employeeId,
                "status" to account.status,
                "assignedCentreId" to account.assignedCentreId,
                "assignedBy" to account.assignedBy,
                "jurisdiction" to hashMapOf(
                    "state" to account.jurisdiction.state,
                    "district" to account.jurisdiction.district,
                    "block" to account.jurisdiction.block,
                    "panchayat" to account.jurisdiction.panchayat
                ),
                "permissions" to account.permissions,
                "createdAt" to (account.createdAt ?: FieldValue.serverTimestamp()),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()

            // Synchronize authoritative role document
            setUserRole(
                userId = account.operatorId,
                role = "OPERATOR",
                email = account.email,
                status = account.status,
                jurisdiction = account.jurisdiction
            )

            recordAuditLog(
                actorId = requireUserId(),
                actorRole = "SUPER_ADMIN",
                action = "CREATE_OPERATOR_ACCOUNT",
                targetEntity = "operator_accounts/${account.operatorId}",
                details = "Operator ${account.fullName} created for Panchayat ${account.jurisdiction.panchayat}"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "operator_accounts")
            Result.failure(e)
        }
    }

    suspend fun updateOperatorStatus(operatorId: String, newStatus: String): Result<Unit> {
        return try {
            db.collection("operator_accounts").document(operatorId).update(
                mapOf("status" to newStatus, "updatedAt" to FieldValue.serverTimestamp())
            ).await()

            db.collection("user_roles").document(operatorId).update(
                mapOf("status" to newStatus, "updatedAt" to FieldValue.serverTimestamp())
            ).await()

            recordAuditLog(
                actorId = requireUserId(),
                actorRole = "SUPER_ADMIN",
                action = "UPDATE_OPERATOR_STATUS",
                targetEntity = "operator_accounts/$operatorId",
                details = "Operator status updated to $newStatus"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "operator_accounts/$operatorId")
            Result.failure(e)
        }
    }

    // --- Security Audit Logging ---

    fun observeAuditLogs(): Flow<List<com.example.data.model.AuditLog>> = flow {
        val query = db.collection("audit_logs").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
        query.snapshots()
            .map { snap -> snap.toObjects(com.example.data.model.AuditLog::class.java) }
            .catch { error ->
                handleFirestoreError(error as Exception, OperationType.LIST, "audit_logs")
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun recordAuditLog(
        actorId: String,
        actorRole: String,
        action: String,
        targetEntity: String,
        details: String,
        status: String = "SUCCESS"
    ): Result<Unit> {
        return try {
            val docRef = db.collection("audit_logs").document()
            val data = hashMapOf(
                "actorId" to actorId,
                "actorEmail" to (auth.currentUser?.email ?: ""),
                "actorRole" to actorRole,
                "action" to action,
                "targetEntity" to targetEntity,
                "details" to details,
                "status" to status,
                "timestamp" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Audit log write failure
            Result.failure(e)
        }
    }
}
