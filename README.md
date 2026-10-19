# 🛒 CostoStock - App de Rescate de Stock y Reducción de Huella de Carbono

CostoStock es una aplicación móvil nativa para Android diseñada para conectar a los usuarios con comercios locales, permitiendo la compra de lotes de productos a punto de expirar o en sobrestock. El objetivo principal es reducir el desperdicio de alimentos y medir el impacto ambiental positivo (reducción de huella de carbono) de cada usuario.

## 🛠 Tecnologías y Arquitectura

El proyecto está desarrollado 100% en **Kotlin** utilizando las últimas herramientas recomendadas por Google para el desarrollo moderno en Android:

* **UI Declarativa:** Jetpack Compose (Material Design 3).
* **Arquitectura:** MVVM (Model-View-ViewModel) con dependencias inyectadas vía `ViewModelFactory`.
* **Persistencia Local:** Room Database (SQLite) para almacenamiento offline.
* **Asincronía y Reactividad:** Kotlin Coroutines y `StateFlow`.
* **Navegación:** Jetpack Navigation Compose con transiciones animadas personalizadas.
* **Integración Nativa:** `NotificationManager` para notificaciones locales (Push).

## ✨ Funcionalidades Principales (Módulo 3)

1. **Gestión de Carrito y Checkout (`CarritoCheckoutScreen`)**
    * Temporizador de reserva de productos con bloqueo dinámico de botones.
    * Validación de formulario de usuario en tiempo real (Nombre, Correo, Teléfono).
    * Simulación de pasarela de pago asíncrona con animaciones de estado (Procesando -> Éxito).

2. **Persistencia de Vouchers y Compras (`VouchersScreen` & Room)**
    * Generación de Vouchers Digitales detallados (Código QR simulado, comercio, dirección de retiro, horario).
    * Guardado local mediante `VoucherDao` asegurando disponibilidad offline del historial de compras.
    * Ordenamiento cronológico automático en UI (basado en `fechaEmisionMillis`).

3. **Dashboard de Impacto Ambiental (`ImpactoScreen` & Room)**
    * Cálculo acumulativo de kilogramos de CO2 evitado por el usuario.
    * Representación de equivalencias ecológicas dinámicas (Árboles plantados, lotes salvados).
    * Consultas reactivas con `Flow` para actualizar la pantalla instantáneamente tras cada confirmación de compra.

4. **Notificaciones Nativas Locales (`GestorNotificaciones`)**
    * Integración con APIs nativas de Android (soporte y gestión de permisos para Android 13+).
    * Canal de notificaciones dedicado ("Vouchers y Compras") de alta prioridad.
    * Alertas push instantáneas al completar el flujo de pago de manera exitosa.

## 🚀 Instalación y Ejecución

1. Clona este repositorio en tu máquina local.
2. Abre la carpeta del proyecto utilizando **Android Studio**.
3. Espera a que Gradle sincronice todas las dependencias.
4. Selecciona un emulador (API 24+) o un dispositivo físico conectado con depuración USB activa.
5. Ejecuta la aplicación (**Shift + F10** o botón Play).

> **Nota de Permisos:** Si ejecutas la aplicación en Android 13 (API 33) o superior, asegúrate de aceptar la solicitud del sistema para enviar notificaciones durante la primera compra para disfrutar de la experiencia completa.

## 👥 Equipo de Desarrollo

* **Mario Quintero:** [BASE] Configuración de arquitectura MVVM, dependencias y Contrato de Datos[cite: 6]. [Módulo 1] Creación de CatalogoViewModel, integración de recurso nativo GPS y diseño de las pantallas Catálogo, Mapa y Detalle[cite: 6, 7].
* **Romina Carrasco:** [Módulo 2] Diseño de CarritoCheckoutScreen, creación de CarritoCheckoutViewModel y configuración de la navegación con AppNavigation[cite: 6].
* **Nicolás Jaramillo:** [Módulo 3] Base de Datos Room, DAOs, ViewModels, Gestor de Notificaciones Nativas y pantallas de Vouchers/Impacto.
* **Nicole Valero:** [Módulo 4] Creación de ProveedorViewModel, integración de recurso nativo Cámara y diseño de pantallas PublicarLote y ValidarVoucher[cite: 7].