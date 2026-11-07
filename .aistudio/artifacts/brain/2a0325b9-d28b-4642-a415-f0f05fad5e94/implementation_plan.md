# Implementation Plan: YojanaMitra – All-India Government Scheme Intelligence Platform

Upgrade YojanaMitra from a Bihar-focused prototype into a **National-Level, All-India Government Scheme Discovery and Automatic Intelligence Platform** covering all **28 Indian States**, **8 Union Territories**, and **Central Ministries**.

---

## 1. User Alignment & Product Decisions
- **National Coverage:** Full catalog covering 28 States, 8 UTs, and Central Government.
- **Publishing Workflow:** Direct automatic publishing for trusted, verified government portals (`myScheme`, `india.gov.in`, `pib.gov.in`, official State Gazette feeds) alongside an Admin Review Queue for low-confidence or unverified sources.
- **Scanning Engine:** Real-time on-device scanning engine with manual refresh, state-specific scans, department-specific scans, retry queues, and source health monitoring.
- **Preservation:** Preserve working Citizen Voice Assistant, Hindi/English/Bhojpuri/Maithili localization, Deterministic Eligibility Engine, Role-Based Access Control (RBAC), and Operator Workspaces.

---

## 2. Architecture & Data Model Extensions

### A. Extended Models (`Models.kt`)
1. **`GovernmentJurisdiction`**: Covers `NATIONAL`, `STATE`, `UNION_TERRITORY`, `DISTRICT`, `BLOCK`, `LOCAL` with complete lookup table for all 28 States and 8 UTs.
2. **`GovernmentSource`**: Registry entity for official portals (e.g. `myScheme.gov.in`, `pib.gov.in`, `services.india.gov.in`, state portals like `edistrict.up.gov.in`, `mahaonline.gov.in`, `bihar.gov.in`). Stores `scanIntervalHours`, `lastScanTimestamp`, `healthStatus` (`HEALTHY`, `DEGRADED`, `OFFLINE`), `totalDiscovered`, and `sourceType` (`PORTAL_API`, `RSS_FEED`, `NOTIFICATION_SCRAPER`, `GAZETTE`).
3. **`DiscoveredAnnouncement`**: Raw notification records with classification tags:
   - `NEW_SCHEME`
   - `EXISTING_SCHEME_UPDATE`
   - `ELIGIBILITY_CHANGE`
   - `BENEFIT_CHANGE`
   - `DEADLINE_CHANGE`
   - `SCHEME_EXTENSION`
   - `SCHEME_CLOSURE`
4. **`SchemeChangeDetection`**: Tracks diffs between scheme versions (benefit amount adjustments, age boundary changes, added required documents).
5. **`SourceScanRun`**: Execution log capturing scanned sources, duration, items found, new schemes added, and errors encountered.

### B. Extended National Schemes Registry (`SchemeData.kt`)
Seed comprehensive All-India national schemes across:
- **Central Government (All-India):** PM-Kisan, Ayushman Bharat PM-JAY, PM Awas Yojana (Gramin & Urban), Pradhan Mantri Mudra Yojana, PM Ujjwala Yojana, Atal Pension Yojana, PM Vishwakarma Yojana.
- **State Schemes:**
  - *Bihar:* Mukhyamantri Kanya Utthan, Bihar Student Credit Card, Saat Nischay Har Ghar Nal Ka Jal.
  - *Uttar Pradesh:* Mukhyamantri Kanya Sumangala Yojana, UP Kisan Karj Mafi Yojana, UP Pension Scheme.
  - *Maharashtra:* Majhi Ladki Bahin Yojana, Mahatma Jyotirao Phule Jan Arogya Yojana.
  - *Madhya Pradesh:* Ladli Behna Yojana, Sambal Yojana.
  - *Rajasthan:* Chiranjeevi Swasthya Bima Yojana, Indira Gandhi Smartphone Yojana.
  - *Karnataka:* Gruha Lakshmi, Yuva Nidhi.
  - *Tamil Nadu:* Kalaignar Magalir Urimai Thogai, Pudhumai Penn Scheme.
  - *West Bengal:* Kanyashree Prakalpa, Krishak Bandhu.
  - *Union Territories (Delhi, J&K, etc.):* Delhi Ladli Scheme, J&K Sehat Scheme, Ladakh Solar Subsidy.

### C. Universal Location & Jurisdiction Matching (`JurisdictionEngine.kt`)
- Filters schemes matching the citizen's selected location:
  - All-India central schemes are visible everywhere.
  - State schemes appear for their respective state residents.
  - Quick filter bar: `ALL INDIA` | `CENTRAL` | `MY STATE` | `MY DISTRICT`.
- Enables citizens to easily switch their home state and district in their profile or filter bar.

---

## 3. Real-Time National Scanner & Source Registry Engine (`NationalScannerEngine.kt`)
1. **Configurable Source Registry**:
   - Central ministries & agencies (Agriculture, Health, Rural Development, Education, MSME, Social Justice).
   - All 28 States & 8 UTs official portals.
   - Initial 5 primary central sources: `myscheme.gov.in`, `india.gov.in`, `services.india.gov.in`, `pib.gov.in`, `egazette.gov.in`.
2. **Scan Execution Engine**:
   - `scanAllSources()`
   - `scanSelectedState(stateName)`
   - `scanSelectedDepartment(departmentName)`
   - `retryFailedSources()`
3. **Change Detection & Auto-Publishing**:
   - Differentiates between `NEW_SCHEME` and `EXISTING_SCHEME_UPDATE`.
   - Direct automatic publishing for trusted official sources; low-confidence discoveries routed to the Super Admin review queue.
   - Comprehensive audit logging for all scan runs and automated publications.

---

## 4. UI & Dashboard Upgrades

### A. Citizen Screen (`CitizenScreen.kt`)
- **National Jurisdiction Filter**: Toggle between `All India (राष्ट्रीय)`, `Central (केन्द्रीय)`, and `My State (मेरा राज्य: बिहार/उत्तर प्रदेश आदि)`.
- **State & District Selector**: Easy picker in the citizen header and profile dialog to browse schemes from any Indian state.
- **Enhanced Scheme Cards**: Explicit badges indicating `National / Central Government` or `State Government (<State Name>)` alongside ministry/department metadata.

### B. Super Admin Portal (`AdminScreen.kt`)
- **"All-India Scheme Intelligence" Section**:
  - **National Overview**: Total configured sources (36 states/UTs + Central), active sources, error count, newly discovered schemes.
  - **Source Registry & Coverage Table**: State-by-state and central ministry coverage status.
  - **Automated Scanner Controls**: Run manual scan across all sources or filter by state/department; retry failed sources.
  - **Change Detection & Verification Queue**: Review newly detected announcements and compare version diffs.
  - **Scheduler & Health Monitoring**: Uptime, scan latency, success rates.

---

## 5. Testing & Verification Plan
1. **National Jurisdiction Unit Tests (`NationalJurisdictionTest.kt`)**:
   - Verify Central schemes appear across all states.
   - Verify State schemes (e.g. UP, Maharashtra, Bihar) only appear in their respective jurisdictions.
   - Verify category and government-level filters.
2. **Scanner & Change Detection Tests (`NationalScannerEngineTest.kt`)**:
   - Test announcement classification (`NEW_SCHEME`, `EXISTING_SCHEME_UPDATE`).
   - Test source health tracking and failure recovery.
   - Test auto-publishing logic.
3. **Applet Compilation**: Run `compile_applet` to confirm zero compilation or build errors.
