# System Architecture

## Project Overview (Monorepo)
The **Rest Your Eyes** project is organized as a monorepo containing both the Android native application and its promotional web landing page.

```mermaid
graph TD
    subgraph Repo["Rest Your Eyes Repository"]
        Android["Android App (/root)<br/>Kotlin + Jetpack Compose"]
        Web["Web Landing Page (/web)<br/>Vite + Tailwind CSS v4"]
        Docs["Documentation (/docs)<br/>Architecture & Learnings"]
    end

    subgraph CI_CD["Build & Distribution"]
        Script["PowerShell Release Script<br/>(./generate_release.ps1)"]
        GHReleases["GitHub Releases<br/>(RestYourEyes.apk)"]
        Vercel["Vercel Hosting<br/>(rest-your-eyes.ana-catalina.com)"]
    end

    Android -->|Built & Signed via| Script
    Script -->|Uploads Artifact to| GHReleases
    Web -->|Automated Git Deploy| Vercel
    Vercel -->|Direct Download Link| GHReleases
```

---

## Directory Layout
- **`/` (Root):** Contains the Android application source code, Gradle configurations, and the release generation automation script.
- **`/web`:** Contains the landing page source code (Vite, Tailwind CSS v4, HTML/JS).
- **`/docs`:** Contains system architecture documentation, external references, and technical learnings (ADRs).

---

## Android App Architecture & Core Flows

The Android application implements a reactive MVVM pattern backed by Jetpack DataStore and a background `ForegroundService` to track continuous screen usage.

### 1. Service Lifecycle & Screen State Tracking

The `EyeRestService` listens to screen state changes dynamically using `ScreenStateReceiver` (`ACTION_SCREEN_ON` and `ACTION_SCREEN_OFF`).

```mermaid
stateDiagram-v2
    [*] --> ServiceCreated: BOOT_COMPLETED or App Launch
    ServiceCreated --> RunningScreenOn: Register ScreenStateReceiver & Start Timer

    state RunningScreenOn {
        [*] --> TrackingTime: Screen Active
        TrackingTime --> TimerExpired: Continuous Screen Time Reached
        TimerExpired --> ShowOverlay: Trigger OverlayActivity
        ShowOverlay --> TrackingTime: Reset Screen Timer
    }

    RunningScreenOn --> ScreenOffState: ACTION_SCREEN_OFF received
    ScreenOffState --> RunningScreenOn: ACTION_SCREEN_ON received (Reset Timer)
    RunningScreenOn --> ServiceDestroyed: Service Stopped
    ServiceDestroyed --> [*]
```

### 2. Overlay Display and Dismissal Flow

When active screen time reaches the configured break interval (e.g. 20 minutes), the service triggers `OverlayActivity` over all other apps.

```mermaid
sequenceDiagram
    autonumber
    participant System as Android OS (Screen)
    participant Receiver as ScreenStateReceiver
    participant Service as EyeRestService
    participant Repo as SettingsRepository (DataStore)
    participant Overlay as OverlayActivity / Screen

    System->>Receiver: ACTION_SCREEN_ON / OFF
    Receiver->>Service: onScreenStateChanged(isScreenOn)
    Service->>Repo: Read break interval & sound settings
    Note over Service: Accumulate active screen time in milliseconds

    alt Continuous screen time >= Target Interval
        Service->>Overlay: startActivity(OverlayActivity)
        Overlay->>Overlay: Render Compose Fullscreen Overlay
        Note over Overlay: 20-second countdown with rest instructions
        alt User taps Dismiss or Timer ends
            Overlay->>Overlay: finish()
            Overlay-->>Service: Reset accumulated screen timer
        end
    else Screen turns OFF before interval
        Service->>Service: Reset accumulated time to 0 ms
    end
```

---

## Data Persistence & Settings
- App preferences (break interval, sound alert enabled, vibration) are stored using **Jetpack DataStore (Preferences)**.
- Coroutine Flows (`Kotlin Flow`) expose live setting updates to both `MainViewModel` (for UI customization) and `EyeRestService` (for background scheduling).

---

## Production Build & Distribution Flow
1. **Keystore Management:** Keystore credentials and keystore properties are maintained locally using PowerShell automation (`./generate_release.ps1`).
2. **Release Artifacts:** Built release APKs are placed inside `releases/RestYourEyes.apk`.
3. **Web Distribution:** The web landing page links directly to the latest GitHub release artifact, keeping app distribution synchronized without frontend re-deployments.
