<p align="center">
  <img src="icon.png" alt="rest-your-eyes Logo" width="120" />
</p>

# Rest Your Eyes

[English](README.md) | [Español](README.es.md)

[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat)](https://opensource.org/licenses/MIT)

---


### 1. Descripción del Proyecto
**Rest Your Eyes** es una aplicación nativa para Android diseñada para prevenir la fatiga visual generada por el uso prolongado de dispositivos móviles. Implementa la famosa regla 20-20-20: por cada 20 minutos de uso de pantalla, debes mirar un objeto a 20 pies de distancia durante 20 segundos.

Esta aplicación funciona discretamente en segundo plano. Cuando detecta que has usado tu teléfono de forma continua por el tiempo establecido (reiniciando la cuenta a cero si apagas la pantalla), superpone una pantalla recordatoria para forzarte amablemente a tomar un descanso, con opciones personalizables de sonido y cierre automático.

### 2. Tecnologías Utilizadas
- **Lenguaje:** Kotlin
- **Interfaz de Usuario:** Jetpack Compose (Material Design 3)
- **Persistencia de Datos:** Jetpack DataStore (Preferences)
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **APIs de Android:**
  - Servicios en Primer Plano (Foreground Services)
  - Broadcast Receivers (`ACTION_SCREEN_ON`/`OFF`)
  - WindowManager (`SYSTEM_ALERT_WINDOW` / Superposición)

### 3. Aprendizajes Clave
El desarrollo de este proyecto me enseñó a crear una aplicación Android simple pero efectiva, capaz de registrar el uso real del teléfono o tablet (tiempo de pantalla encendida/apagada) y de mostrar notificaciones superpuestas sobre lo que el usuario está haciendo para fomentar hábitos digitales más saludables.

### 4. Página de Producto
Puedes ver la página web oficial del proyecto aquí:
👉 [https://rest-your-eyes.ana-catalina.com](https://rest-your-eyes.ana-catalina.com)

### 5. Instrucciones de Configuración Local
Para ejecutar este proyecto en tu máquina local:
1. Clona este repositorio.
2. Abre el proyecto en **Android Studio**.
3. Deja que Gradle sincronice y resuelva todas las dependencias.
4. Ejecuta la app en un emulador o dispositivo físico (nivel mínimo de API 26).
5. Otorga los permisos necesarios (Notificaciones y Mostrar sobre otras apps) cuando la aplicación lo solicite.

---

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

