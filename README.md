<p align="center">
  <img src="capturas/Logo-ute.png" alt="Logo UTE" width="200"/>
</p>

<h1 align="center">JumpUp — Plataforma Móvil de Aprendizaje de Idiomas</h1>

<p align="center">
  <strong>Universidad Tecnológica Equinoccial (UTE)</strong><br/>
  Facultad de Ciencias de la Ingeniería e Industrias<br/>
  Carrera de Software
</p>

<p align="center">
  <strong>Materia:</strong> Desarrollo Móvil &nbsp;|&nbsp;
  <strong>Estudiante:</strong> Danny Guamán &nbsp;|&nbsp;
  <strong>Período:</strong> 2024-2025
</p>

---

## Descripción de la Aplicación

Aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose** que consume una API REST construida con **Django REST Framework** y **PostgreSQL**. La plataforma permite el aprendizaje de idiomas con tres roles diferenciados:

- **Administrador** — Gestión completa de usuarios, cursos, órdenes, suscripciones y auditoría del sistema.
- **Profesor** — Creación de clases virtuales, lecciones interactivas, exámenes y recursos educativos.
- **Estudiante** — Experiencia gamificada de aprendizaje con ejercicios, juegos, logros, ranking y certificados.

El sistema implementa autenticación JWT, operaciones CRUD completas sobre **7 entidades**, búsqueda con paginación, manejo de errores HTTP y diferenciación de permisos por rol.

---

## Requisitos de Instalación

| Requisito | Versión mínima |
|---|---|
| Android Studio | Hedgehog (2023.1) o superior |
| JDK | 17 |
| Gradle | 8.x (incluido en el wrapper) |
| Android SDK | API 26+ (Android 8.0 Oreo) |
| Dispositivo/Emulador | API 26 – 35 |
| Conexión a Internet | Requerida para consumir la API |

### Stack Tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Kotlin | 2.0+ | Lenguaje principal |
| Jetpack Compose | BOM 2024+ | UI declarativa |
| Material 3 | Última estable | Diseño y componentes |
| Hilt | 2.51+ | Inyección de dependencias |
| Retrofit | 2.9+ | Consumo de APIs REST |
| OkHttp | 4.12+ | Cliente HTTP con interceptor JWT |
| Navigation Compose | 2.7+ | Navegación entre pantallas |
| DataStore Preferences | 1.0+ | Persistencia local de sesión |
| Coil | 2.5+ | Carga de imágenes |
| Coroutines + StateFlow | — | Programación reactiva |

---

## Configuración de la URL Base del Backend

La URL base de la API está configurada en el archivo `local.properties` en la raíz del proyecto:

```properties
API_BASE_URL=https://guaman-idiomas-ute.online/api/
```

Esta variable es leída en tiempo de compilación por el `build.gradle.kts` e inyectada como `BuildConfig.API_BASE_URL` en el módulo de red (`NetworkModule.kt`).

> **Nota:** Si deseas apuntar a un backend local de desarrollo, cambia la URL a:
> ```properties
> API_BASE_URL=http://10.0.2.2:8000/api/
> ```
> (10.0.2.2 es el alias del host desde el emulador de Android)

---

## Usuario y Contraseña de Prueba

| Rol | Correo | Contraseña | Acceso |
|---|---|---|---|
| **Admin** | `alexander18br17@gmail.com` | `principe123` | Panel completo de administración |
| **Profesor** | `profe1@gmail.com` | `principe123` | Panel del profesor (clases, exámenes, recursos) |
| **Estudiante** | `alex1234@gmail.com` | `principe123` | Experiencia de aprendizaje completa |

---

## Capturas de Pantalla

### Autenticación

| Login | Registro | Credenciales Válidas | Error de Credenciales |
|:---:|:---:|:---:|:---:|
| ![Login](capturas/login.jpg) | ![Registro](capturas/register.jpg) | ![Válidas](capturas/credenciales_validas.jpg) | ![Error](capturas/error_credenciales.jpg) |

### Panel del Estudiante

| Home | Catálogo de Cursos | Carrito | Compra |
|:---:|:---:|:---:|:---:|
| ![Home](capturas/home_usuario.jpg) | ![Catálogo](capturas/catalogo_user.jpg) | ![Carrito](capturas/carrito_user.jpg) | ![Compra](capturas/compra_user.jpg) |

| Mis Clases | Contenido Clase | Centro de Juegos | Perfil |
|:---:|:---:|:---:|:---:|
| ![Clases](capturas/clases_usuario.jpg) | ![Contenido](capturas/contenido_clase_user.jpg) | ![Juegos](capturas/centro_juegos.jpg) | ![Perfil](capturas/perfil_usuario.jpg) |

### Panel del Profesor

| Clases | Estudiantes | Lecciones Interactivas | Recursos |
|:---:|:---:|:---:|:---:|
| ![Clases](capturas/clases_profejpg.jpg) | ![Estudiantes](capturas/estudiantes_profe.jpg) | ![Lecciones](capturas/lecciones_profe.jpg) | ![Recursos](capturas/recursos_profe.jpg) |

| Perfil Profesor |
|:---:|
| ![Perfil](capturas/perfil_profe.jpg) |

### Panel del Administrador

| Dashboard | Usuarios | Cursos | Órdenes |
|:---:|:---:|:---:|:---:|
| ![Dashboard](capturas/deshboard_admin.jpg) | ![Usuarios](capturas/usuarios_admin.jpg) | ![Cursos](capturas/cursos_admin.jpg) | ![Órdenes](capturas/ordenes_admin.png) |

| Suscripciones |
|:---:|
| ![Suscripciones](capturas/subscripciones_admin.jpg) |

---

## Explicación de las 7 Entidades Implementadas

### 1. Courses (Cursos)
Representa los cursos de idiomas disponibles en la plataforma. Cada curso pertenece a un idioma, tiene un precio, nivel de dificultad y stock de cupos.

| Operación | Método | Endpoint |
|---|---|---|
| Listar | GET | `/api/courses/` |
| Detalle | GET | `/api/courses/{id}/` |
| Crear | POST | `/api/courses/` |
| Actualizar | PUT | `/api/courses/{id}/` |
| Eliminar | DELETE | `/api/courses/{id}/` |

**Archivo API:** `data/remote/api/CourseApi.kt`
**Pantalla:** Admin → Cursos

---

### 2. Modules (Módulos)
Cada curso se compone de módulos secuenciales. Un módulo agrupa lecciones y ejercicios por temas.

| Operación | Método | Endpoint |
|---|---|---|
| Listar | GET | `/api/modules/?course={id}` |
| Detalle | GET | `/api/modules/{id}/` |
| Crear | POST | `/api/modules/` |
| Actualizar | PUT | `/api/modules/{id}/` |
| Eliminar | DELETE | `/api/modules/{id}/` |

**Archivo API:** `data/remote/api/ModuleApi.kt`
**Pantalla:** Admin → Módulos / Ruta de Aprendizaje

---

### 3. Classrooms (Aulas Virtuales)
Los profesores crean aulas virtuales asociadas a un curso. Los estudiantes se unen mediante un código de acceso único.

| Operación | Método | Endpoint |
|---|---|---|
| Listar | GET | `/api/teacher/classrooms/` |
| Detalle | GET | `/api/teacher/classrooms/{id}/` |
| Crear | POST | `/api/teacher/classrooms/` |
| Actualizar | PUT | `/api/teacher/classrooms/{id}/` |
| Eliminar | DELETE | `/api/teacher/classrooms/{id}/` |

**Archivo API:** `data/remote/api/TeacherClassroomApi.kt`
**Pantalla:** Profesor → Mis Clases

---

### 4. Users (Usuarios)
Gestión administrativa de todos los usuarios del sistema. Permite crear, activar/desactivar y asignar roles.

| Operación | Método | Endpoint |
|---|---|---|
| Listar | GET | `/api/users/` |
| Listar Estudiantes | GET | `/api/admin-students/` |
| Crear | POST | `/api/users/` |
| Actualizar | PATCH | `/api/users/{id}/` |
| Desactivar | PATCH | `/api/users/{id}/` (isActive=false) |

**Archivo API:** `data/remote/api/AdminUsersApi.kt`
**Pantalla:** Admin → Usuarios

---

### 5. Orders (Órdenes de Compra)
Registra las inscripciones y compras de cursos. Maneja estados (pending, paid, completed, cancelled).

| Operación | Método | Endpoint |
|---|---|---|
| Listar | GET | `/api/orders/` |
| Detalle | GET | `/api/orders/{id}/` |
| Crear | POST | `/api/orders/` |
| Agregar ítem | POST | `/api/orders/{id}/add_item/` |
| Confirmar | POST | `/api/orders/{id}/confirm/` |
| Cambiar estado | PATCH | `/api/orders/{id}/update_status/` |

**Archivo API:** `data/remote/api/OrderApi.kt`
**Pantalla:** Admin → Órdenes / Estudiante → Historial

---

### 6. Resources (Recursos Educativos)
Material complementario que los profesores comparten con sus clases: PDFs, videos, enlaces externos.

| Operación | Método | Endpoint |
|---|---|---|
| Listar | GET | `/api/teacher/resources/` |
| Detalle | GET | `/api/teacher/resources/{id}/` |
| Crear | POST | `/api/teacher/resources/` |
| Actualizar | PUT | `/api/teacher/resources/{id}/` |
| Eliminar | DELETE | `/api/teacher/resources/{id}/` |

**Archivo API:** `data/remote/api/TeacherResourceApi.kt`
**Pantalla:** Profesor → Recursos

---

### 7. Subscriptions (Suscripciones Premium)
Planes de suscripción que desbloquean funcionalidades adicionales para los estudiantes.

| Operación | Método | Endpoint |
|---|---|---|
| Listar planes | GET | `/api/subscriptions/` |
| Mis suscripciones | GET | `/api/my-subscriptions/` |
| Suscribirse | POST | `/api/my-subscriptions/` |
| Historial pagos | GET | `/api/payments/` |

**Archivo API:** `data/remote/api/SubscriptionApi.kt`
**Pantalla:** Estudiante → Premium

---

## Listado de Pantallas (40+)

### Autenticación
| Pantalla | Archivo | Descripción |
|---|---|---|
| Login | `ui/auth/LoginScreen.kt` | Email + contraseña con JWT |
| Registro | `ui/auth/RegisterScreen.kt` | Crear cuenta nueva |

### Panel Estudiante
| Pantalla | Archivo | Descripción |
|---|---|---|
| Home | `ui/home/HomeScreen.kt` | XP, racha, cursos, accesos rápidos |
| Catálogo | `ui/home/CatalogScreen.kt` | Búsqueda + paginación de cursos |
| Detalle Curso | `ui/home/CourseDetailScreen.kt` | Info del curso + inscripción |
| Ruta de Aprendizaje | `ui/course/LearningPathScreen.kt` | Módulos + ejercicios tipo Duolingo |
| Ejercicios | `ui/course/ExerciseScreen.kt` | Multiple choice, traducción, listening |
| Unirse a Clase | `ui/course/JoinClassScreen.kt` | Código de acceso |
| Mis Clases | `ui/student/MyClassesScreen.kt` | Clases inscritas |
| Mis Certificados | `ui/student/MyCertificatesScreen.kt` | Certificados obtenidos |
| Logros | `ui/student/AchievementsScreen.kt` | Achievements desbloqueados |
| Ranking | `ui/student/LeaderboardScreen.kt` | Top estudiantes por XP |
| Centro de Juegos | `ui/games/GameCenterScreen.kt` | 6 juegos interactivos |
| Word Match | `ui/games/WordMatchScreen.kt` | Emparejar palabras |
| Flashcards | `ui/games/FlashcardsScreen.kt` | Tarjetas de vocabulario |
| Constructor | `ui/games/SentenceBuilderScreen.kt` | Ordenar oraciones |
| Vocab Quiz | `ui/games/VocabQuizScreen.kt` | Quiz con temporizador |
| Ahorcado | `ui/games/HangmanScreen.kt` | Adivinar palabra |
| Memory Cards | `ui/games/MemoryCardsScreen.kt` | Encontrar pares |
| Perfil | `ui/profile/ProfileScreen.kt` | Datos + stats + logros |
| Premium | `ui/profile/PremiumScreen.kt` | Suscripciones + pagos |
| Ajustes | `ui/settings/SettingsScreen.kt` | Tema oscuro, notificaciones |
| Órdenes | `ui/orders/OrdersScreen.kt` | Historial de compras |

### Panel Profesor
| Pantalla | Archivo | Descripción |
|---|---|---|
| Dashboard | `ui/teacher/TeacherDashboardScreen.kt` | Stats + navegación interna |
| Mis Clases | `ui/teacher/screens/TeacherClassesSection.kt` | CRUD clases + código acceso |
| Estudiantes | `ui/teacher/screens/TeacherStudentsSection.kt` | Lista por clase + XP |
| Exámenes | `ui/teacher/screens/TeacherExamsSection.kt` | CRUD lecciones interactivas |
| Recursos | `ui/teacher/screens/TeacherResourcesSection.kt` | PDF, Video, Audio, Links |

### Panel Administrador
| Pantalla | Archivo | Descripción |
|---|---|---|
| Dashboard | `ui/admin/AdminDashboardScreen.kt` | NavigationDrawer + métricas |
| Usuarios | `ui/admin/sections/AdminUsersSection.kt` | CRUD + cambio de rol |
| Cursos | `ui/admin/sections/AdminCoursesSection.kt` | Gestión académica |
| Órdenes | `ui/admin/sections/AdminOrdersSection.kt` | Aprobar/filtrar |
| Suscripciones | `ui/admin/sections/AdminRemainingSection.kt` | Ingresos |
| Roles | `ui/admin/sections/AdminRemainingSection.kt` | CRUD permisos |
| Auditoría | `ui/admin/sections/AdminRemainingSection.kt` | Bitácora del sistema |

---

## Ejemplos de Consumo de la API con Token

### 1. Autenticación — Obtener Token JWT

```http
POST /api/auth/login/
Content-Type: application/json

{
  "email": "alex1234@gmail.com",
  "password": "principe123"
}
```

**Respuesta:**
```json
{
  "access": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 2. Listar Cursos (con token)

```http
GET /api/courses/?page=1&page_size=12&search=A1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Implementación en Kotlin:**
```kotlin
@GET("courses/")
suspend fun getCourses(
    @Query("page") page: Int? = null,
    @Query("page_size") pageSize: Int? = null,
    @Query("search") search: String? = null
): Response<StaffPaginationResponse<CourseDto>>
```

---

### 3. Crear una Clase Virtual (Profesor)

```http
POST /api/teacher/classrooms/
Authorization: Bearer <token_profesor>
Content-Type: application/json

{
  "course_id": 1,
  "name": "Inglés Básico - Grupo A",
  "description": "Clase para principiantes"
}
```

**Respuesta:**
```json
{
  "id": 5,
  "course_id": 1,
  "name": "Inglés Básico - Grupo A",
  "description": "Clase para principiantes",
  "access_code": "ABC123",
  "created_at": "2025-01-15T10:30:00Z"
}
```

---

### 4. Crear una Orden de Compra (Estudiante)

```http
POST /api/orders/
Authorization: Bearer <token_estudiante>
Content-Type: application/json

{
  "total_amount": 0.0,
  "payment_method": "credit_card"
}
```

```http
POST /api/orders/1/add_item/
Authorization: Bearer <token_estudiante>
Content-Type: application/json

{
  "course_id": 3,
  "quantity": 1
}
```

```http
POST /api/orders/1/confirm/
Authorization: Bearer <token_estudiante>
```

---

### 5. Suscribirse a un Plan Premium

```http
POST /api/my-subscriptions/
Authorization: Bearer <token_estudiante>
Content-Type: application/json

{
  "subscription": 2
}
```

---

### 6. Interceptor Automático de Token (Kotlin)

```kotlin
class AuthInterceptor(private val tokenDataStore: TokenDataStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenDataStore.accessToken.first() }
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

> El interceptor inyecta automáticamente el header `Authorization: Bearer <token>` en **todas** las peticiones HTTP sin necesidad de pasarlo manualmente.

---

## Instrucciones para Ejecutar la App

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/Axel-25-dg/front_idiomas_danny.git
```

### Paso 2 — Abrir en Android Studio

Abre Android Studio → **File → Open** → Selecciona la carpeta del proyecto.

### Paso 3 — Verificar `local.properties`

Asegúrate de que el archivo `local.properties` en la raíz contenga:

```properties
sdk.dir=C:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
API_BASE_URL=https://guaman-idiomas-ute.online/api/
```

### Paso 4 — Sincronizar Gradle

Android Studio sincronizará automáticamente. Si no lo hace, ve a **File → Sync Project with Gradle Files**.

### Paso 5 — Ejecutar

1. Conecta un dispositivo físico con **Depuración USB** habilitada, o inicia un emulador (API 26+).
2. Presiona el botón **Run ▶️** o usa `Shift + F10`.
3. Espera la compilación e instalación automática.

### Paso 6 — Probar cada rol

| Rol | Credenciales | Qué verificar |
|---|---|---|
| **Admin** | `alexander18br17@gmail.com` / `principe123` | Dashboard con métricas, CRUD usuarios y cursos |
| **Profesor** | `profe1@gmail.com` / `principe123` | Crear clases, agregar lecciones y recursos |
| **Estudiante** | `alex1234@gmail.com` / `principe123` | Home gamificado, catálogo, juegos, carrito |

---

## Arquitectura del Proyecto

```
┌──────────────────────────────────────────────────────┐
│                      UI Layer                        │
│  Screens (Compose) → ViewModels (StateFlow)          │
├──────────────────────────────────────────────────────┤
│                    Domain Layer                       │
│  Repository Interfaces → Domain Models               │
├──────────────────────────────────────────────────────┤
│                     Data Layer                        │
│  Repository Impl → Retrofit APIs → DTOs              │
├──────────────────────────────────────────────────────┤
│                    DI (Hilt)                          │
│  NetworkModule → RepositoryModule                    │
└──────────────────────────────────────────────────────┘
```

**Patrón:** MVVM + Repository Pattern + Clean Architecture

---

## Estructura del Proyecto

```
app/src/main/java/com/ute/guamanidiomas/
├── data/
│   ├── local/                  → TokenDataStore (JWT + preferencias)
│   ├── remote/
│   │   ├── api/                → 18 interfaces Retrofit
│   │   ├── dto/                → DTOs con serialización Gson
│   │   └── interceptors/       → AuthInterceptor (Bearer auto)
│   └── repository/             → 15 implementaciones
├── di/
│   ├── NetworkModule.kt        → OkHttp + Retrofit + API providers
│   └── RepositoryModule.kt     → Bindings Hilt
├── domain/
│   ├── model/                  → 13 modelos de dominio
│   └── repository/             → 15 interfaces
├── ui/
│   ├── admin/                  → Panel admin (8 secciones)
│   ├── auth/                   → Login + Register
│   ├── course/                 → LearningPath + Exercises
│   ├── games/                  → 6 juegos educativos
│   ├── home/                   → Home + Catalog + CourseDetail
│   ├── navigation/             → NavGraph + BottomNav
│   ├── profile/                → Profile + Premium
│   ├── settings/               → Ajustes + tema oscuro
│   ├── student/                → MyClasses + Certificates + Achievements
│   ├── teacher/                → Panel profesor (5 secciones)
│   └── viewmodel/              → ViewModels compartidos
└── util/
    └── JwtDecoder.kt           → Decodificación JWT
```

---

## Configuración de Compilación

| Propiedad | Valor |
|---|---|
| compileSdk | 35 |
| minSdk | 26 |
| targetSdk | 35 |
| applicationId | `com.ute.guamanidiomas` |
| versionName | 1.0 |
| JDK | 17 |
| Build System | Gradle Kotlin DSL |

---

## Información Académica

| Campo | Detalle |
|---|---|
| **Proyecto** | Plataforma de Aprendizaje de Idiomas — JumpUp |
| **Universidad** | Universidad Tecnológica Equinoccial (UTE) |
| **Facultad** | Ciencias de la Ingeniería e Industrias |
| **Carrera** | Software |
| **Estudiante** | Danny Guamán |
| **Backend** | Django REST Framework + PostgreSQL |
| **Frontend** | Kotlin + Jetpack Compose |
| **Despliegue API** | https://guaman-idiomas-ute.online/api/ |
