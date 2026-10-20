# 🛒 CostoStock | Marketplace Móvil de Excedentes

> Conectando comercios locales con usuarios para reducir el desperdicio de alimentos a través de la tecnología, generando un impacto ambiental medible y positivo.

CostoStock es una aplicación móvil nativa para Android diseñada para la adquisición de lotes de productos a punto de expirar o en sobrestock a precios de oferta bajo su valor normal. Su objetivo principal es mitigar el desperdicio de alimentos mientras calcula y transparenta la reducción de la huella de carbono de cada usuario.

## 🛠 Tecnologías y Arquitectura
Proyecto desarrollado 100% en **Kotlin**, adoptando las directrices modernas de Google para el ecosistema Android:
* **Lenguaje:** Kotlin
* **UI Declarativa:** Jetpack Compose (Material Design 3)
* **Arquitectura:** MVVM (Model-View-ViewModel) con dependencias inyectadas vía `ViewModelFactory`
* **Persistencia Local:** Room Database (SQLite)
* **Asincronía:** Coroutines y `StateFlow`
* **Navegación:** Jetpack Navigation Compose
* **Hardware y SO:** Google Play Services (Location API), Android `NotificationManager` y Camera API

---

## 👥 Nombres de los/las estudiantes

* **MARIO ANTONIO QUINTERO ALMENDRAS:** 
  * **[BASE]** Configuración de arquitectura MVVM, dependencias Gradle y Contrato de Datos.
  * **[Módulo 1]** Creación de CatalogoViewModel, integración de recurso nativo GPS y diseño de pantallas Catálogo, Mapa y Detalle.

* **ROMINA PAZ CARRASCO RUIZ:** 
  * **[Módulo 2]** Diseño de CarritoCheckoutScreen, creación de CarritoCheckoutViewModel y configuración de AppNavigation.

* **NICOLAS FERNANDO JARAMILLO ZAMORA:** 
  * **[Módulo 3]** Configuración de Base de Datos Room, DAOs, Gestor de Notificaciones Nativas y pantallas de Vouchers e Impacto.

* **NICOL ANDREINA VALERO LOPEZ:** 
  * **[Módulo 4]** Creación de ProveedorViewModel, integración de recurso nativo Cámara y diseño de pantallas PublicarLote y ValidarVoucher.

---

## ✨ Funcionalidades implementadas

### 1. Arquitectura y Navegación
* **Implementación MVVM:** Estructuración de la arquitectura utilizando **Jetpack Compose** y flujos reactivos mediante `StateFlow` y `ViewModel`.
* **Navegación Centralizada:** Gestión de rutas mediante `NavHost` y una barra inferior, soportando transiciones animadas y paso de parámetros seguros entre pantallas.

### 2. Catálogo y Filtrado en Tiempo Real
* **Catálogo Dinámico:** Despliegue de ofertas con opciones para alternar dinámicamente entre vista de lista y simulación de mapa visual.
* **Filtrado Espacial (GPS):** Filtrado de productos basado en el radio de distancia (5, 15 o 30 km), empleando la fórmula matemática de **Haversine** para cálculos de proximidad exactos.
* **Motor de Actualización:** Proceso en segundo plano que oculta automáticamente los productos sin stock y actualiza los estados al finalizar o caducar la oferta.

### 3. Geolocalización y Hardware
* **Integración GPS:** Uso de Google Play Services (`FusedLocationProviderClient`) para la captura precisa de las coordenadas del dispositivo.
* **Gestión de Permisos:** Administración en tiempo de ejecución para el acceso a la ubicación y a la cámara nativa para la captura fotográfica de los lotes.

### 4. Proceso de Reserva (Checkout) y Notificaciones
* **Sistema de Reserva:** Bloqueo de compra temporal mediante un temporizador de 10 minutos mientras se completa la validación del formulario de contacto.
* **Gestión Comercial:** Cálculo automático de una comisión por servicio del 5% y generación de un voucher digital de retiro tras simular el pago.
* **Alertas Nativas:** Emisión de notificaciones Push locales mediante `NotificationManager` para alertar al usuario sobre la transacción exitosa.

### 5. Módulo de Proveedores y Códigos QR
* **Publicación de Lotes:** Formulario con validaciones de campos y reglas de negocio estrictas que restringen la venta de alimentos frescos (ej. carne, fruta, verdura).
* **Transparencia de Precios:** Cálculo automático de una comisión comercial del 10% para estructurar y transparentar el precio final de la oferta al proveedor.
* **Validación de Autenticidad:** Pantalla de validación de códigos QR que verifica el voucher, previniendo la entrega de cupones expirados, ya entregados o pertenecientes a otros comercios.

### 6. Impacto Ambiental y Persistencia Local
* **Persistencia de Datos:** Implementación de base de datos local con motor **Room** (`SQLite`) para asegurar la persistencia de las métricas y el historial de vouchers.
* **Métricas Ecológicas:** Cálculo y visualización de impacto ambiental, transformando el total de kilogramos de CO2 evitados en su equivalencia de árboles plantados al año.

---

## 🚀 Pasos para ejecutar

1. Clona este repositorio en tu máquina local:
   ```bash
   git clone [https://github.com/tu-usuario/costostock.git](https://github.com/tu-usuario/costostock.git)
   ```
2. Abre la carpeta del proyecto utilizando **Android Studio**.
3. Sincroniza el proyecto con Gradle para descargar todas las dependencias.
4. Selecciona un emulador (API 26+) o conecta un dispositivo físico con depuración USB activa.
5. Ejecuta la aplicación presionando `Shift + F10` o el botón **Run**.

> ⚠️ **Nota de Permisos:** Si la ejecución se realiza en Android 13 (API 33) o superior, el sistema solicitará autorización para **enviar notificaciones** durante la primera compra. Debe aceptarse para asegurar el flujo completo de la experiencia.
