> **Created:** 2026-07-16
> **Last Updated:** 2026-07-16

# Android Background Activity & Screen Overlay Restrictions

## 1. Background Activity Launches (BAL)
Since Android 10, launching activities directly from the background is heavily restricted to prevent task hijacking.
- **System Behavior:** Blocked launches log `"Background activity launch blocked!"` to Logcat.
- **Alternatives:** 
  - Full-Screen Intents via Notifications.
  - Foreground Services (require persistent notifications).
  - Drawing over other apps using `SYSTEM_ALERT_WINDOW`.

## 2. Drawing Overlays (`SYSTEM_ALERT_WINDOW`)
To show an intrusive reminder (like a 20-20-20 rule screen) that interrupts the user:
- **Permission:** Require `<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />` in Manifest.
- **Runtime Request:** Must prompt user via `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` to grant permission in Android settings.
- **Implementation:** Use a `Service` (Foreground Service recommended) and get the system `WINDOW_SERVICE` to add a view with `TYPE_APPLICATION_OVERLAY`.

## 3. Detecting Screen State (Active Use)
To detect when the screen turns on and off to calculate active usage time:
- **Broadcast Receiver:** Use a `BroadcastReceiver` registered programmatically for `Intent.ACTION_SCREEN_ON` and `Intent.ACTION_SCREEN_OFF`. (Cannot be declared in Manifest).
- **Service Requirement:** The receiver should live inside a Foreground Service so the app isn't killed by the OS while waiting in the background.

## Conclusions for 20-20-20 App
- We need a **Foreground Service** to run continuously in the background.
- This service will register a `BroadcastReceiver` to track screen ON time.
- When screen ON time reaches 20 minutes, the service will inject a full-screen layout using `WindowManager` and `TYPE_APPLICATION_OVERLAY`.
