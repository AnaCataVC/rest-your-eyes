# Proyecto: Rest Your Eyes

## 📚 Contexto del Proyecto
Esta es una aplicación móvil nativa para Android enfocada en el descanso visual de los usuarios ("Rest Your Eyes"). Funciona como un temporizador y recordatorio en segundo plano.

## 🛠️ Stack Tecnológico
- **Plataforma:** Android
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **SDK:** `minSdk = 26` (Android 8.0), `targetSdk = 34` (Android 14)
- **Almacenamiento Local:** DataStore Preferences
- **Gestor de Dependencias:** Gradle (Kotlin DSL - `build.gradle.kts`)

## 🚀 Flujo de Producción y Releases (IMPORTANTE)
Para publicar nuevas versiones o crear _releases_ de esta aplicación, se **DEBE** utilizar un script automatizado local en lugar de ejecutar comandos de Gradle manualmente.

1. **Compilación de Producción:** 
   Nunca generes el APK manualmente. Siempre debes ejecutar el script automatizado en la terminal:
   ```powershell
   ./generate_release.ps1
   ```
2. **Artefactos (Build Outputs):**
   El script anterior se encargará de compilar, firmar y extraer el APK final en la ruta `releases/RestYourEyes.apk`. Este es el único archivo válido para distribución.
3. **GitHub Releases:**
   Cuando se te pida crear o publicar un _Release_ en GitHub (por ejemplo, a través del agente `ami-release-manager`), **siempre** debes ejecutar `./generate_release.ps1` primero para actualizar el archivo, y luego adjuntar el archivo `./releases/RestYourEyes.apk` directamente en el Release de GitHub. No subas artefactos en Debug ni APKs con otros nombres.

## 📱 Consideraciones de Sistema (Android)
- **Servicios en Segundo Plano (Background Services):** La aplicación utiliza un `ForegroundService` que se inicia automáticamente al arrancar el dispositivo (`BOOT_COMPLETED`). Cualquier cambio en la lógica del servicio debe garantizar su supervivencia y respetar las restricciones de consumo de batería de Android (Doze Mode).
- **Permisos Sensibles:** Esta aplicación requiere permisos especiales como `SYSTEM_ALERT_WINDOW` (para el Overlay) y `POST_NOTIFICATIONS`. No asumas que los permisos están concedidos, siempre verifica su estado en tiempo de ejecución.
