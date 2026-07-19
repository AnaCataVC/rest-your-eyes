> **Created:** 2026-07-16
> **Last Updated:** 2026-07-16

# Android Development & UI Best Practices

## 1. Battery Optimization for Foreground Services
Keeping a service running indefinitely to track screen time can trigger Android's "bad behavior" heuristics for battery drain.
- **Wake Locks:** Avoid holding partial wake locks in the foreground service. The service should strictly rely on the `BroadcastReceiver` to wake up only when the screen state changes.
- **Transparency:** The persistent notification must clearly state why the service is running so the user understands the battery usage.
- **Minimal CPU usage:** The background thread should only calculate time diffs and not perform heavy I/O or polling operations.

## 2. Jetpack Compose MVVM Architecture
To ensure the app scales well and remains bug-free, the UI must follow Google's recommended architecture for Compose:
- **Unidirectional Data Flow (UDF):** State flows down from the `ViewModel` to the Composables, and events (like button clicks) flow up to the `ViewModel`.
- **Single Source of Truth:** The `ViewModel` (or a DataStore Repository wrapped by a ViewModel) should be the only component modifying the state.
- **State Hoisting:** Composables should be stateless whenever possible, accepting their state and event callbacks as parameters to improve testability.

## 3. Minimalist UI in Dark Mode (Premium Aesthetics)
Given the requirement for a simple, minimalist, and visually pleasing application:
- **Avoid Pure Black:** Use deep charcoal/grays (e.g., `#121212`) instead of `#000000` to prevent eye strain and OLED pixel smearing.
- **Negative Space:** Use generous padding and margins. Minimalism works best when elements have room to breathe.
- **Hierarchy via Depth:** Differentiate cards or modals from the background by using slightly lighter shades of gray rather than harsh drop shadows.
- **High Contrast Typography:** Use white or very light gray for primary text, ensuring at least a 4.5:1 contrast ratio. Use modern fonts like Inter or Roboto.
