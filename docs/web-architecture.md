# Web Landing Page Architecture

## Overview
The web landing page is located in the `/web` directory. It serves as the official distribution and marketing front for the **Rest Your Eyes** Android application.

Hosted dynamically on Vercel: [https://rest-your-eyes.ana-catalina.com](https://rest-your-eyes.ana-catalina.com)

---

## Technology Stack

- **Build Tool / Bundler:** [Vite](https://vitejs.dev/)
- **CSS Framework:** [Tailwind CSS v4](https://tailwindcss.com/)
- **Core Languages:** HTML5, Modern Vanilla JavaScript (ES6+), CSS3
- **Deployment Platform:** [Vercel](https://vercel.com/)

---

## Architectural & Design Decisions

### 1. Modern Glassmorphism Aesthetic
The UI applies modern glassmorphism principles characterized by:
- Semi-transparent container backgrounds (`bg-white/10` or `bg-slate-900/40`) with backdrop filters (`backdrop-blur-md` / `backdrop-blur-lg`).
- Subtle light borders (`border border-white/10` to `border-white/20`) to create depth against dark, radiant mesh gradients.
- High-contrast typography and fluid layout adjustments for seamless responsiveness across mobile, tablet, and desktop screens.

### 2. Direct GitHub Releases Integration
To avoid manual deployments whenever a new APK version is built:
- The landing page provides direct download buttons pointing to the latest GitHub Release asset or the repository's releases page.
- This decoupling ensures that updating the Android APK does not require re-deploying the frontend application.

---

## Directory Structure

```text
web/
├── dist/                # Production build output
├── public/              # Static assets (favicons, icons, metadata)
├── src/
│   ├── index.css        # Tailwind v4 import and custom utility classes
│   └── main.js          # Client-side interactions and animations
├── index.html           # Main single-page application entry point
├── package.json         # Scripts and project dependencies
├── vite.config.js       # Vite configuration
└── README.md            # Quickstart guide for the web module
```

---

## Local Development & Build

### Prerequisites
- Node.js (v18 or higher recommended)
- npm or yarn

### Available Scripts

```bash
# Install dependencies
npm install

# Start local development server with Hot Module Replacement (HMR)
npm run dev

# Compile and minify for production
npm run build

# Locally preview production build
npm run preview
```
