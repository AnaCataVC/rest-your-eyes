# PowerShell Release Automation & Local Keystore Signing

## Problem Statement
Building release APKs in Android projects frequently leads to friction:
1. Manually running `keytool` requires remembering complex parameter sets and keeping generated passwords secure.
2. Inconsistent Gradle build commands (`assembleRelease` vs `bundleRelease`) or manually moved output files lead to outdated or mismatched artifacts.
3. Signature mismatches (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`) occur when switching between debug and release builds or when the signing keystore changes unexpectedly.

---

## Architectural Decision & Solution
We implemented a centralized PowerShell release script: `generate_release.ps1`.

### Key Capabilities of the Script
1. **Automated Keystore Creation:** If `release.jks` does not exist, the script generates a 2048-bit RSA keystore with a cryptographically secure 16-character randomized password and creates `keystore.properties`.
2. **Deterministic Build Invocation:** Invokes `gradlew.bat assembleRelease` to produce a signed release APK.
3. **Standardized Artifact Output:** Copies the signed APK from `app/build/outputs/apk/release/app-release.apk` to a centralized, gitignored `releases/RestYourEyes.apk` file.

---

## Lessons & Best Practices
- **Never Commit Keystores or Artifacts:** The `releases/` folder and `keystore.properties` / `*.jks` must remain strictly within `.gitignore`.
- **Single Source of Truth for Releases:** Both human contributors and AI orchestrators (e.g. `ami-release-manager`) must execute `./generate_release.ps1` before publishing GitHub releases, ensuring the distributed binary is always up to date and correctly signed.
