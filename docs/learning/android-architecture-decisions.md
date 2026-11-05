# Android Architecture Decisions & Learnings

## Decisions
- **Overlays vs Notifications:** Decided to use `WindowManager` with `SYSTEM_ALERT_WINDOW` permissions instead of standard notifications. This is the only way to visually force the user to take a 20-20-20 break and effectively interrupt their workflow.
- **Foreground Services:** Opted for a persistent foreground service. This prevents the Android OS from killing the screen time tracker while the app is in the background.
- **Continuous Screen Usage Tracking:** Reset accumulated screen time to 0 whenever `ACTION_SCREEN_OFF` is received. This ensures that the rest timer strictly counts uninterrupted continuous usage rather than cumulative pauses across multiple locks.
- **DataStore Compact Delimited Serialization vs Room SQLite:** For tracking break compliance over 180 days (completed vs. skipped counts), Room SQLite introduces hundreds of kilobytes of APK bloat, KSP code-generation slowdowns, and schema migration maintenance. A custom reflection-free delimited serializer (`epochDay:completed:skipped;...`) stores the entire 180-day history in ~2.1 KB within Jetpack DataStore Preferences, parsing in < 1 ms while remaining completely immune to R8/ProGuard reflection obfuscation.
- **Rolling 7-Day Window vs Fixed Calendar Week:** Metric aggregation uses a dynamic rolling 7-day window (`[currentEpochDay - 6 .. currentEpochDay]`) labeled as *"Últimos 7 días"* instead of an ISO calendar week (Monday to Sunday). This eliminates the demotivating "Monday morning zero" drop where active users would see 0% compliance every Monday morning.
- **Atomic Overlay Idempotency Barrier:** In Android, overlays can be terminated unexpectedly via system back gestures, incoming phone calls, home button presses, or auto-dismiss timeouts. Using an `AtomicBoolean` guard ensures that every overlay session transitions into exactly one terminal state (`COMPLETED` or `SKIPPED`) without double-counting on Activity lifecycle events (`onStop`) or Compose recompositions.

## Lessons & Gotchas
- **Real Usage Measurement:** The most reliable way to measure actual screen time (and avoid false positives when the phone is locked) is by listening to system broadcasts for `ACTION_SCREEN_ON` and `ACTION_SCREEN_OFF`.
- **System Clock Shifts & Timezone Safety:** Date operations must rely on `LocalDate.now(ZoneId.systemDefault()).toEpochDay()` rather than raw UTC timestamps or calendar intervals, insulating date bucketing against Daylight Saving Time transitions and timezone changes.

## Patterns
- **MVVM + Jetpack Compose:** Strict separation of concerns handling state in the ViewModel and consuming it reactively in Compose UI via Kotlin `StateFlow`.
