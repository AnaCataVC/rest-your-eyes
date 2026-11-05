# Technical Reference: Break Compliance Tracking Architecture & Stress-Test

> **Created:** 2026-11-04  
> **Status:** Active  
> **Scope:** `app/src/main/java/com/example/restyoureyes/data/`, `service/`, `ui/`

---

## 1. Context & Architectural Problem

The user requires tracking visual break adherence under the 20-20-20 rule:
- Counting times the 20-second break was completed vs. skipped/dismissed.
- Displaying both **Weekly** (rolling 7-day window) and **Historical** (up to 6 months / 180 days).
- Providing an explicit reset action with confirmation.

---

## 2. Adversarial Stress-Test Findings (`ami-stress-test-idea`)

### 2.1. Critical Failure Modes & Mitigations

1. **System Clock Drift & Timezone Shifts:**
   - *Risk:* Raw timestamps (`System.currentTimeMillis()`) cause corruption when the user travels across timezones or when daylight saving changes occur. Future manual clock shifts create orphaned records that escape pruning.
   - *Mitigation:* Daily buckets must use `LocalDate.now(ZoneId.systemDefault()).toEpochDay()` as the single source of truth for calendar days. Sanitization clamps incoming epoch days to `currentEpochDay + 1`.

2. **The "Monday Morning Zero" Psychological Trap:**
   - *Risk:* A rigid ISO calendar week (Monday-Sunday) resets all counts on Monday morning, giving the false impression of lost progress.
   - *Mitigation:* The "Weekly" metric is formulated as a **rolling 7-day window** (`[currentEpochDay - 6 .. currentEpochDay]`), labeled in the UI as *"Últimos 7 días"*.

3. **Overlay Lifecycle & Idempotency:**
   - *Risk:* If the user exits the overlay via the Android system Back button/gesture or incoming phone call, or if the process dies, tracking via button `onClick` misses the event. Recompositions could duplicate counts.
   - *Mitigation:* Lifecycle tracking is encapsulated in `OverlayActivity` / `OverlayScreen` with an atomic guard (`AtomicBoolean`). Exactly one outcome (`COMPLETED` or `SKIPPED`) is registered per overlay invocation.

4. **Zero-Denominator Defense:**
   - *Risk:* `completed / (completed + skipped)` results in `NaN` or `ArithmeticException` on clean install or after reset.
   - *Mitigation:* Safe helper function returning `0f` when total is zero, and a friendly empty state displayed in Compose.

5. **Reset Scope Disambiguation:**
   - *Risk:* User intends to clear the week but accidentally deletes 6 months of historical data.
   - *Mitigation:* Material 3 `AlertDialog` explicitly warning that the full 6-month history and current week will be permanently reset.

---

## 3. Persistence Evaluation: Room SQLite vs. DataStore Preferences

| Metric | Room SQLite | DataStore Preferences (Compact Serialization) |
| :--- | :--- | :--- |
| **Data Size (180 days)** | ~2.1 KB total | ~2.1 KB total |
| **New Dependencies** | KSP plugin, Room Runtime, Room Compiler | **0 dependencies** (uses existing `androidx.datastore`) |
| **APK Overhead** | +300-500 KB | **0 KB** |
| **Build Time Impact** | Code-gen slowdown on Gradle build | **0 impact** |
| **R8 / ProGuard Risk** | Requires custom rules for entities | **Immune** (deterministic string parser without reflection) |
| **Verdict** | Overengineering (violates KISS/YAGNI) | **Optimal solution** |

---

## 4. Storage Format Specification

In DataStore Preferences (`stats.preferences_pb` or shared preferences):
- Key: `daily_break_stats` (String)
- Serialization Format: Delimited entries `epochDay:completed:skipped;...`
- Pruning Invariant: On read/write, records with `epochDay < currentEpochDay - 180` are filtered out.
- Memory Representation: Immutable `Map<Long, DailyStats>` where:
  ```kotlin
  data class DailyStats(
      val epochDay: Long,
      val completed: Int,
      val skipped: Int
  )
  ```
