# UX Specification: Remediación UX/UI en Rest Your Eyes

> **Status:** Implemented
> **Target Interface:** Mobile (Android / Jetpack Compose)
> **Related Plan:** [implementation_plan.md](file:///C:/Users/anaca/.gemini/antigravity/brain/d3fb2c4f-c5bb-4b36-aa8f-0d42dfb4fe00/implementation_plan.md)

---

## 1. User Intent & Mental Model

- **Primary User Goal:** El usuario desea descansar la vista de forma regular mientras utiliza el dispositivo móvil sin fricción mental, confiando en que la aplicación monitorea de forma discreta y fidedigna el tiempo acumulado de pantalla bajo la regla 20-20-20.
- **Mental Model (Jakob\'s Law):** El usuario percibe la app como un temporizador de salud en segundo plano. Espera feedback claro e inmediato sobre si el rastreador está funcionando (`StatusActiveGreen` vs `StatusInactiveGray`), sliders directos para ajustar tiempos y un solo botón primario evidente para iniciar o detener el rastreo.
- **Progressive Disclosure Strategy:**
  - Los permisos requeridos (`SYSTEM_ALERT_WINDOW` y `POST_NOTIFICATIONS`) se destacan únicamente cuando están pendientes, reevaluándose automáticamente al volver de los ajustes del sistema (`ON_RESUME`).
  - Los ajustes secundarios (vibración y cierre automático) se agrupan en una tarjeta de preferencias con soporte de interacción para toda la fila (`toggleable` con `Role.Switch`).

---

## 2. The 5 Essential UX States

| Estado | Comportamiento en Pantalla | Elementos de Acción y Microcopy |
| :--- | :--- | :--- |
| **1. Ideal (Activo)** | Servicio en ejecución, punto verde visible, sliders interactivos que sincronizan en tiempo real. | Botón secundario contextual de parada: `"DETENER RASTREO"`. |
| **2. Inactivo / Pausado** | Servicio detenido, punto gris visible, sliders listos para configurar. | Botón primario prominente: `"INICIAR RASTREO"`. |
| **3. Descanso en Curso (Overlay)** | Fondo traslúcido con cuenta regresiva circular animada (`CircularProgressIndicator`) y número central. | Botón discreto para emergencias: `"Omitir descanso"` (`OutlinedButton`). |
| **4. Descanso Completado (Peak-End)** | Transición a mensaje positivo (*"¡Descanso completado!"* / *"Tus ojos están listos para continuar"*). | Botón primario de cierre amigable: `"Continuar"`. |
| **5. Permiso Pendiente (Recuperación)** | Banners contextuales de advertencia (`Card` coloreadas con explicación y CTA directo). | `"Otorgar permiso"` (superposición) / `"Habilitar notificaciones"`. |

---

## 3. Microcopy & Button Directory

| Elemento UI | Texto / Etiqueta | Propósito y Feedback |
| :--- | :--- | :--- |
| CTA Iniciar | `"INICIAR RASTREO"` | Inicia el `ForegroundService`, actualiza el estado a verde y muestra snackbar de confirmación. |
| CTA Detener | `"DETENER RASTREO"` | Detiene el servicio y actualiza el estado inmediatamente a gris. |
| Permiso Overlay | `"Otorgar permiso"` | Redirige a `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` y se actualiza al volver a la app. |
| Permiso Notificaciones | `"Habilitar notificaciones"` | Lanza el diálogo del sistema en Android 13+. |
| Overlay (En progreso) | `"Omitir descanso"` | Permite cerrar anticipadamente sin texto agresivo ni mayúsculas intrusivas. |
| Overlay (Finalizado) | `"Continuar"` | Confirma la finalización satisfactoria del descanso ocular. |

---

## 4. Interaction Ergonomics & Safety Guardrails

- **Single Primary Action Rule:** Un único botón primario (`Button`) activo por contexto. En `SettingsScreen`, el control principal alterna entre iniciar o detener el rastreo.
- **Área Táctil y Ley de Fitts:** Toda la fila de cada switch (`Switch`) es clickeable (`Modifier.toggleable`) con rol semántico de accesibilidad para lectores de pantalla.
- **Scroll y Adaptabilidad:** Toda la pantalla de ajustes implementa `verticalScroll(rememberScrollState())` previniendo recortes en pantallas compactas o rotación horizontal.
- **Supervivencia a Rotación en Descanso:** El estado del temporizador en `OverlayScreen` utiliza `rememberSaveable`, evitando que el tiempo restante se reinicie a 20 segundos al girar el dispositivo.
