package com.example

import com.example.domain.engine.NationalCoverageStats
import com.example.domain.engine.NationalJurisdictionData
import com.example.domain.engine.NationalScannerEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure JVM Unit Tests for All-India National Government Scheme Intelligence:
 * Verifies coverage across 28 States and 8 UTs, national jurisdiction evaluation,
 * source registry health tracking, and real-time scanner discovery.
 */
class AllIndiaSchemeIntelligenceTest {

    @Test
    fun test1_nationalJurisdiction_hasAll28States() {
        assertEquals(28, NationalJurisdictionData.STATES_28.size)
        assertTrue(NationalJurisdictionData.STATES_28.contains("Bihar"))
        assertTrue(NationalJurisdictionData.STATES_28.contains("Uttar Pradesh"))
        assertTrue(NationalJurisdictionData.STATES_28.contains("Maharashtra"))
        assertTrue(NationalJurisdictionData.STATES_28.contains("Tamil Nadu"))
        assertTrue(NationalJurisdictionData.STATES_28.contains("West Bengal"))
        assertTrue(NationalJurisdictionData.STATES_28.contains("Rajasthan"))
    }

    @Test
    fun test2_nationalJurisdiction_hasAll8UnionTerritories() {
        assertEquals(8, NationalJurisdictionData.UNION_TERRITORIES_8.size)
        assertTrue(NationalJurisdictionData.UNION_TERRITORIES_8.contains("Delhi"))
        assertTrue(NationalJurisdictionData.UNION_TERRITORIES_8.contains("Jammu and Kashmir"))
        assertTrue(NationalJurisdictionData.UNION_TERRITORIES_8.contains("Ladakh"))
        assertTrue(NationalJurisdictionData.UNION_TERRITORIES_8.contains("Chandigarh"))
    }

    @Test
    fun test3_totalJurisdictionsCount_is36() {
        assertEquals(36, NationalJurisdictionData.ALL_JURISDICTIONS.size)
    }

    @Test
    fun test4_schemeApplicability_centralSchemesAreUniversal() {
        // All-India Central Scheme (e.g. PM Kisan, Ayushman Bharat) must be applicable across all states
        assertTrue(NationalJurisdictionData.isSchemeApplicable("All India", "Bihar"))
        assertTrue(NationalJurisdictionData.isSchemeApplicable("All India", "Uttar Pradesh"))
        assertTrue(NationalJurisdictionData.isSchemeApplicable("All India", "Maharashtra"))
        assertTrue(NationalJurisdictionData.isSchemeApplicable("National", "Tamil Nadu"))
        assertTrue(NationalJurisdictionData.isSchemeApplicable("Central", "Delhi"))
    }

    @Test
    fun test5_schemeApplicability_stateSchemesAreStrictlyIsolated() {
        // Uttar Pradesh state scheme must NOT be applicable in Bihar
        assertFalse(NationalJurisdictionData.isSchemeApplicable("Uttar Pradesh", "Bihar"))
        assertTrue(NationalJurisdictionData.isSchemeApplicable("Uttar Pradesh", "Uttar Pradesh"))

        // Maharashtra state scheme must NOT be applicable in Rajasthan
        assertFalse(NationalJurisdictionData.isSchemeApplicable("Maharashtra", "Rajasthan"))
        assertTrue(NationalJurisdictionData.isSchemeApplicable("Maharashtra", "Maharashtra"))
    }

    @Test
    fun test6_nationalSourcesRegistry_containsCentralAndStateSources() {
        val sources = NationalScannerEngine.initialSources
        assertTrue(sources.size >= 10)

        // Verify primary central portals
        val myScheme = sources.find { it.sourceId == "myscheme_gov_in" }
        assertNotNull(myScheme)
        assertEquals("All India", myScheme?.jurisdictionState)
        assertEquals(200, myScheme?.lastResponseCode)

        val pib = sources.find { it.sourceId == "pib_gov_in" }
        assertNotNull(pib)

        // Verify state portals exist
        val upSource = sources.find { it.jurisdictionState == "Uttar Pradesh" }
        assertNotNull(upSource)

        val mahaSource = sources.find { it.jurisdictionState == "Maharashtra" }
        assertNotNull(mahaSource)
    }

    @Test
    fun test7_nationalScannerEngine_performsScanAndReturnsRunReport() {
        val sources = NationalScannerEngine.initialSources
        val (scanRun, newAnnouncements) = NationalScannerEngine.performScan(sources, "All India")

        assertNotNull(scanRun)
        assertEquals("COMPLETED", scanRun.status)
        assertTrue(scanRun.totalSourcesScanned > 0)
        assertEquals(scanRun.totalSourcesScanned, scanRun.successfulSources)
        assertEquals(0, scanRun.failedSources)
    }

    @Test
    fun test8_nationalScannerEngine_stateTargetedScanWorks() {
        val sources = NationalScannerEngine.initialSources
        val (scanRun, announcements) = NationalScannerEngine.performScan(sources, "Bihar")

        assertEquals("STATE_Bihar", scanRun.scope)
        assertEquals("COMPLETED", scanRun.status)
        assertTrue(announcements.isNotEmpty())
        assertEquals("Bihar", announcements.first().jurisdictionState)
    }

    @Test
    fun test9_coverageStatsCalculation_computesActiveSources() {
        val stats: NationalCoverageStats = NationalScannerEngine.calculateCoverageStats(NationalScannerEngine.initialSources)
        assertEquals(28, stats.totalStatesCount)
        assertEquals(8, stats.totalUtsCount)
        assertTrue(stats.activeSources > 0)
        assertEquals(0, stats.errorSources)
        assertTrue(stats.allIndiaSourceCount >= 4)
    }
}
