# Android Architecture Decisions & Learnings

## Decisions
- **Overlays vs Notifications:** Decided to use `WindowManager` with `SYSTEM_ALERT_WINDOW` permissions instead of standard notifications. This is the only way to visually force the user to take a 20-20-20 break and effectively interrupt their workflow.
- **Foreground Services:** Opted for a persistent foreground service. This prevents the Android OS from killing the screen time tracker while the app is in the background.

## Lessons & Gotchas
- **Real Usage Measurement:** The most reliable way to measure actual screen time (and avoid false positives when the phone is locked) is by listening to system broadcasts for `ACTION_SCREEN_ON` and `ACTION_SCREEN_OFF`.

## Patterns
- **MVVM + Jetpack Compose:** Strict separation of concerns handling state in the ViewModel and consuming it reactively in Compose UI.
