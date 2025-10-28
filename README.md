# 🎮 GameZone: Tu Tienda Móvil de Videojuegos 🕹️

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-darkgreen.svg)](https://developer.android.com/jetpack/compose)
[![Room DB](https://img.shields.io/badge/DB-Room%20DB-lightgray.svg)](https://developer.android.com/topic/libraries/architecture/room)

## 🌟 Resumen del Proyecto

**GameZone** es una aplicación de comercio electrónico móvil (e-commerce) desarrollada en **Kotlin** con **Jetpack Compose** que simula una tienda en línea de videojuegos. Su objetivo es demostrar la implementación de una arquitectura limpia (MVVM) y la integración de funcionalidades clave de Android, como la persistencia de datos y el acceso a la cámara nativa.

## ✨ Tecnologías y Arquitectura

| Concepto | Tecnología Clave | Uso/Implementación |
| :--- | :--- | :--- |
| **Arquitectura** | MVVM + StateFlow | Manejo reactivo del estado en Compose (Unidirectional Data Flow). |
| **Persistencia** | Room Database | Almacenamiento local de datos de usuario (tabla `users`). |
| **Navegación** | Compose Navigation | Flujo de navegación principal (`MainActivity`) y anidado (`MenuShellView`). |
| **Imágenes** | Coil-Compose | Carga asíncrona de imágenes (usado para la foto de perfil). |
| **Estilo** | Material 3 (Dark Theme) | Diseño forzado al tema oscuro para una estética "gamer". |

## 🚀 Módulos y Funcionalidades

### 1. Autenticación y Cuentas
* **Login y Registro**: Validación de formularios y lógica de acceso/creación de cuenta gestionada por `LoginViewModel` y `RegisterViewModel`.
* **Recuperación de Contraseña**: Implementación de `ForgotPasswordView` con lógica para actualizar credenciales existentes.

### 2. Tienda y Carrito 🛒
* **Vista Principal (`HomeView`)**: Muestra los productos destacados en una cuadrícula (Grid) y permite al usuario añadir ítems al carrito.
* **Lógica de Carrito (`CartViewModel`)**: Una única instancia de `CartViewModel` es compartida por la jerarquía de navegación interna (`MenuShellView`), asegurando que el estado del carrito (ítems y total) se mantenga y esté sincronizado.

### 3. Función Nativa: Cámara (Perfil) 📸
* **Toma de Foto**: Integración de la cámara para capturar y establecer una foto de perfil.
* **Manejo de Uri Seguro**: Se utiliza `FileProvider` para generar un `Uri` con permisos temporales, siguiendo las prácticas de seguridad de Android para el acceso a archivos. La Uri de la foto es persistida en la base de datos del usuario.

## ⚙️ Configuración del Proyecto

### Requisitos del Entorno
* **Android Studio**
* **JDK 11** o superior
* **SDK Android**: `compileSdk` 36, `minSdk` 33.

### ✨ Integrantes del Equipo
* **Adrian Diaz**
* **Bastián Bravo**
