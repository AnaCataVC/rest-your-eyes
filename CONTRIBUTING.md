# Contributing to Rest Your Eyes

Thank you for your interest in contributing to **Rest Your Eyes**! To maintain high code quality, security, and project consistency, please read and follow these guidelines.

---

## 🏗️ Repository Architecture

This repository is structured as a monorepo containing:
- **Root (`/`):** Android native application (Kotlin, Jetpack Compose, DataStore, Foreground Services).
- **Web (`/web`):** Landing page and web distribution portal (Vite, Tailwind CSS v4).
- **Docs (`/docs`):** Architectural specifications, technical learnings, and reference guides.

---

## 🛠️ Development Guidelines

### Android Application
1. **Language & UI:** Kotlin, Jetpack Compose with Material 3.
2. **Architecture:** MVVM (Model-View-ViewModel) with unidirectional data flow (UDF) and Coroutines / Kotlin Flows.
3. **Background Processing:**
   - Background monitoring runs as a `ForegroundService` with `specialUse` foreground service type.
   - Screen on/off transitions are captured via a dynamic `BroadcastReceiver` (`ScreenStateReceiver`).
   - Any modifications to the background timer or receivers must consider Android battery optimization (Doze Mode) and Android 14+ background execution restrictions.
4. **Permissions:** Always verify runtime permissions (`POST_NOTIFICATIONS`, `SYSTEM_ALERT_WINDOW`) before invoking service components or attempting to launch overlays.

### Web Landing Page
1. **Tooling:** Vite, ES modules, Tailwind CSS v4.
2. **Styling:** Follow the existing glassmorphism design language. Ensure responsive behavior across mobile, tablet, and desktop viewports.

---

## 🌿 Git Workflow & Commit Conventions

### Branch Strategy
- `main`: Production-ready branch.
- `feat/<feature-name>`: New features or UI components.
- `fix/<bug-name>`: Bug fixes and performance patches.
- `docs/<topic>`: Documentation updates and ADR additions.

### Conventional Commits
We strictly adhere to the [Conventional Commits](https://www.conventionalcommits.org/) specification:

- `feat: add sound notification options to break overlay`
- `fix: prevent timer reset on intermittent screen state changes`
- `docs: add mermaid diagrams for background service lifecycle`
- `refactor: simplify DataStore preferences repository`
- `chore: update dependencies and Gradle plugins`

---

## 📦 Building and Releasing (Android APK)

To ensure consistent APK signing and prevent key mismatches, **never build release APKs manually via raw Gradle commands**.

Always use the automated release script:

```powershell
./generate_release.ps1
```

This script:
1. Automatically verifies or generates the release keystore (`release.jks` and `keystore.properties`).
2. Compiles and signs the production APK (`assembleRelease`).
3. Outputs the validated artifact to `releases/RestYourEyes.apk`.

> **Note:** The `releases/` directory is gitignored to avoid committing binary assets to the repository tree.

---

## 🧪 Testing and Verification

Before submitting changes:
1. Run local unit tests:
   ```bash
   ./gradlew test
   ```
2. Build debug APK and test overlay interactions on an emulator or physical device running API 26+.
3. Verify that no private or local computer paths are committed in documentation or source files.
