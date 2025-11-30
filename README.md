# 🎮 Aplicación Móvil GameZone

Aplicación de comercio electrónico (e-commerce) para la venta de videojuegos y artículos relacionados, desarrollada en Kotlin utilizando **Android Jetpack Compose**.

Este proyecto sirve como la base para la evaluación de desarrollo de aplicaciones móviles.

## 🚀 Tecnologías Clave del Frontend (Android)

* **Lenguaje de Programación:** Kotlin
* **Interfaz de Usuario:** Jetpack Compose
* **Arquitectura:** Sigue principios de la arquitectura MVI/MVVM (Model-View-Intent/Model-View-ViewModel) utilizando:
    * **ViewModels:** Para la lógica de negocio y estado.
    * **Navigation Compose:** Para la gestión de la navegación entre pantallas.
* **Persistencia de Sesión:** Utiliza `SharedPreferences` (implementado en `SessionManager.kt`) para mantener la sesión del usuario.
* **Inyección de Dependencias (Futuro):** La estructura del proyecto está preparada para la implementación de un patrón de inyección de dependencias (por ejemplo, con Koin o Hilt) para los ViewModels y servicios.
* **Conectividad de Red:**
    * **Retrofit:** Para las llamadas a la API REST.
    * **Kotlin Coroutines:** Para la gestión de operaciones asíncronas.

## 💻 Módulos y Componentes Principales

El proyecto se estructura en las siguientes vistas (Composables) y lógica de negocio:

* **Vistas Principales (`views/`)**
    * `WelcomeView.kt`: Pantalla de bienvenida.
    * `LoginView.kt`: Interfaz de inicio de sesión.
    * `RegisterView.kt`: Interfaz de registro de nuevos usuarios.
    * `ForgotPasswordView.kt`: Interfaz para la recuperación de contraseña.
    * `MenuShellView.kt`: Estructura del menú principal y navegación interna.
    * `HomeView.kt`: Vista principal del catálogo de productos.
    * `CartView.kt`: Vista del carrito de compras.
    * `CameraView.kt`: Implementación para el uso de la cámara.
* **Lógica de Estado (`viewmodels/`)**
    * `LoginViewModel.kt`, `RegisterViewModel.kt`, `ForgotPasswordViewModel.kt`, `HomeViewModel.kt`, `CartViewModel.kt`.
* **Modelos de Datos (`data/`)**
    * `User.kt`, `Product.kt`, `CartItem.kt`.
* **Capa de Red (`network/`)**
    * `RetrofitClient.kt`: Configuración de la instancia de Retrofit.
    * `GameZoneApiService.kt`: Interfaz con las definiciones de los endpoints de la API.

## ⚙️ Configuración del Backend (Próximamente)

El frontend espera conectarse a una API REST. La implementación del backend se realizará en **Spring Boot**, siguiendo los requisitos de la rúbrica.

* **URL Base de la API (Configuración de desarrollo):** La URL base se configura en `RetrofitClient.kt`. Actualmente, está apuntando a un valor de prueba: `BASE_URL = "http://10.0.2.2:8080/api/"` (Para emulador Android).
* **Tecnologías Previstas para el Backend:**
    * **Spring Boot:** Framework principal.
    * **Base de Datos:** Se definirá en base a la rúbrica (ej. H2, PostgreSQL, MySQL).
    * **Seguridad:** Spring Security (para autenticación y autorización).

## 📄 Licencia

(Agregar información de licencia si aplica)
