# Web Landing Page Decisions & Learnings

## Decisions
- **Monorepo Structure:** Decided to include the landing page inside the `web/` directory of the same repository. This keeps the product and its marketing site synchronized and simplifies automated deployments to Vercel.

## Lessons & Gotchas
- **Tailwind v4 Setup:** The new Tailwind v4 setup in a Vite environment is much cleaner, relying on direct CSS imports rather than extensive `tailwind.config.js` and `postcss.config.js` setups.

## Patterns
- **Glassmorphism Design:** Applied a premium design pattern using semi-transparent backgrounds (`bg-zinc-900/60`), blurs (`backdrop-blur-xl`), and subtle borders. This modern aesthetic provides a clean, fluid look for the landing page.
