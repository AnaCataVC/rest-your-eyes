# System Architecture

## Project Structure (Monorepo)
The **Rest Your Eyes** project is organized as a monorepo. This decision was made to centralize both the core product (the Android application) and its marketing/distribution site (the Web landing page) in a single version-controlled repository.

### Directory Layout
- **`/` (Root):** Contains the Android application source code (Kotlin, Jetpack Compose).
- **`/web`:** Contains the landing page source code (Vite, Tailwind CSS v4, HTML/JS).
- **`/docs`:** Contains project documentation, external references, and technical learnings.

## Integration & Deployment
While both projects share the same repository, they are decoupled in their build and deployment processes:
- **Android App:** Built via Gradle. Releases are generated as APK files and hosted on the GitHub Releases page.
- **Web Landing Page:** Deployed automatically to Vercel. The landing page pulls the download link directly from the GitHub Releases page, ensuring the user always downloads the latest published APK without requiring manual web updates.
