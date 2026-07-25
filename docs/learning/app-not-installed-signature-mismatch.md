# Lesson: Android "App Not Installed" Error due to Signature Mismatch

## Context
When attempting to install a production APK (Release version) directly on an Android device, the system may block the installation and display a generic "App not installed" error message.

## Root Cause
This error frequently occurs during development if a "Debug" version of the app is already installed on the device (e.g., deployed previously via Android Studio). Android's security model strictly prohibits installing an APK signed with a different key (the Release keystore) over an existing APK signed with the default Debug keystore.

## Resolution
Always completely uninstall any existing Debug version of the application from the device before attempting to manually install a Release APK. Ensure the app is not lingering in hidden or "Disabled" application lists.
