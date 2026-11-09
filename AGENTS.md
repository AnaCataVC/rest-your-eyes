# Proyecto: Rest Your Eyes

## 📚 Contexto del Proyecto
Esta es una aplicación móvil nativa para Android enfocada en el descanso visual de los usuarios ("Rest Your Eyes"). Funciona como un temporizador y recordatorio en segundo plano bajo la regla 20-20-20.

## 🏗️ Estructura del Repositorio
- **`/` (Raíz):** Aplicación nativa Android (Kotlin, Jetpack Compose, DataStore, Foreground Services).
- **`/docs`:** Documentación técnica de arquitectura, lecciones aprendidas (ADRs) y referencias externas.

La página de producto se mantiene en el repositorio `projects-hub` (https://github.com/AnaCataVC/projects-hub) y se sirve en https://rest-your-eyes.ana-catalina.com.

## 🛠️ Stack Tecnológico
- **Móvil (Android):** Kotlin, Jetpack Compose (Material 3), DataStore Preferences, Gradle Kotlin DSL (`minSdk = 26`, `targetSdk = 34`).
- **Infraestructura:** GitHub Releases (Distribución APK).

## 🚀 Flujo de Producción y Releases (IMPORTANTE)
El flujo de publicación de releases para Android está automatizado de extremo a extremo a través de GitHub Actions:

1. **Pipeline Automatizado en CI/CD (Recomendado):**
   - Al hacer push de un tag de versión (`git tag vX.Y.Z && git push origin vX.Y.Z`), se dispara automáticamente el workflow `.github/workflows/release.yml`.
   - El workflow decodifica el almacén de claves seguro desde `secrets.RELEASE_KEYSTORE_BASE64`, compila `assembleRelease`, genera el archivo criptográfico `RestYourEyes-vX.Y.Z.apk.sha256` y publica ambos assets en GitHub Releases.
   - Si se requiere actualizar o rotar las credenciales del Keystore, se inyectan vía GitHub CLI:
     ```powershell
     [System.Convert]::ToBase64String([System.IO.File]::ReadAllBytes("release.jks")) | gh secret set RELEASE_KEYSTORE_BASE64 -R AnaCataVC/rest-your-eyes
     "android123" | gh secret set RELEASE_KEYSTORE_PASSWORD -R AnaCataVC/rest-your-eyes
     "restyoureyes" | gh secret set RELEASE_KEY_ALIAS -R AnaCataVC/rest-your-eyes
     "android123" | gh secret set RELEASE_KEY_PASSWORD -R AnaCataVC/rest-your-eyes
     ```
2. **Compilación Local de Respaldo:**
   Si se necesita compilar el APK de producción localmente sin internet o para pruebas offline, ejecutar:
   ```powershell
   ./generate_release.ps1
   ```
   El script compila, firma y extrae el APK final en `releases/RestYourEyes.apk`.
3. **Artefactos Oficiales de Release:**
   Los únicos entregables oficiales para cada versión en GitHub Releases son:
   - `RestYourEyes-vX.Y.Z.apk`
   - `RestYourEyes-vX.Y.Z.apk.sha256`

## 📱 Consideraciones de Sistema (Android)
- **Servicios en Segundo Plano (Background Services):** La aplicación utiliza un `ForegroundService` que se inicia automáticamente al arrancar el dispositivo (`BOOT_COMPLETED`). Cualquier cambio en la lógica del servicio debe garantizar su supervivencia y respetar las restricciones de consumo de batería de Android (Doze Mode).
- **Permisos Sensibles:** Esta aplicación requiere permisos especiales como `SYSTEM_ALERT_WINDOW` (para el Overlay) y `POST_NOTIFICATIONS`. No asumas que los permisos están concedidos, siempre verifica su estado en tiempo de ejecución.
- **Estándares y Contribución:** Consultar [`CONTRIBUTING.md`](./CONTRIBUTING.md) para convenciones de código Kotlin/Compose, flujo de ramas y Conventional Commits.
