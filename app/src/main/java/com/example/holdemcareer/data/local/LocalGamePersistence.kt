package com.example.holdemcareer.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.holdemcareer.domain.career.CareerState
import com.example.holdemcareer.domain.career.OpponentDossierState

class LocalGamePersistence(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("holdem_career_prefs", Context.MODE_PRIVATE)

    fun saveCareerState(state: CareerState) {
        prefs.edit().apply {
            putInt("bankroll", state.currentRunBankroll)
            putInt("starting_bankroll", state.startingBankrollBaseline)
            putInt("table_buy_in", state.currentTableBuyIn)
            putInt("total_hands_all_time", state.totalHandsPlayedAllTime)
            putInt("total_hands_run", state.totalHandsPlayedCurrentRun)
            putInt("highest_bankroll", state.highestBankrollReached)
            apply()
        }
    }

    fun loadCareerState(): CareerState {
        return CareerState(
            currentRunBankroll = prefs.getInt("bankroll", 50),
            startingBankrollBaseline = prefs.getInt("starting_bankroll", 50),
            currentTableBuyIn = prefs.getInt("table_buy_in", 50),
            totalHandsPlayedAllTime = prefs.getInt("total_hands_all_time", 0),
            totalHandsPlayedCurrentRun = prefs.getInt("total_hands_run", 0),
            highestBankrollReached = prefs.getInt("highest_bankroll", 50)
        )
    }

    fun saveOpponentDossier(dossier: OpponentDossierState) {
        val prefix = "dossier_${dossier.opponentId}_"
        prefs.edit().apply {
            putInt("${prefix}hands", dossier.handsPlayedAgainst)
            putInt("${prefix}stacked", dossier.timesStackedByPlayer)
            putInt("${prefix}profit", dossier.netProfitAgainstPlayer)
            putBoolean("${prefix}lvl1", dossier.isDossierUnlockedLevel1)
            putBoolean("${prefix}lvl2", dossier.isDossierUnlockedLevel2)
            putBoolean("${prefix}lvl3", dossier.isDossierUnlockedLevel3)
            apply()
        }
    }

    fun loadOpponentDossier(opponentId: String): OpponentDossierState {
        val prefix = "dossier_${opponentId}_"
        return OpponentDossierState(
            opponentId = opponentId,
            handsPlayedAgainst = prefs.getInt("${prefix}hands", 0),
            timesStackedByPlayer = prefs.getInt("${prefix}stacked", 0),
            netProfitAgainstPlayer = prefs.getInt("${prefix}profit", 0),
            isDossierUnlockedLevel1 = prefs.getBoolean("${prefix}lvl1", false),
            isDossierUnlockedLevel2 = prefs.getBoolean("${prefix}lvl2", false),
            isDossierUnlockedLevel3 = prefs.getBoolean("${prefix}lvl3", false)
        )
    }
}
