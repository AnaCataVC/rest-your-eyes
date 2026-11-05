package com.example.restyoureyes.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

val Context.statsDataStore: DataStore<Preferences> by preferencesDataStore(name = "stats")

data class BreakMetrics(
    val completed: Int = 0,
    val skipped: Int = 0
) {
    val total: Int get() = completed + skipped
    val complianceRate: Float get() = if (total > 0) completed.toFloat() / total else 0f
    val compliancePercentage: Int get() = (complianceRate * 100).toInt()
}

data class DailyStats(
    val epochDay: Long,
    val completed: Int,
    val skipped: Int
)

class StatsRepository(
    private val dataStore: DataStore<Preferences>,
    private val clockProvider: () -> Long = { LocalDate.now(ZoneId.systemDefault()).toEpochDay() }
) {
    constructor(context: Context) : this(context.statsDataStore)

    companion object {
        val STATS_KEY = stringPreferencesKey("daily_break_stats")
        const val MAX_RETENTION_DAYS = 180L
        const val WEEKLY_WINDOW_DAYS = 7L

        fun serializeStats(list: List<DailyStats>): String {
            return list.joinToString(";") { "${it.epochDay}:${it.completed}:${it.skipped}" }
        }

        fun deserializeStats(raw: String?): List<DailyStats> {
            if (raw.isNullOrBlank()) return emptyList()
            return raw.split(";").mapNotNull { entry ->
                val parts = entry.trim().split(":")
                if (parts.size == 3) {
                    val epochDay = parts[0].toLongOrNull()
                    val completed = parts[1].toIntOrNull()
                    val skipped = parts[2].toIntOrNull()
                    if (epochDay != null && completed != null && skipped != null) {
                        DailyStats(epochDay, completed, skipped)
                    } else null
                } else null
            }
        }

        fun computeWeeklyMetrics(stats: List<DailyStats>, currentEpochDay: Long): BreakMetrics {
            val startDay = currentEpochDay - (WEEKLY_WINDOW_DAYS - 1)
            var completed = 0
            var skipped = 0
            for (entry in stats) {
                if (entry.epochDay in startDay..currentEpochDay) {
                    completed += entry.completed
                    skipped += entry.skipped
                }
            }
            return BreakMetrics(completed = completed, skipped = skipped)
        }

        fun computeHistoricalMetrics(stats: List<DailyStats>, currentEpochDay: Long): BreakMetrics {
            val cutoff = currentEpochDay - MAX_RETENTION_DAYS
            var completed = 0
            var skipped = 0
            for (entry in stats) {
                if (entry.epochDay >= cutoff) {
                    completed += entry.completed
                    skipped += entry.skipped
                }
            }
            return BreakMetrics(completed = completed, skipped = skipped)
        }
    }

    val allDailyStatsFlow: Flow<List<DailyStats>> = dataStore.data.map { preferences ->
        val raw = preferences[STATS_KEY]
        deserializeStats(raw)
    }

    val weeklyMetricsFlow: Flow<BreakMetrics> = allDailyStatsFlow.map { statsList ->
        computeWeeklyMetrics(statsList, clockProvider())
    }

    val historicalMetricsFlow: Flow<BreakMetrics> = allDailyStatsFlow.map { statsList ->
        computeHistoricalMetrics(statsList, clockProvider())
    }

    suspend fun recordBreakCompleted() {
        recordBreak(clockProvider(), isCompleted = true)
    }

    suspend fun recordBreakSkipped() {
        recordBreak(clockProvider(), isCompleted = false)
    }

    suspend fun recordBreak(epochDay: Long, isCompleted: Boolean) {
        dataStore.edit { preferences ->
            val raw = preferences[STATS_KEY]
            val statsList = deserializeStats(raw).toMutableList()
            val cutoff = epochDay - MAX_RETENTION_DAYS
            val pruned = statsList.filter { it.epochDay >= cutoff }.toMutableList()

            val index = pruned.indexOfFirst { it.epochDay == epochDay }
            if (index >= 0) {
                val existing = pruned[index]
                pruned[index] = existing.copy(
                    completed = if (isCompleted) existing.completed + 1 else existing.completed,
                    skipped = if (!isCompleted) existing.skipped + 1 else existing.skipped
                )
            } else {
                pruned.add(
                    DailyStats(
                        epochDay = epochDay,
                        completed = if (isCompleted) 1 else 0,
                        skipped = if (!isCompleted) 1 else 0
                    )
                )
            }
            preferences[STATS_KEY] = serializeStats(pruned)
        }
    }

    suspend fun clearAllStats() {
        dataStore.edit { preferences ->
            preferences.remove(STATS_KEY)
        }
    }
}
