# 📱 JumpUp UTE — Frontend Android

**Plataforma de aprendizaje de inglés** desarrollada con Kotlin y Jetpack Compose para la Universidad Tecnológica Equinoccial (UTE).

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Kotlin | 2.0+ | Lenguaje principal |
| Jetpack Compose | BOM 2024+ | UI declarativa |
| Material 3 | Última estable | Diseño y componentes |
| Hilt | 2.51+ | Inyección de dependencias |
| Retrofit | 2.9+ | Consumo de APIs REST |
| OkHttp | 4.12+ | Cliente HTTP + interceptor JWT |
| Navigation Compose | 2.7+ | Navegación entre pantallas |
| DataStore Preferences | 1.0+ | Persistencia local de sesión |
| Coil | 2.5+ | Carga de imágenes |
| Coroutines + StateFlow | — | Programación reactiva |
| KSP | 2.0+ | Procesamiento de anotaciones Hilt |

---

## 🏗️ Arquitectura

```
┌──────────────────────────────────────────────────────┐
│                      UI Layer                         │
│  Screens (Compose) → ViewModels (StateFlow)          │
├──────────────────────────────────────────────────────┤
│                    Domain Layer                       │
│  Repository Interfaces → Models                      │
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

## 👥 Roles del Sistema

| Rol | Pantalla de inicio | Funcionalidades |
|---|---|---|
| **Admin** | Panel de administración con NavigationDrawer | Gestión de usuarios, cursos, órdenes, roles, auditoría, suscripciones |
| **Teacher** | Panel del profesor con BottomNav propio | Gestión de clases, estudiantes, exámenes, recursos |
| **Student** | Home con BottomNav (Inicio, Clases, Juegos, Perfil) | Aprendizaje, gamificación, certificados, suscripciones |

---

## 🌐 Base URL del Backend

```
https://guaman-idiomas-ute.online/api/
```

Configurado en `local.properties` → `API_BASE_URL`

---

## 📡 APIs Consumidas — Total: 18 interfaces Retrofit

### Resumen por módulo

| # | Interfaz API | Archivo | Endpoints | Usado por |
|---|---|---|---|---|
| 1 | `AuthApi` | `data/remote/api/AuthApi.kt` | 4 | Login, Register, Logout, Refresh Token |
| 2 | `CourseApi` | `data/remote/api/CourseApi.kt` | 6 | CRUD Cursos + Join Class |
| 3 | `ModuleApi` | `data/remote/api/ModuleApi.kt` | 6 | CRUD Módulos por curso |
| 4 | `LessonApi` | `data/remote/api/LessonApi.kt` | 5 | CRUD Lecciones |
| 5 | `ExerciseApi` | `data/remote/api/ExerciseApi.kt` | 6 | CRUD Ejercicios por módulo |
| 6 | `GamificationApi` | `data/remote/api/GamificationApi.kt` | 5 | Stats, Progress, Achievements |
| 7 | `HomeApi` | `data/remote/api/HomeApi.kt` | 3 | Stats del home, logros, progreso |
| 8 | `LanguageApi` | `data/remote/api/LanguageApi.kt` | 4 | CRUD Idiomas |
| 9 | `OrderApi` | `data/remote/api/OrderApi.kt` | 8 | Órdenes, ítems, confirmación, stats |
| 10 | `SubscriptionApi` | `data/remote/api/SubscriptionApi.kt` | 4 | Planes, mis suscripciones, pagos |
| 11 | `AdminConsoleApi` | `data/remote/api/AdminConsoleApi.kt` | 3 | Roles, Audit Logs |
| 12 | `AdminUsersApi` | `data/remote/api/AdminUsersApi.kt` | 3 | CRUD Usuarios (staff) |
| 13 | `TeacherClassroomApi` | `data/remote/api/TeacherClassroomApi.kt` | 7 | Stats profesor, Classrooms CRUD, Enrollments |
| 14 | `TeacherExamApi` | `data/remote/api/TeacherExamApi.kt` | 6 | CRUD Exámenes + Resultados |
| 15 | `TeacherResourceApi` | `data/remote/api/TeacherResourceApi.kt` | 5 | CRUD Recursos educativos |
| 16 | `ClassroomApi` | `data/remote/api/ClassroomApi.kt` | 4 | Mis clases (student), recursos, salir de clase |
| 17 | `CertificateApi` | `data/remote/api/CertificateApi.kt` | 5 | Crear, listar, emitir, revocar, verificar |
| 18 | `TutorApi` | `data/remote/api/TutorApi.kt` | 1 | IA Tutor (POST prompt) |

**Total de endpoints consumidos: 85+**

---

## 📡 Detalle de Endpoints por API

### 1. AuthApi — Autenticación
```
POST /api/auth/login/          → Login con JWT
POST /api/auth/register/       → Registro de usuarios
POST /api/auth/logout/         → Cierre de sesión
POST /api/auth/token/refresh/  → Renovar token de acceso
```

### 2. CourseApi — Cursos
```
GET    /api/courses/           → Listar cursos (paginado + filtros)
GET    /api/courses/{id}/      → Detalle de curso
POST   /api/courses/           → Crear curso
PUT    /api/courses/{id}/      → Editar curso
DELETE /api/courses/{id}/      → Eliminar curso
POST   /api/courses/join/      → Unirse a clase con código de acceso
```

### 3. ModuleApi — Módulos
```
GET    /api/modules/                      → Listar todos los módulos
GET    /api/courses/{courseId}/modules/    → Módulos de un curso
GET    /api/modules/{id}/                 → Detalle de módulo
POST   /api/modules/                      → Crear módulo
PUT    /api/modules/{id}/                 → Editar módulo
DELETE /api/modules/{id}/                 → Eliminar módulo
```

### 4. LessonApi — Lecciones
```
GET    /api/lessons/           → Listar lecciones (paginado)
GET    /api/lessons/{id}/      → Detalle de lección
POST   /api/lessons/           → Crear lección
PUT    /api/lessons/{id}/      → Editar lección
DELETE /api/lessons/{id}/      → Eliminar lección
```

### 5. ExerciseApi — Ejercicios
```
GET    /api/exercises/                      → Listar todos
GET    /api/modules/{moduleId}/exercises/   → Ejercicios por módulo
GET    /api/exercises/{id}/                 → Detalle
POST   /api/exercises/                      → Crear ejercicio
PUT    /api/exercises/{id}/                 → Editar
DELETE /api/exercises/{id}/                 → Eliminar
```

### 6. GamificationApi — Gamificación
```
GET  /api/stats/               → Estadísticas del usuario (XP, racha, módulos)
POST /api/progress/            → Registrar progreso (lección + puntaje)
GET  /api/progress/            → Historial de progreso (paginado)
GET  /api/achievements/        → Todos los logros disponibles
GET  /api/my-achievements/     → Logros desbloqueados del usuario
```

### 7. HomeApi — Dashboard del estudiante
```
GET /api/stats/                → Stats resumidas para el home
GET /api/my-achievements/      → Logros del usuario
GET /api/progress/             → Progreso reciente
```

### 8. LanguageApi — Idiomas
```
GET    /api/languages/         → Listar idiomas
GET    /api/languages/{id}/    → Detalle
POST   /api/languages/         → Crear
DELETE /api/languages/{id}/    → Eliminar
```

### 9. OrderApi — Órdenes de compra
```
GET   /api/orders/                    → Listar órdenes (paginado + filtro)
GET   /api/orders/{id}/               → Detalle de orden
POST  /api/orders/                    → Crear orden vacía
POST  /api/orders/{id}/add_item/      → Agregar item al carrito
POST  /api/orders/{id}/confirm/       → Confirmar orden
PATCH /api/orders/{id}/update_status/ → Cambiar estado (admin)
PATCH /api/orders/{id}/               → Patch estado alternativo
GET   /api/orders/stats/              → Estadísticas de ventas
```

### 10. SubscriptionApi — Suscripciones Premium
```
GET  /api/subscriptions/       → Planes disponibles
GET  /api/my-subscriptions/    → Suscripciones del usuario
POST /api/subscriptions/       → Suscribirse a un plan
GET  /api/payments/            → Historial de pagos (paginado)
```

### 11. AdminConsoleApi — Administración
```
GET  /api/roles/               → Listar roles
POST /api/roles/               → Crear rol con permisos
GET  /api/audit-logs/          → Bitácora del sistema
```

### 12. AdminUsersApi — Gestión de personal
```
GET   /api/users/              → Listar usuarios
POST  /api/users/              → Crear usuario con rol
PATCH /api/users/{id}/         → Actualizar usuario (activar/desactivar, cambiar rol)
```

### 13. TeacherClassroomApi — Clases del profesor
```
GET    /api/teacher/stats/             → Estadísticas del profesor
GET    /api/classrooms/                → Listar clases (paginado)
GET    /api/classrooms/{id}/           → Detalle de clase
POST   /api/classrooms/               → Crear clase
PUT    /api/classrooms/{id}/           → Editar clase
DELETE /api/classrooms/{id}/           → Eliminar clase
GET    /api/classrooms/{id}/enrollments/ → Estudiantes inscritos
```

### 14. TeacherExamApi — Exámenes
```
GET    /api/exams/                     → Listar exámenes (paginado)
GET    /api/exams/{id}/                → Detalle
POST   /api/exams/                     → Crear examen
PUT    /api/exams/{id}/                → Editar examen
DELETE /api/exams/{id}/                → Eliminar examen
GET    /api/exams/{id}/results/        → Resultados de estudiantes
```

### 15. TeacherResourceApi — Recursos educativos
```
GET    /api/teacher-resources/         → Listar recursos
GET    /api/teacher-resources/{id}/    → Detalle
POST   /api/teacher-resources/         → Crear recurso
PUT    /api/teacher-resources/{id}/    → Editar
DELETE /api/teacher-resources/{id}/    → Eliminar
```

### 16. ClassroomApi — Clases del estudiante
```
GET  /api/classrooms/mine/             → Mis clases inscritas
GET  /api/classrooms/{id}/             → Detalle de clase
GET  /api/resources/                   → Recursos de una clase
POST /api/classrooms/{id}/remove-student/ → Salir de una clase
```

### 17. CertificateApi — Certificados
```
POST  /api/certificates/               → Crear certificado
GET   /api/certificates/               → Mis certificados
PATCH /api/certificates/{id}/issue/    → Emitir certificado (teacher)
PATCH /api/certificates/{id}/revoke/   → Revocar certificado (teacher)
GET   /api/certificates/verify/{code}/ → Verificar código
```

### 18. TutorApi — IA Tutor
```
POST /api/tutor/ask/                   → Enviar prompt al tutor IA
```

---

## 🎮 Módulo de Juegos — 6 juegos interactivos

| Juego | Descripción | XP máximo | Dificultad |
|---|---|---|---|
| Word Match | Emparejar inglés–español | 90 | Fácil |
| Flashcards | Tarjetas de vocabulario con flip 3D | 160 | Fácil |
| Constructor | Ordenar palabras para formar oraciones | 100 | Medio |
| Vocab Quiz | Preguntas con temporizador de 10s | 150 | Medio |
| Ahorcado | Adivinar palabra letra por letra | 120 | Medio |
| Memory Cards | Encontrar pares en tablero | 120 | Difícil |

Todos los juegos guardan progreso en el backend via `POST /api/progress/`

---

## 📂 Estructura del Proyecto

```
app/src/main/java/com/ute/guamanidiomas/
├── data/
│   ├── local/                  → TokenDataStore (JWT persistence)
│   ├── remote/
│   │   ├── api/                → 18 interfaces Retrofit
│   │   ├── dto/                → DTOs con serialización Gson
│   │   └── interceptors/       → AuthInterceptor (JWT auto-inject)
│   └── repository/             → 15 implementaciones de repositorios
├── di/
│   ├── NetworkModule.kt        → OkHttp + Retrofit + providers de APIs
│   └── RepositoryModule.kt     → Bindings Hilt (interface → impl)
├── domain/
│   ├── model/                  → 13 modelos de dominio
│   │   └── teacher/            → Modelos del profesor (Classroom, Exam, etc)
│   └── repository/             → 15 interfaces de repositorios
├── navigation/
│   └── Screen.kt               → Sealed class con todas las rutas
├── ui/
│   ├── admin/                  → Panel administrador (8 secciones + NavigationDrawer)
│   │   ├── components/         → AdminDrawerContent, AdminStatCard, etc
│   │   └── sections/           → Overview, Users, Courses, Orders, Roles, Audit
│   ├── auth/                   → Login + Register
│   ├── certificate/            → Pantalla de certificado PDF
│   ├── components/             → CartBottomSheet, LoadingScreen, campos
│   ├── course/                 → LearningPath, Exercise, JoinClass
│   ├── games/                  → 6 juegos con ViewModels
│   ├── home/                   → HomeScreen + Catalog + CourseDetail
│   ├── navigation/             → NavGraph + BottomNavBar
│   ├── orders/                 → Pantalla de órdenes
│   ├── profile/                → ProfileScreen + PremiumScreen
│   ├── settings/               → Settings + EditProfile
│   ├── student/                → MyClasses, MyCertificates, Achievements, Leaderboard
│   ├── teacher/                → Panel profesor (5 secciones + BottomNav)
│   │   ├── components/         → TeacherTopBar, TeacherBottomNav, StatCard
│   │   └── screens/            → Home, Classes, Students, Exams, Resources
│   ├── theme/                  → Color.kt, Theme.kt, Type.kt
│   ├── tutor/                  → IA Tutor Screen
│   └── viewmodel/              → ViewModels compartidos (Auth, Home, Cart, etc)
└── util/
    └── JwtDecoder.kt           → Decodificación de claims del JWT
```

---

## 🔐 Autenticación

- JWT (JSON Web Token) con access + refresh token
- `AuthInterceptor` inyecta automáticamente el token en cada request
- El rol del usuario se lee directamente del JWT (`claims.role`)
- `TokenDataStore` persiste sesión con DataStore Preferences
- Navegación automática según rol al login

---

## 📊 Repositorios (15 implementaciones)

| Repositorio | Interfaz | Implementación |
|---|---|---|
| Auth | `AuthRepository` | `AuthRepositoryImpl` |
| Course | `CourseRepository` | `CourseRepositoryImpl` |
| Module | `ModuleRepository` | `ModuleRepositoryImpl` |
| Lesson | `LessonRepository` | `LessonRepositoryImpl` |
| Exercise | `ExerciseRepository` | `ExerciseRepositoryImpl` |
| Gamification | `GamificationRepository` | `GamificationRepositoryImpl` |
| Home | `HomeRepository` | `HomeRepositoryImpl` |
| Language | `LanguageRepository` | `LanguageRepositoryImpl` |
| Order | `OrderRepository` | `OrderRepositoryImpl` |
| Subscription | `SubscriptionRepository` | `SubscriptionRepositoryImpl` |
| AdminConsole | `AdminConsoleRepository` | `AdminConsoleRepositoryImpl` |
| AdminUsers | `AdminUsersRepository` | `AdminUsersRepositoryImpl` |
| Teacher | `TeacherRepository` | `TeacherRepositoryImpl` |
| StudentClassroom | `StudentClassroomRepository` | `StudentClassroomRepositoryImpl` |
| Certificate | `CertificateRepository` | `CertificateRepositoryImpl` |

---

## 📱 Pantallas (40+)

### Estudiante
- Home (stats reales, cursos, idiomas, accesos rápidos)
- Catálogo de cursos
- Detalle de curso
- Learning Path (ruta de aprendizaje tipo Duolingo)
- Ejercicios interactivos
- Mis Clases (consumo de `/classrooms/mine/`)
- Unirse a Clase (código de acceso)
- 6 Juegos interactivos
- Logros (achievements API)
- Ranking / Leaderboard (stats API)
- Mis Certificados (certificates API)
- Perfil con XP, racha y nivel
- Premium / Suscripciones
- Órdenes de compra
- IA Tutor
- Configuración

### Profesor
- Dashboard con stats reales
- Gestión de Clases (CRUD + código de acceso)
- Gestión de Estudiantes (XP, racha, módulos)
- Gestión de Exámenes (CRUD + resultados)
- Gestión de Recursos (PDF, Word, Video, YouTube, Audio)

### Administrador
- Dashboard General (métricas globales, ingresos, actividad)
- Gestión de Usuarios (crear, editar, activar/desactivar, cambiar rol)
- Gestión de Cursos (CRUD + estadísticas por nivel)
- Gestión de Órdenes (aprobar, filtrar por estado)
- Suscripciones y Pagos (ingresos totales, planes activos)
- Gamificación (logros, XP)
- Roles y Permisos (CRUD)
- Auditoría del Sistema (bitácora)

---

## ▶️ Cómo ejecutar

1. Clonar el repositorio
2. Abrir en Android Studio (Hedgehog o superior)
3. Crear archivo `local.properties` en la raíz (si no existe):
   ```properties
   API_BASE_URL=https://guaman-idiomas-ute.online/api/
   ```
4. Sync Gradle
5. Ejecutar en emulador o dispositivo (minSdk 26 / Android 8.0+)

---

## 📋 Configuración del proyecto

| Propiedad | Valor |
|---|---|
| `compileSdk` | 35 |
| `minSdk` | 26 |
| `targetSdk` | 35 |
| `applicationId` | `com.ute.guamanidiomas` |
| `versionName` | 1.0 |
| JDK | 17 |
| Kotlin | 2.0+ |

---

## 👨‍💻 Autor

**Danny Guamán** — Universidad Tecnológica Equinoccial (UTE)

Proyecto de Idiomas — Plataforma LMS de Aprendizaje de Inglés
