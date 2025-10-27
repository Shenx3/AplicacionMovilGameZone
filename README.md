🎮 GameZone: Tienda Móvil de Videojuegos
Descripción del Proyecto
GameZone es una aplicación móvil desarrollada en Kotlin con Jetpack Compose que simula una tienda en línea de videojuegos. Permite a los usuarios registrarse, iniciar sesión, explorar un catálogo de productos destacados, añadir ítems al carrito de compras y gestionar un perfil personal que incluye la captura de una foto con la cámara del dispositivo.

Características Principales
Autenticación Segura (Simulada): Pantallas de Login y Registro con validaciones y manejo de sesión.

Base de Datos Local (Room): Persistencia de datos de usuario mediante la librería Room.

Tienda y Carrito:

Vista Home que muestra productos destacados con una tarjeta interactiva (ProductCard).

Carrito de Compras que gestiona la adición y eliminación de ítems, y calcula el total de la compra.

Función Nativa (Cámara): El perfil de usuario permite tomar y guardar una foto de perfil, utilizando FileProvider para manejar Uri seguros.

Arquitectura MVVM: Implementación clara de Model-View-ViewModel utilizando StateFlow para el manejo reactivo del estado en Compose.

Estructura de la Aplicación (Estructura de Archivos Clave)
La aplicación sigue el estándar de la arquitectura MVVM:

data/: Contiene la capa de datos.

AppDataBase.kt: Configuración de la base de datos Room.

User.kt, UserDao.kt: Definición de la entidad y la interfaz de acceso a datos para el usuario.

Product.kt, CartItem.kt: Modelos de negocio para los productos y los ítems del carrito.

viewmodels/: Contiene la lógica de negocio y el estado de la UI.

LoginViewModel.kt, RegisterViewModel.kt, ForgotPasswordViewModel.kt: Manejan la lógica de autenticación y validación.

HomeViewModel.kt: Controla el estado del perfil de usuario y la lista de productos.

CartViewModel.kt: Gestiona el estado y la lógica del carrito de compras.

views/: Componentes Composables que representan la interfaz de usuario.

HomeView.kt, CartView.kt, CameraView.kt, LoginView.kt, etc..

navigation/: Define las rutas de navegación utilizando Compose Navigation.

Requisitos y Configuración
Para ejecutar este proyecto, necesitas:

Android Studio (versión compatible con Gradle 8.13).

Kotlin (Versión 2.0.21).

Android SDK con compileSdk = 36 y minSdk = 33.

Dependencias Destacadas
Las dependencias clave incluyen:

Jetpack Compose: Para la construcción de la UI declarativa.

Room: Para la capa de persistencia local de usuarios.

Coil: Para la carga asíncrona de imágenes (utilizado en la gestión de la foto de perfil).

Compose Navigation: Para manejar el flujo entre pantallas.

KSP: Para el procesamiento de anotaciones de Room.

Uso de la Cámara
La funcionalidad de la cámara se implementa en CameraView.kt.

Permiso: Solicita el permiso android.permission.CAMERA en tiempo de ejecución.

Almacenamiento Seguro: Utiliza FileProvider para generar un Uri seguro, guardando la imagen capturada en el directorio privado de la aplicación (external-files-path/Pictures) para evitar problemas de permisos de almacenamiento en versiones recientes de Android.

Persistencia: La Uri de la foto se guarda como una String en la base de datos Room del usuario.
