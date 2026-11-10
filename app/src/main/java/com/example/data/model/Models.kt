package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class UserRole(val labelHindi: String, val labelEnglish: String) {
    CITIZEN("नागरिक (Citizen)", "Citizen"),
    PANCHAYAT_OPERATOR("पंचायत ऑपरेटर (Operator)", "Panchayat Operator"),
    SUPER_ADMIN("सुपर एडमिन (Admin)", "Super Admin")
}

enum class AppLanguage(val code: String, val displayName: String, val greeting: String) {
    HINDI("hi", "हिन्दी (Hindi)", "नमस्ते! आपका स्वागत है।"),
    ENGLISH("en", "English", "Welcome! How can I assist you today?"),
    BHOJPURI("bho", "भोजपुरी (Bhojpuri)", "प्रणाम! राउर स्वागत बा।"),
    MAITHILI("mai", "मैथिली (Maithili)", "प्रणाम! अहाँक स्वागत अछि।")
}

enum class EligibilityStatus(val labelHindi: String, val labelEnglish: String) {
    POTENTIALLY_ELIGIBLE("संभावित रूप से पात्र", "Potentially Eligible"),
    MORE_INFO_NEEDED("अधिक जानकारी आवश्यक", "More Info Needed"),
    CRITERIA_NOT_MET("पात्रता मानदंड पूरा नहीं", "Criteria Not Met")
}

enum class VoiceStatus(val labelHindi: String, val labelEnglish: String) {
    IDLE("बोलने के लिए माइक दबाएं", "Tap mic to speak"),
    LISTENING("सुन रहे हैं... कृपया बोलें", "Listening..."),
    PROCESSING("प्रसंस्करण हो रहा है...", "Processing..."),
    SPEAKING("उत्तर दे रहे हैं...", "Speaking response...")
}

enum class CriterionOperator {
    EQ,
    GTE,
    LTE,
    IN,
    BOOLEAN_TRUE,
    LESS_THAN_OR_EQUAL_LAND,
    MAX_INCOME
}

data class EligibilityCriterion(
    val id: String = "",
    val attribute: String = "",
    val operator: CriterionOperator = CriterionOperator.EQ,
    val expectedValue: String = "",
    val isMandatory: Boolean = true,
    val labelHindi: String = "",
    val labelEnglish: String = "",
    val failureReasonHindi: String = "",
    val failureReasonEnglish: String = ""
)

data class SchemeDocument(
    val titleHindi: String = "",
    val titleEnglish: String = "",
    val category: String = "Identity",
    val isMandatory: Boolean = true,
    val description: String = ""
)

@IgnoreExtraProperties
data class Scheme(
    @DocumentId val id: String = "",
    val schemeName: String = "",
    val schemeNameHindi: String = "",
    val category: String = "Agriculture",
    val department: String = "",
    val governmentLevel: String = "Central",
    val applicableState: String = "All India",
    val shortDescription: String = "",
    val shortDescriptionHindi: String = "",
    val benefitSummary: String = "",
    val benefitSummaryHindi: String = "",
    val officialLink: String = "",
    val sourceReference: String = "",
    val lastVerifiedDate: String = "2026-11-01",
    val version: Int = 1,
    val isPublished: Boolean = true,
    val criteria: List<EligibilityCriterion> = emptyList(),
    val requiredDocuments: List<SchemeDocument> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CitizenProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val preferredLanguage: String = "hi",
    val state: String = "Bihar",
    val district: String = "Patna",
    val block: String = "",
    val panchayat: String = "",
    val village: String = "",
    val age: Int? = null,
    val gender: String = "Male",
    val occupation: String = "Farmer",
    val landholdingAcres: Double? = null,
    val householdIncome: Int? = null,
    val category: String = "OBC",
    val hasDisability: Boolean = false,
    val hasBplRationCard: Boolean = false,
    val consentGiven: Boolean = true,
    val operatorId: String? = null,
    val photoUrl: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CriterionEvaluation(
    val criterion: EligibilityCriterion,
    val isSatisfied: Boolean,
    val isMissing: Boolean,
    val messageHindi: String,
    val messageEnglish: String
)

data class EligibilityEvaluationResult(
    val schemeId: String,
    val schemeName: String,
    val schemeNameHindi: String,
    val status: EligibilityStatus,
    val satisfiedList: List<CriterionEvaluation>,
    val failedList: List<CriterionEvaluation>,
    val missingList: List<CriterionEvaluation>,
    val summaryHindi: String,
    val summaryEnglish: String,
    val officialSource: String,
    val ruleVersion: Int = 1,
    val lastVerifiedDate: String = "2026-11-01"
)

@IgnoreExtraProperties
data class ApplicationCase(
    @DocumentId val id: String = "",
    val userId: String = "",
    val citizenName: String = "",
    val schemeId: String = "",
    val schemeName: String = "",
    val referenceNumber: String = "",
    val submissionDate: String = "",
    val channel: String = "CSC Center",
    val status: String = "Preparing Documents",
    val statusSource: String = "Citizen Self-Reported",
    val operatorNotes: String = "",
    val state: String = "Bihar",
    val district: String = "Patna",
    val block: String = "Phulwari Sharif",
    val panchayat: String = "Nohsa",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ServiceCentre(
    @DocumentId val id: String = "",
    val name: String = "",
    val type: String = "CSC Center",
    val address: String = "",
    val district: String = "Patna",
    val block: String = "Phulwari Sharif",
    val panchayat: String = "Nohsa",
    val contactPhone: String = "",
    val openingHours: String = "9:30 AM - 5:30 PM",
    val distanceKm: Double = 1.2,
    val isVerified: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SavedScheme(
    @DocumentId val id: String = "",
    val userId: String = "",
    val schemeId: String = "",
    val schemeName: String = "",
    val category: String = "",
    @ServerTimestamp val savedAt: Timestamp? = null
)

data class DocumentChecklistItem(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val category: String,
    val isMandatory: Boolean,
    var isAvailable: Boolean = false,
    var notes: String = ""
)

data class Jurisdiction(
    val state: String = "Bihar",
    val district: String = "Patna",
    val block: String = "Phulwari Sharif",
    val panchayat: String = "Nohsa"
)

enum class AccountStatus(val labelHindi: String, val labelEnglish: String) {
    ACTIVE("सक्रिय (Active)", "Active"),
    SUSPENDED("निलंबित (Suspended)", "Suspended"),
    DEACTIVATED("निष्क्रिय (Deactivated)", "Deactivated")
}

@IgnoreExtraProperties
data class OperatorAccount(
    @DocumentId val id: String = "",
    val operatorId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val employeeId: String = "",
    val jurisdiction: Jurisdiction = Jurisdiction(),
    val status: String = "ACTIVE",
    val assignedCentreId: String = "",
    val assignedBy: String = "Super Admin",
    val permissions: List<String> = listOf("ASSIST_CITIZEN", "VERIFY_DOCUMENTS", "SUBMIT_APPLICATION"),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserRoleRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val role: String = "CITIZEN",
    val status: String = "ACTIVE",
    val email: String = "",
    val jurisdictionState: String = "Bihar",
    val jurisdictionDistrict: String = "Patna",
    val jurisdictionBlock: String = "Phulwari Sharif",
    val jurisdictionPanchayat: String = "Nohsa",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AuditLog(
    @DocumentId val id: String = "",
    val actorId: String = "",
    val actorEmail: String = "",
    val actorRole: String = "",
    val action: String = "",
    val targetEntity: String = "",
    val details: String = "",
    val status: String = "SUCCESS",
    @ServerTimestamp val timestamp: Timestamp? = null
)

enum class GovernmentLevel(val labelHindi: String, val labelEnglish: String) {
    NATIONAL("राष्ट्रीय / केन्द्रीय (National / Central)", "National / Central"),
    STATE("राज्य सरकार (State Government)", "State Government"),
    UNION_TERRITORY("केंद्र शासित प्रदेश (Union Territory)", "Union Territory"),
    DISTRICT("जिला स्तर (District Level)", "District Level"),
    LOCAL("स्थानीय / पंचायत (Local / Panchayat)", "Local / Panchayat")
}

enum class SourceType(val label: String) {
    PORTAL_API("Official Portal API"),
    RSS_FEED("Government RSS / Atom Feed"),
    NOTIFICATION_SCRAPER("Official Notification Scraper"),
    GAZETTE("Official Gazette Repository"),
    PRESS_RELEASE("PIB / Press Information Bureau")
}

enum class SourceHealth(val label: String) {
    HEALTHY("Healthy (Online)"),
    DEGRADED("Degraded (High Latency)"),
    OFFLINE("Offline (Unreachable)"),
    PENDING("Awaiting First Scan")
}

enum class DiscoveryClassification(val labelHindi: String, val labelEnglish: String) {
    NEW_SCHEME("नई योजना (New Scheme)", "NEW_SCHEME"),
    EXISTING_SCHEME_UPDATE("योजना अद्यतन (Scheme Update)", "EXISTING_SCHEME_UPDATE"),
    NEW_APPLICATION_WINDOW("आवेदन विंडो खुली (Application Window)", "NEW_APPLICATION_WINDOW"),
    ELIGIBILITY_CHANGE("पात्रता नियम बदलाव (Eligibility Change)", "ELIGIBILITY_CHANGE"),
    BENEFIT_CHANGE("लाभ राशि संशोधन (Benefit Change)", "BENEFIT_CHANGE"),
    DOCUMENT_CHANGE("दस्तावेज आवश्यकता संशोधन (Document Change)", "DOCUMENT_CHANGE"),
    DEADLINE_CHANGE("अंतिम तिथि विस्तार (Deadline Change)", "DEADLINE_CHANGE"),
    SCHEME_EXTENSION("योजना विस्तार (Scheme Extension)", "SCHEME_EXTENSION"),
    SCHEME_CLOSURE("योजना समापन (Scheme Closure)", "SCHEME_CLOSURE"),
    NEEDS_REVIEW("पुनरावलोकन आवश्यक (Needs Review)", "NEEDS_REVIEW")
}

@IgnoreExtraProperties
data class GovernmentSource(
    @DocumentId val id: String = "",
    val sourceId: String = "",
    val name: String = "",
    val nameHindi: String = "",
    val url: String = "",
    val governmentLevel: String = "National",
    val jurisdictionState: String = "All India",
    val ministryOrDepartment: String = "",
    val sourceType: String = "PORTAL_API",
    val scanIntervalHours: Int = 6,
    val healthStatus: String = "HEALTHY",
    val totalDiscoveredCount: Int = 0,
    val lastScanTimestamp: String = "2026-11-06 09:30 AM",
    val isEnabled: Boolean = true,
    val isAutoPublishEnabled: Boolean = true,
    val lastResponseCode: Int = 200,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class DiscoveredAnnouncement(
    @DocumentId val id: String = "",
    val announcementId: String = "",
    val sourceId: String = "",
    val sourceName: String = "",
    val title: String = "",
    val titleHindi: String = "",
    val rawSummary: String = "",
    val classification: String = "NEW_SCHEME",
    val governmentLevel: String = "National",
    val jurisdictionState: String = "All India",
    val ministryOrDepartment: String = "",
    val officialLink: String = "",
    val notificationNumber: String = "",
    val publicationDate: String = "2026-11-06",
    val discoveryTimestamp: String = "2026-11-06 10:15 AM",
    val confidenceScore: Double = 0.95,
    val isVerified: Boolean = true,
    val isPublished: Boolean = true,
    val generatedSchemeId: String? = null,
    val changeSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SchemeChangeDetection(
    @DocumentId val id: String = "",
    val changeId: String = "",
    val schemeId: String = "",
    val schemeName: String = "",
    val fieldName: String = "",
    val previousValue: String = "",
    val newValue: String = "",
    val effectiveDate: String = "2026-11-06",
    val officialCircularUrl: String = "",
    val detectedAt: String = "2026-11-06 10:30 AM",
    val status: String = "AUTO_APPLIED",
    @ServerTimestamp val timestamp: Timestamp? = null
)

@IgnoreExtraProperties
data class SourceScanRun(
    @DocumentId val id: String = "",
    val runId: String = "",
    val triggeredBy: String = "SYSTEM_SCHEDULER",
    val scope: String = "ALL_INDIA",
    val targetState: String? = null,
    val totalSourcesScanned: Int = 0,
    val successfulSources: Int = 0,
    val failedSources: Int = 0,
    val newlyDiscoveredCount: Int = 0,
    val updatesFoundCount: Int = 0,
    val durationSeconds: Double = 3.4,
    val status: String = "COMPLETED",
    val summary: String = "",
    val timestamp: String = "2026-11-06 10:00 AM",
    @ServerTimestamp val createdAt: Timestamp? = null
)


