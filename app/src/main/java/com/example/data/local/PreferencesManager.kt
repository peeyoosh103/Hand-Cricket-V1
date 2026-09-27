package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hand_cricket_game_prefs", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val tournamentAdapter = moshi.adapter(TournamentState::class.java)
    private val careerStatsAdapter = moshi.adapter(PlayerCareerStats::class.java)
    private val settingsAdapter = moshi.adapter(GameSettings::class.java)

    private val matchListType = Types.newParameterizedType(List::class.java, MatchResult::class.java)
    private val matchListAdapter = moshi.adapter<List<MatchResult>>(matchListType)

    companion object {
        private const val KEY_TOURNAMENT = "key_active_tournament"
        private const val KEY_CAREER_STATS = "key_career_stats"
        private const val KEY_SETTINGS = "key_game_settings"
        private const val KEY_MATCH_HISTORY = "key_match_history"
    }

    // Tournament Persistence
    fun saveTournament(tournamentState: TournamentState?) {
        if (tournamentState == null) {
            prefs.edit().remove(KEY_TOURNAMENT).apply()
        } else {
            val json = tournamentAdapter.toJson(tournamentState)
            prefs.edit().putString(KEY_TOURNAMENT, json).apply()
        }
    }

    fun getTournament(): TournamentState? {
        val json = prefs.getString(KEY_TOURNAMENT, null) ?: return null
        return try {
            tournamentAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun clearTournament() {
        prefs.edit().remove(KEY_TOURNAMENT).apply()
    }

    // Career Stats Persistence
    fun saveCareerStats(stats: PlayerCareerStats) {
        val json = careerStatsAdapter.toJson(stats)
        prefs.edit().putString(KEY_CAREER_STATS, json).apply()
    }

    fun getCareerStats(): PlayerCareerStats {
        val json = prefs.getString(KEY_CAREER_STATS, null) ?: return PlayerCareerStats()
        return try {
            careerStatsAdapter.fromJson(json) ?: PlayerCareerStats()
        } catch (e: Exception) {
            PlayerCareerStats()
        }
    }

    fun clearCareerStats() {
        prefs.edit().remove(KEY_CAREER_STATS).apply()
    }

    // Match History Persistence
    fun getMatchHistory(): List<MatchResult> {
        val json = prefs.getString(KEY_MATCH_HISTORY, null) ?: return emptyList()
        return try {
            matchListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveMatchToHistory(result: MatchResult) {
        val current = getMatchHistory().toMutableList()
        // Prevent duplicates
        current.removeAll { it.matchId == result.matchId }
        current.add(0, result) // Add newest at front
        // Keep up to 100 recent matches
        val trimmed = if (current.size > 100) current.take(100) else current
        val json = matchListAdapter.toJson(trimmed)
        prefs.edit().putString(KEY_MATCH_HISTORY, json).apply()
    }

    fun clearMatchHistory() {
        prefs.edit().remove(KEY_MATCH_HISTORY).apply()
    }

    // Settings Persistence
    fun saveSettings(settings: GameSettings) {
        val json = settingsAdapter.toJson(settings)
        prefs.edit().putString(KEY_SETTINGS, json).apply()
    }

    fun getSettings(): GameSettings {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return GameSettings()
        return try {
            settingsAdapter.fromJson(json) ?: GameSettings()
        } catch (e: Exception) {
            GameSettings()
        }
    }
}
