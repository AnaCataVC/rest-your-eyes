# Rest Your Eyes

[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=white)](https://vitejs.dev/)
[![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=flat&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![Vercel](https://img.shields.io/badge/Vercel-000000?style=flat&logo=vercel&logoColor=white)](https://vercel.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat)](https://opensource.org/licenses/MIT)

[English](#english) | [Español](#español)

---

<a name="english"></a>
## English

### 1. Project Description
**Rest Your Eyes** is a native Android application designed to prevent eye strain caused by prolonged mobile device usage. It enforces the famous 20-20-20 rule: for every 20 minutes of screen time, you should look at an object 20 feet away for 20 seconds. 

The app runs quietly in the background using a foreground service. When it detects that you have been actively using your phone for the set time limit, it overlays a reminder screen to gently force you to take a break, featuring customizable sound alerts and auto-dismiss options.

### 2. Technologies Used
**Android Application:**
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Data Persistence:** Jetpack DataStore (Preferences)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Android APIs:** Foreground Services, Broadcast Receivers, WindowManager (`SYSTEM_ALERT_WINDOW`)

**Landing Page:**
- **Framework:** Vite
- **Styling:** Tailwind CSS v4, Vanilla CSS
- **Deployment:** Vercel

### 3. Key Learnings
*(Placeholder: To be filled after development is complete)*
> Note: What did you learn while building this project? (e.g., handling background services in modern Android, using Jetpack Compose overlays). 

### 4. Live Demo
You can view the official landing page for the project here:
👉 [Rest Your Eyes - Web](https://rest-your-eyes.vercel.app) *(Update this with your actual Vercel URL)*

### 5. Local Setup Instructions
To run this project locally on your machine:
1. Clone this repository.
2. Open the project in **Android Studio**.
3. Let Gradle sync and resolve all dependencies.
4. Run the app on an emulator or a physical device (minimum API level 26).
5. Grant the necessary permissions (Notifications and Display over other apps) when prompted.

---

<a name="español"></a>
## Español

### 1. Descripción del Proyecto
**Rest Your Eyes** es una aplicación nativa para Android diseñada para prevenir la fatiga visual generada por el uso prolongado de dispositivos móviles. Implementa la famosa regla 20-20-20: por cada 20 minutos de uso de pantalla, debes mirar un objeto a 20 pies de distancia durante 20 segundos.

Esta aplicación funciona discretamente en segundo plano. Cuando detecta que has usado tu teléfono activamente por el tiempo establecido, superpone una pantalla recordatoria para forzarte amablemente a tomar un descanso, con opciones personalizables de sonido y cierre automático.

### 2. Tecnologías Utilizadas
**Aplicación Android:**
- **Lenguaje:** Kotlin
- **Interfaz de Usuario:** Jetpack Compose (Material Design 3)
- **Persistencia de Datos:** Jetpack DataStore (Preferences)
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **APIs de Android:** Servicios en Primer Plano, Broadcast Receivers, WindowManager (`SYSTEM_ALERT_WINDOW`)

**Página Web (Landing Page):**
- **Framework:** Vite
- **Estilos:** Tailwind CSS v4, Vanilla CSS
- **Despliegue:** Vercel

### 3. Aprendizajes Clave
*(Espacio reservado: Para ser completado al finalizar el desarrollo)*
> Nota: ¿Qué aprendiste al construir este proyecto? (Ej. manejo de servicios en segundo plano en Android moderno, superposiciones con Jetpack Compose).

### 4. Demo en Vivo
Puedes ver la página web oficial del proyecto aquí:
👉 [Rest Your Eyes - Web](https://rest-your-eyes.vercel.app) *(Actualiza esto con tu URL real de Vercel)*

### 5. Instrucciones de Configuración Local
Para ejecutar este proyecto en tu máquina local:
1. Clona este repositorio.
2. Abre el proyecto en **Android Studio**.
3. Deja que Gradle sincronice y resuelva todas las dependencias.
4. Ejecuta la app en un emulador o dispositivo físico (nivel mínimo de API 26).
5. Otorga los permisos necesarios (Notificaciones y Mostrar sobre otras apps) cuando la aplicación lo solicite.
