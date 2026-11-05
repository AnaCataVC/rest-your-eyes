package com.example.restyoureyes.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StatsRepositoryTest {

    @Test
    fun `deserializeStats returns empty list for null or empty string`() {
        assertEquals(emptyList<DailyStats>(), StatsRepository.deserializeStats(null))
        assertEquals(emptyList<DailyStats>(), StatsRepository.deserializeStats(""))
        assertEquals(emptyList<DailyStats>(), StatsRepository.deserializeStats("   "))
    }

    @Test
    fun `deserializeStats handles malformed entries gracefully without throwing`() {
        val corrupted = "corrupted;123:abc:def;20000:5:1;:;:;not_numbers"
        val result = StatsRepository.deserializeStats(corrupted)
        assertEquals(1, result.size)
        assertEquals(DailyStats(20000L, 5, 1), result[0])
    }

    @Test
    fun `serialization and deserialization roundtrip preserves data`() {
        val original = listOf(
            DailyStats(20001L, 10, 2),
            DailyStats(20002L, 8, 4),
            DailyStats(20003L, 12, 0)
        )
        val serialized = StatsRepository.serializeStats(original)
        val deserialized = StatsRepository.deserializeStats(serialized)
        assertEquals(original, deserialized)
    }

    @Test
    fun `computeWeeklyMetrics aggregates only last 7 days inclusive`() {
        val currentDay = 20010L
        val stats = listOf(
            DailyStats(20002L, 5, 5),  // 8 days ago: excluded
            DailyStats(20003L, 5, 5),  // 7 days ago: excluded (20010 - 7 = 20003, window is 20004..20010)
            DailyStats(20004L, 3, 1),  // 6 days ago: included
            DailyStats(20007L, 4, 2),  // 3 days ago: included
            DailyStats(20010L, 2, 0)   // today: included
        )

        val metrics = StatsRepository.computeWeeklyMetrics(stats, currentDay)
        assertEquals(9, metrics.completed) // 3 + 4 + 2
        assertEquals(3, metrics.skipped)   // 1 + 2 + 0
        assertEquals(12, metrics.total)
        assertEquals(0.75f, metrics.complianceRate, 0.001f)
        assertEquals(75, metrics.compliancePercentage)
    }

    @Test
    fun `computeHistoricalMetrics aggregates all entries within 180 days`() {
        val currentDay = 20200L
        val stats = listOf(
            DailyStats(20000L, 10, 10), // 200 days ago: excluded
            DailyStats(20019L, 5, 5),   // 181 days ago: excluded
            DailyStats(20020L, 8, 2),   // 180 days ago: included
            DailyStats(20150L, 20, 5),  // 50 days ago: included
            DailyStats(20200L, 10, 1)   // today: included
        )

        val metrics = StatsRepository.computeHistoricalMetrics(stats, currentDay)
        assertEquals(38, metrics.completed) // 8 + 20 + 10
        assertEquals(8, metrics.skipped)    // 2 + 5 + 1
        assertEquals(46, metrics.total)
    }

    @Test
    fun `BreakMetrics handles zero total without NaN or division by zero`() {
        val empty = BreakMetrics(completed = 0, skipped = 0)
        assertEquals(0, empty.total)
        assertEquals(0f, empty.complianceRate, 0.0001f)
        assertEquals(0, empty.compliancePercentage)
    }

    @Test
    fun `BreakMetrics calculates 100 percent when zero skipped`() {
        val perfect = BreakMetrics(completed = 15, skipped = 0)
        assertEquals(15, perfect.total)
        assertEquals(1.0f, perfect.complianceRate, 0.0001f)
        assertEquals(100, perfect.compliancePercentage)
    }

    @Test
    fun `BreakMetrics calculates correct rate with mixed completed and skipped`() {
        val mixed = BreakMetrics(completed = 6, skipped = 4)
        assertEquals(10, mixed.total)
        assertEquals(0.6f, mixed.complianceRate, 0.0001f)
        assertEquals(60, mixed.compliancePercentage)
    }
}
