package com.example.holdemcareer

import com.example.holdemcareer.domain.career.DossierManager
import com.example.holdemcareer.domain.career.OpponentDossierState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DossierManagerTest {

    @Test
    fun testLevel1UnlocksAfter10Hands() {
        val initial = OpponentDossierState("marcus")
        assertFalse(initial.isDossierUnlockedLevel1)

        val updated = DossierManager.updateDossier(initial, handsDelta = 10, stacked = false, profitDelta = 20)
        assertTrue(updated.isDossierUnlockedLevel1)
        assertFalse(updated.isDossierUnlockedLevel2)
    }

    @Test
    fun testLevel3SecretTellUnlocksAfter50HandsAndStacking() {
        val initial = OpponentDossierState("vince")

        val updated = DossierManager.updateDossier(initial, handsDelta = 50, stacked = true, profitDelta = 150)
        assertTrue(updated.isDossierUnlockedLevel1)
        assertTrue(updated.isDossierUnlockedLevel2)
        assertTrue(updated.isDossierUnlockedLevel3)
    }
}
