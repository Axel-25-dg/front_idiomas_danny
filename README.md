<p align="center">
  <img src="capturas/Logo-ute.png" alt="Logo UTE" width="180"/>
</p>

<h1 align="center">JumpUp -- Plataforma Movil de Aprendizaje de Idiomas</h1>

<p align="center">
  <strong>Universidad Tecnologica Equinoccial (UTE)</strong><br/>
  Facultad de Ciencias de la Ingenieria e Industrias - Carrera de Software
</p>

<p align="center">
  <strong>Materia:</strong> Desarrollo Movil &nbsp;|&nbsp;
  <strong>Estudiante:</strong> Danny Guaman &nbsp;|&nbsp;
  <strong>Periodo:</strong> 2024-2025
</p>

---

## Indice

1. [Descripcion](#1-descripcion)
2. [Requisitos de instalacion](#2-requisitos-de-instalacion)
3. [Configuracion de la URL base del backend](#3-configuracion-de-la-url-base-del-backend)
4. [Usuarios y contrasenas de prueba](#4-usuarios-y-contrasenas-de-prueba)
5. [Capturas de pantalla](#5-capturas-de-pantalla)
6. [Las 7 entidades implementadas](#6-las-7-entidades-implementadas)
7. [Listado de pantallas](#7-listado-de-pantallas)
8. [Ejemplos de consumo de la API con token](#8-ejemplos-de-consumo-de-la-api-con-token)
9. [Instrucciones para ejecutar la app](#9-instrucciones-para-ejecutar-la-app)

---

## 1. Descripcion

**JumpUp** es una aplicacion movil Android desarrollada en **Kotlin** con **Jetpack Compose** que permite el aprendizaje gamificado de idiomas. Consume una **API REST** construida con **Django REST Framework** y **PostgreSQL**, desplegada en produccion en:

> **https://guaman-idiomas-ute.online/api/**

La plataforma implementa tres roles diferenciados:

| Rol | Descripcion |
|---|---|
| **Administrador** | Gestion completa de usuarios, cursos, ordenes, suscripciones y auditoria del sistema |
| **Profesor** | Creacion de aulas virtuales, lecciones interactivas, examenes y recursos educativos |
| **Estudiante** | Experiencia gamificada con ejercicios, 6 juegos, logros, ranking y certificados |

Caracteristicas tecnicas principales:

- Autenticacion con **JWT** (access + refresh token)
- **CRUD completo** sobre 7 entidades del dominio
- Busqueda con paginacion en todos los listados
- Manejo de errores HTTP con mensajes descriptivos
- Diferenciacion de permisos por rol en cada endpoint
- Modo oscuro, XP automatico y sistema de logros

---

## 2. Requisitos de instalacion

### Para ejecutar el APK (usuario final)

| Requisito | Detalle |
|---|---|
| Android | 8.0 Oreo (API 26) o superior |
| Conexion | Internet activa |
| Almacenamiento | ~50 MB libres |

### Para compilar desde el codigo fuente (desarrolladores)

| Herramienta | Version minima |
|---|---|
| Android Studio | Hedgehog 2023.1 o superior |
| JDK | 17 |
| Gradle | 8.x (incluido en el wrapper) |
| Android SDK | API 26+ |
| Dispositivo/Emulador | API 26 - 35 |

### Stack tecnologico

| Tecnologia | Version | Uso |
|---|---|---|
| Kotlin | 2.0+ | Lenguaje principal |
| Jetpack Compose | BOM 2024+ | UI declarativa |
| Material 3 | Ultima estable | Sistema de diseno y componentes |
| Hilt | 2.51+ | Inyeccion de dependencias |
| Retrofit | 2.9+ | Consumo de APIs REST |
| OkHttp | 4.12+ | Cliente HTTP con interceptor JWT |
| Navigation Compose | 2.7+ | Navegacion entre pantallas |
| DataStore Preferences | 1.0+ | Persistencia local de sesion |
| Coil | 2.5+ | Carga y cache de imagenes |
| Coroutines + StateFlow | - | Programacion reactiva asincrona |

---

## 3. Configuracion de la URL base del backend

La URL base de la API se configura en `local.properties`, en la raiz del proyecto:

```properties
API_BASE_URL=https://guaman-idiomas-ute.online/api/
```

Esta variable es leida en tiempo de compilacion por `build.gradle.kts` e inyectada como `BuildConfig.API_BASE_URL` en el modulo de red (`NetworkModule.kt`).

Para desarrollo local, cambia la URL a:

```properties
API_BASE_URL=http://10.0.2.2:8000/api/
```

> `10.0.2.2` es el alias del host desde el emulador de Android Studio.

El `local.properties` completo debe quedar asi:

```properties
sdk.dir=C:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
API_BASE_URL=https://guaman-idiomas-ute.online/api/
```

---

## 4. Usuarios y contrasenas de prueba

Los siguientes usuarios estan disponibles en el entorno de produccion:

| Rol | Correo | Contrasena | Que puede hacer |
|---|---|---|---|
| **Admin** | `alexander18br17@gmail.com` | `principe123` | Panel completo: usuarios, cursos, ordenes, suscripciones, auditoria |
| **Profesor** | `profe1@gmail.com` | `principe123` | Crear clases, agregar lecciones, examenes y recursos educativos |
| **Estudiante** | `alex1234@gmail.com` | `principe123` | Home gamificado, catalogo, juegos, carrito de compras, certificados |

---

## 5. Capturas de pantalla

### Autenticacion

| Login | Registro | Credenciales Validas | Error de Credenciales |
|:---:|:---:|:---:|:---:|
| ![Login](capturas/login.jpg) | ![Registro](capturas/register.jpg) | ![Validas](capturas/credenciales_validas.jpg) | ![Error](capturas/error_credenciales.jpg) |

### Panel del Estudiante

| Home | Catalogo de Cursos | Carrito | Compra |
|:---:|:---:|:---:|:---:|
| ![Home](capturas/home_usuario.jpg) | ![Catalogo](capturas/catalogo_user.jpg) | ![Carrito](capturas/carrito_user.jpg) | ![Compra](capturas/compra_user.jpg) |

| Mis Clases | Contenido de Clase | Centro de Juegos | Perfil |
|:---:|:---:|:---:|:---:|
| ![Clases](capturas/clases_usuario.jpg) | ![Contenido](capturas/contenido_clase_user.jpg) | ![Juegos](capturas/centro_juegos.jpg) | ![Perfil](capturas/perfil_usuario.jpg) |

### Panel del Profesor

| Mis Clases | Estudiantes | Lecciones Interactivas | Recursos |
|:---:|:---:|:---:|:---:|
| ![Clases](capturas/clases_profejpg.jpg) | ![Estudiantes](capturas/estudiantes_profe.jpg) | ![Lecciones](capturas/lecciones_profe.jpg) | ![Recursos](capturas/recursos_profe.jpg) |

| Perfil Profesor |
|:---:|
| ![Perfil](capturas/perfil_profe.jpg) |

### Panel del Administrador

| Dashboard | Usuarios | Cursos | Ordenes |
|:---:|:---:|:---:|:---:|
| ![Dashboard](capturas/deshboard_admin.jpg) | ![Usuarios](capturas/usuarios_admin.jpg) | ![Cursos](capturas/cursos_admin.jpg) | ![Ordenes](capturas/ordenes_admin.png) |

| Suscripciones |
|:---:|
| ![Suscripciones](capturas/subscripciones_admin.jpg) |

---

## 6. Las 7 entidades implementadas

### Entidad 1 -- Courses (Cursos)

Representa los cursos de idiomas disponibles en la plataforma. Cada curso pertenece a un idioma, tiene un precio, nivel de dificultad (A1-C2) y stock de cupos disponibles.

| Operacion | Metodo | Endpoint |
|---|---|---|
| Listar | `GET` | `/api/courses/` |
| Detalle | `GET` | `/api/courses/{id}/` |
| Crear | `POST` | `/api/courses/` |
| Actualizar | `PUT` | `/api/courses/{id}/` |
| Eliminar | `DELETE` | `/api/courses/{id}/` |

**Archivo:** `data/remote/api/CourseApi.kt` | **Pantalla:** Admin - Cursos

---

### Entidad 2 -- Modules (Modulos)

Cada curso se divide en modulos secuenciales que agrupan lecciones y ejercicios por temas especificos.

| Operacion | Metodo | Endpoint |
|---|---|---|
| Listar | `GET` | `/api/modules/?course={id}` |
| Detalle | `GET` | `/api/modules/{id}/` |
| Crear | `POST` | `/api/modules/` |
| Actualizar | `PUT` | `/api/modules/{id}/` |
| Eliminar | `DELETE` | `/api/modules/{id}/` |

**Archivo:** `data/remote/api/ModuleApi.kt` | **Pantalla:** Admin - Modulos / Ruta de Aprendizaje

---

### Entidad 3 -- Classrooms (Aulas Virtuales)

Los profesores crean aulas virtuales asociadas a un curso. Los estudiantes se unen mediante un codigo de acceso unico generado automaticamente.

| Operacion | Metodo | Endpoint |
|---|---|---|
| Listar | `GET` | `/api/teacher/classrooms/` |
| Detalle | `GET` | `/api/teacher/classrooms/{id}/` |
| Crear | `POST` | `/api/teacher/classrooms/` |
| Actualizar | `PUT` | `/api/teacher/classrooms/{id}/` |
| Eliminar | `DELETE` | `/api/teacher/classrooms/{id}/` |

**Archivo:** `data/remote/api/TeacherClassroomApi.kt` | **Pantalla:** Profesor - Mis Clases

---

### Entidad 4 -- Users (Usuarios)

Gestion administrativa de todos los usuarios del sistema. Permite crear, activar/desactivar y asignar roles (admin, profesor, estudiante).

| Operacion | Metodo | Endpoint |
|---|---|---|
| Listar todos | `GET` | `/api/users/` |
| Listar estudiantes | `GET` | `/api/admin-students/` |
| Crear | `POST` | `/api/users/` |
| Actualizar | `PATCH` | `/api/users/{id}/` |
| Desactivar | `PATCH` | `/api/users/{id}/` (isActive=false) |

**Archivo:** `data/remote/api/AdminUsersApi.kt` | **Pantalla:** Admin - Usuarios

---

### Entidad 5 -- Orders (Ordenes de Compra)

Registra las inscripciones y compras de cursos. Gestiona el ciclo de vida de cada orden: pending - paid - completed / cancelled.

| Operacion | Metodo | Endpoint |
|---|---|---|
| Listar | `GET` | `/api/orders/` |
| Detalle | `GET` | `/api/orders/{id}/` |
| Crear | `POST` | `/api/orders/` |
| Agregar item | `POST` | `/api/orders/{id}/add_item/` |
| Confirmar | `POST` | `/api/orders/{id}/confirm/` |
| Cambiar estado | `PATCH` | `/api/orders/{id}/update_status/` |

**Archivo:** `data/remote/api/OrderApi.kt` | **Pantalla:** Admin - Ordenes / Estudiante - Historial

---

### Entidad 6 -- Resources (Recursos Educativos)

Material complementario que los profesores comparten con sus clases: PDFs, videos, audios y enlaces externos.

| Operacion | Metodo | Endpoint |
|---|---|---|
| Listar | `GET` | `/api/teacher/resources/` |
| Detalle | `GET` | `/api/teacher/resources/{id}/` |
| Crear | `POST` | `/api/teacher/resources/` |
| Actualizar | `PUT` | `/api/teacher/resources/{id}/` |
| Eliminar | `DELETE` | `/api/teacher/resources/{id}/` |

**Archivo:** `data/remote/api/TeacherResourceApi.kt` | **Pantalla:** Profesor - Recursos

---

### Entidad 7 -- Subscriptions (Suscripciones Premium)

Planes de suscripcion que desbloquean funcionalidades adicionales para los estudiantes.

| Operacion | Metodo | Endpoint |
|---|---|---|
| Listar planes | `GET` | `/api/subscriptions/` |
| Mis suscripciones | `GET` | `/api/my-subscriptions/` |
| Suscribirse | `POST` | `/api/my-subscriptions/` |
| Historial de pagos | `GET` | `/api/payments/` |

**Archivo:** `data/remote/api/SubscriptionApi.kt` | **Pantalla:** Estudiante - Premium

---

## 7. Listado de pantallas

La aplicacion cuenta con mas de 40 pantallas distribuidas en cuatro areas funcionales.

### Autenticacion

| Pantalla | Archivo | Descripcion |
|---|---|---|
| Login | `ui/auth/LoginScreen.kt` | Inicio de sesion con email y contrasena. Genera token JWT |
| Registro | `ui/auth/RegisterScreen.kt` | Creacion de cuenta nueva con validacion de campos |

### Panel del Estudiante (21 pantallas)

| Pantalla | Archivo | Descripcion |
|---|---|---|
| Home | `ui/home/HomeScreen.kt` | Dashboard con XP, racha diaria, cursos activos y accesos rapidos |
| Catalogo | `ui/home/CatalogScreen.kt` | Busqueda y paginacion de cursos disponibles |
| Detalle de Curso | `ui/home/CourseDetailScreen.kt` | Informacion completa del curso e inscripcion |
| Ruta de Aprendizaje | `ui/course/LearningPathScreen.kt` | Modulos y ejercicios estilo Duolingo con progreso visual |
| Ejercicios | `ui/course/ExerciseScreen.kt` | Multiple choice, traduccion y listening interactivos |
| Unirse a Clase | `ui/course/JoinClassScreen.kt` | Ingreso mediante codigo de acceso del profesor |
| Mis Clases | `ui/student/MyClassesScreen.kt` | Listado de aulas virtuales en las que esta inscrito |
| Mis Certificados | `ui/student/MyCertificatesScreen.kt` | Certificados obtenidos por completar cursos |
| Logros | `ui/student/AchievementsScreen.kt` | Achievements desbloqueados y progreso |
| Ranking | `ui/student/LeaderboardScreen.kt` | Tabla de posiciones por XP acumulado |
| Centro de Juegos | `ui/games/GameCenterScreen.kt` | Hub de los 6 juegos educativos |
| Word Match | `ui/games/WordMatchScreen.kt` | Emparejar palabras con su traduccion |
| Flashcards | `ui/games/FlashcardsScreen.kt` | Tarjetas de vocabulario con flip animado |
| Constructor de Oraciones | `ui/games/SentenceBuilderScreen.kt` | Ordenar palabras para formar oraciones |
| Vocab Quiz | `ui/games/VocabQuizScreen.kt` | Quiz de vocabulario con temporizador |
| Ahorcado | `ui/games/HangmanScreen.kt` | Adivinar la palabra oculta letra a letra |
| Memory Cards | `ui/games/MemoryCardsScreen.kt` | Encontrar pares de palabras e imagenes |
| Perfil | `ui/profile/ProfileScreen.kt` | Datos personales, estadisticas y logros |
| Premium | `ui/profile/PremiumScreen.kt` | Planes de suscripcion y historial de pagos |
| Ajustes | `ui/settings/SettingsScreen.kt` | Tema oscuro, notificaciones y preferencias |
| Ordenes | `ui/orders/OrdersScreen.kt` | Historial de compras y estados de ordenes |

### Panel del Profesor (5 pantallas)

| Pantalla | Archivo | Descripcion |
|---|---|---|
| Dashboard | `ui/teacher/TeacherDashboardScreen.kt` | Estadisticas y navegacion interna del panel |
| Mis Clases | `ui/teacher/screens/TeacherClassesSection.kt` | CRUD completo de aulas + codigo de acceso |
| Estudiantes | `ui/teacher/screens/TeacherStudentsSection.kt` | Lista de estudiantes por clase con XP |
| Lecciones/Examenes | `ui/teacher/screens/TeacherExamsSection.kt` | CRUD de lecciones interactivas y examenes |
| Recursos | `ui/teacher/screens/TeacherResourcesSection.kt` | Subida y gestion de PDFs, videos, audio y links |

### Panel del Administrador (7 pantallas)

| Pantalla | Archivo | Descripcion |
|---|---|---|
| Dashboard | `ui/admin/AdminDashboardScreen.kt` | NavigationDrawer con metricas globales del sistema |
| Usuarios | `ui/admin/sections/AdminUsersSection.kt` | CRUD completo con cambio de rol y activacion |
| Cursos | `ui/admin/sections/AdminCoursesSection.kt` | Gestion academica de todos los cursos |
| Ordenes | `ui/admin/sections/AdminOrdersSection.kt` | Aprobar, rechazar y filtrar ordenes |
| Suscripciones | `ui/admin/sections/AdminRemainingSection.kt` | Ingresos por suscripciones premium |
| Roles | `ui/admin/sections/AdminRemainingSection.kt` | CRUD de permisos y roles del sistema |
| Auditoria | `ui/admin/sections/AdminRemainingSection.kt` | Bitacora completa de acciones del sistema |

---

## 8. Ejemplos de consumo de la API con token

### 1. Obtener token JWT (Login)

```http
POST /api/auth/login/
Content-Type: application/json

{
  "email": "alex1234@gmail.com",
  "password": "principe123"
}
```

Respuesta 200 OK:

```json
{
  "access": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 2. Listar cursos con busqueda y paginacion

```http
GET /api/courses/?page=1&page_size=12&search=A1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Implementacion Kotlin (Retrofit):

```kotlin
@GET("courses/")
suspend fun getCourses(
    @Query("page") page: Int? = null,
    @Query("page_size") pageSize: Int? = null,
    @Query("search") search: String? = null
): Response<StaffPaginationResponse<CourseDto>>
```

---

### 3. Crear un aula virtual (Profesor)

```http
POST /api/teacher/classrooms/
Authorization: Bearer <token_profesor>
Content-Type: application/json

{
  "course_id": 1,
  "name": "Ingles Basico - Grupo A",
  "description": "Clase para principiantes absolutos"
}
```

Respuesta 201 Created:

```json
{
  "id": 5,
  "course_id": 1,
  "name": "Ingles Basico - Grupo A",
  "description": "Clase para principiantes absolutos",
  "access_code": "ABC123",
  "created_at": "2025-01-15T10:30:00Z"
}
```

---

### 4. Flujo completo de compra (Estudiante)

Paso 1 - Crear la orden:

```http
POST /api/orders/
Authorization: Bearer <token_estudiante>
Content-Type: application/json

{
  "total_amount": 0.0,
  "payment_method": "credit_card"
}
```

Paso 2 - Agregar un curso a la orden:

```http
POST /api/orders/1/add_item/
Authorization: Bearer <token_estudiante>
Content-Type: application/json

{
  "course_id": 3,
  "quantity": 1
}
```

Paso 3 - Confirmar la orden:

```http
POST /api/orders/1/confirm/
Authorization: Bearer <token_estudiante>
```

---

### 5. Suscribirse a un plan Premium

```http
POST /api/my-subscriptions/
Authorization: Bearer <token_estudiante>
Content-Type: application/json

{
  "subscription": 2
}
```

---

### 6. Interceptor automatico de token (JWT)

El siguiente interceptor inyecta el header `Authorization: Bearer <token>` en todas las peticiones HTTP de forma automatica:

```kotlin
class AuthInterceptor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenDataStore.accessToken.first() }
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

---

## 9. Instrucciones para ejecutar la app

### Paso 1 - Clonar el repositorio

```bash
git clone https://github.com/Axel-25-dg/front_idiomas_danny.git
cd front_idiomas_danny
```

### Paso 2 - Abrir en Android Studio

Abre Android Studio, selecciona File, Open y elige la carpeta del proyecto clonado.

### Paso 3 - Configurar local.properties

Verifica que el archivo `local.properties` en la raiz del proyecto contenga:

```properties
sdk.dir=C:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
API_BASE_URL=https://guaman-idiomas-ute.online/api/
```

### Paso 4 - Sincronizar Gradle

Android Studio lo hara automaticamente al abrir el proyecto. Si no, ve a File, Sync Project with Gradle Files.

### Paso 5 - Ejecutar la aplicacion

1. Conecta un dispositivo fisico con Depuracion USB habilitada, o inicia un emulador con API 26+.
2. Presiona Run en la barra superior o usa el atajo Shift + F10.
3. Espera la compilacion e instalacion automatica en el dispositivo.

### Paso 6 - Probar los tres roles

| Rol | Correo | Contrasena | Que verificar |
|---|---|---|---|
| Admin | `alexander18br17@gmail.com` | `principe123` | Dashboard con metricas, CRUD de usuarios y cursos |
| Profesor | `profe1@gmail.com` | `principe123` | Crear aula virtual, agregar lecciones, subir recursos |
| Estudiante | `alex1234@gmail.com` | `principe123` | Home gamificado, catalogo, juegos, carrito de compras |

---

## Arquitectura

```
+------------------------------------------------------+
|                      UI Layer                        |
|  Screens (Jetpack Compose) -> ViewModels (StateFlow) |
+------------------------------------------------------+
|                    Domain Layer                       |
|  Repository Interfaces -> Domain Models              |
+------------------------------------------------------+
|                     Data Layer                        |
|  Repository Impl -> Retrofit APIs -> DTOs            |
+------------------------------------------------------+
|              Dependency Injection (Hilt)              |
|  NetworkModule -> RepositoryModule                   |
+------------------------------------------------------+
```

Patron: MVVM + Repository Pattern + Clean Architecture

---

## Informacion Academica

| Campo | Detalle |
|---|---|
| Proyecto | Plataforma de Aprendizaje de Idiomas - JumpUp |
| Universidad | Universidad Tecnologica Equinoccial (UTE) |
| Facultad | Ciencias de la Ingenieria e Industrias |
| Carrera | Software |
| Estudiante | Danny Guaman |
| Backend | Django REST Framework + PostgreSQL |
| Frontend | Kotlin + Jetpack Compose |
| Despliegue API | https://guaman-idiomas-ute.online/api/ |
