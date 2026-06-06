# JumpUp UTE - Aplicacion Movil de Aprendizaje de Ingles

## Universidad Tecnologica Equinoccial (UTE)
### Facultad de Ciencias de la Ingenieria e Industrias
### Carrera de Software

**Materia:** Desarrollo Movil  
**Estudiante:** Danny Guaman  
**Periodo:** 2024-2025

---

## Descripcion del Proyecto

Aplicacion movil Android desarrollada en Kotlin con Jetpack Compose que consume una API REST construida con Django REST Framework y PostgreSQL. La plataforma permite el aprendizaje de ingles con tres roles diferenciados: administrador, profesor y estudiante.

El sistema implementa autenticacion JWT, operaciones CRUD completas sobre 7 entidades, busqueda con paginacion, manejo de errores HTTP y diferenciacion de permisos por rol.

---

## Stack Tecnologico

| Tecnologia | Version | Uso |
|---|---|---|
| Kotlin | 2.0+ | Lenguaje principal |
| Jetpack Compose | BOM 2024+ | UI declarativa |
| Material 3 | Ultima estable | Diseno y componentes |
| Hilt | 2.51+ | Inyeccion de dependencias |
| Retrofit | 2.9+ | Consumo de APIs REST |
| OkHttp | 4.12+ | Cliente HTTP con interceptor JWT |
| Navigation Compose | 2.7+ | Navegacion entre pantallas |
| DataStore Preferences | 1.0+ | Persistencia local de sesion |
| Coil | 2.5+ | Carga de imagenes |
| Coroutines + StateFlow | - | Programacion reactiva |
| KSP | 2.0+ | Procesamiento de anotaciones Hilt |

---

## Arquitectura

```
+------------------------------------------------------+
|                      UI Layer                         |
|  Screens (Compose) -> ViewModels (StateFlow)         |
+------------------------------------------------------+
|                    Domain Layer                       |
|  Repository Interfaces -> Models                     |
+------------------------------------------------------+
|                     Data Layer                        |
|  Repository Impl -> Retrofit APIs -> DTOs            |
+------------------------------------------------------+
|                    DI (Hilt)                          |
|  NetworkModule -> RepositoryModule                   |
+------------------------------------------------------+
```

Patron: MVVM + Repository Pattern + Clean Architecture

---

## Credenciales de Prueba

| Rol | Correo | Contrasena | Funcionalidades |
|---|---|---|---|
| Admin | alexander18br17@gmail.com | principe123 | Panel completo de administracion |
| Teacher | profe1@gmail.com | principe123 | Panel del profesor (clases, examenes, recursos) |
| Student | alex1234@gmail.com | principe123 | Experiencia de aprendizaje completa |

---

## Configuracion de la URL Base

```
https://guaman-idiomas-ute.online/api/
```

Configurado en `local.properties`:
```properties
API_BASE_URL=https://guaman-idiomas-ute.online/api/
```

---

## Entidades Implementadas con CRUD Completo (7 entidades)

| N | Entidad | Listar | Detalle | Crear | Actualizar | Eliminar | Archivo API | Pantalla |
|---|---|---|---|---|---|---|---|---|
| 1 | Courses | Si | Si | Si | Si | Si | CourseApi.kt | Admin - Cursos |
| 2 | Modules | Si | Si | Si | Si | Si | ModuleApi.kt | Admin - Modulos |
| 3 | Classrooms | Si | Si | Si | Si | Si | TeacherClassroomApi.kt | Teacher - Clases |
| 4 | Users | Si | Si | Si | Si | Si | AdminUsersApi.kt | Admin - Usuarios |
| 5 | Orders | Si | Si | Si | Si | - | OrderApi.kt | Admin - Ordenes |
| 6 | Resources | Si | Si | Si | Si | Si | TeacherResourceApi.kt | Teacher - Recursos |
| 7 | Subscriptions | Si | Si | Si | - | - | SubscriptionApi.kt | Student - Premium |

---

## APIs Consumidas - Total: 18 interfaces Retrofit (85+ endpoints)

| N | Interfaz | Endpoints | Uso |
|---|---|---|---|
| 1 | AuthApi | 4 | Login, Register, Logout, Refresh Token |
| 2 | CourseApi | 6 | CRUD Cursos + Join Class |
| 3 | ModuleApi | 6 | CRUD Modulos por curso |
| 4 | LessonApi | 5 | CRUD Lecciones |
| 5 | ExerciseApi | 6 | CRUD Ejercicios por modulo |
| 6 | GamificationApi | 5 | Stats, Progress, Achievements |
| 7 | HomeApi | 3 | Stats del home, logros, progreso |
| 8 | LanguageApi | 4 | CRUD Idiomas |
| 9 | OrderApi | 8 | Ordenes, items, confirmacion, stats |
| 10 | SubscriptionApi | 4 | Planes, suscripciones, pagos |
| 11 | AdminConsoleApi | 3 | Roles, Audit Logs |
| 12 | AdminUsersApi | 3 | CRUD Usuarios |
| 13 | TeacherClassroomApi | 8 | Classrooms CRUD, Enrollments, Stats |
| 14 | TeacherExamApi | 6 | CRUD Examenes + Resultados |
| 15 | TeacherResourceApi | 5 | CRUD Recursos educativos |
| 16 | ClassroomApi | 4 | Mis clases (student), recursos |
| 17 | CertificateApi | 5 | Crear, listar, emitir, verificar |
| 18 | TutorApi | 1 | IA Tutor |

---

## Filtros, Busqueda y Paginacion

| Funcionalidad | Implementacion | Archivo |
|---|---|---|
| Busqueda con ?search= | Catalogo de cursos con debounce 400ms | CatalogViewModel.kt |
| Paginacion ?page= y ?page_size= | Cursos, ordenes, clases, progreso | Todos los repositorios |
| Scroll infinito | Carga siguiente pagina al llegar al final | CatalogViewModel.loadNextPage() |
| Estado vacio | Composable reutilizable EmptyState | Todas las pantallas |
| Indicador de carga | CircularProgressIndicator | Todas las pantallas |
| Filtros por rol/estado | Chips de filtro en Users y Orders | AdminUsersSection.kt |

Ejemplo de consumo con busqueda:
```kotlin
courseRepository.getCourses(CourseFilters(search = "A1", page = 1, pageSize = 12))
// Genera: GET /api/courses/?search=A1&page=1&page_size=12
```

---

## Autenticacion y Seguridad

### Flujo completo:

```
1. Usuario ingresa email + contrasena en LoginScreen
2. POST /api/auth/login/ -> recibe { access, refresh }
3. JWT se decodifica -> se extrae el campo "role"
4. Token se guarda en DataStore (persistencia segura)
5. AuthInterceptor inyecta "Authorization: Bearer <token>" en CADA request
6. NavGraph navega automaticamente segun el rol:
   - role = "admin"   -> AdminDashboard
   - role = "teacher" -> TeacherDashboard
   - role = "student" -> Home
7. Al cerrar sesion: POST /api/auth/logout/ + se limpia DataStore
```

### Archivos clave:
- `data/remote/interceptors/AuthInterceptor.kt` - Inyecta token automaticamente
- `data/local/TokenDataStore.kt` - Guarda tokens y datos del usuario
- `util/JwtDecoder.kt` - Decodifica claims del JWT (role, user_id)
- `ui/viewmodel/AuthViewModel.kt` - Controla login/logout/restore session
- `ui/navigation/NavGraph.kt` - Proteccion de rutas por rol

### Ejemplo de peticion autenticada:
```
GET /api/classrooms/
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  Content-Type: application/json
```

### Diferenciacion de permisos:

| Rol | Consultar | Crear | Editar | Eliminar |
|---|---|---|---|---|
| Student | Si (cursos, clases, progreso) | Si (unirse a clases) | No | No |
| Teacher | Si (todo lo suyo) | Si (clases, examenes, recursos) | Si (sus clases) | Si (sus clases) |
| Admin | Si (todo) | Si (todo) | Si (todo) | Si (todo) |

---

## Manejo de Errores

| Codigo HTTP | Mensaje mostrado | Donde se maneja |
|---|---|---|
| 400 | Datos invalidos + detalle del backend | apiError() en repositorios |
| 401 | Credenciales invalidas / redireccion a login | AuthInterceptor + AuthViewModel |
| 403 | No tienes permisos | Banner de error en pantalla |
| 404 | No encontrado + fallback | OrderRepositoryImpl |
| 500 | Error del servidor | Todos los runCatching |
| Sin internet | Error de conexion | IOException capturada |
| Token vencido | Refresh automatico o redireccion a login | AuthInterceptor |

Ejemplo en codigo:
```kotlin
override suspend fun getClassrooms(): Result<List<Classroom>> = runCatching {
    val response = api.getClassrooms()
    if (response.isSuccessful) {
        response.body()?.results?.map { it.toDomain() } ?: emptyList()
    } else {
        throw Exception("Error ${response.code()}: ${response.errorBody()?.string()}")
    }
}
```

---

## Capturas de Pantalla

Las capturas de pantalla se encuentran en la carpeta `/capturas` del proyecto.

---

## Listado de Pantallas (40+)

### Autenticacion
| Pantalla | Archivo | Descripcion |
|---|---|---|
| Login | ui/auth/LoginScreen.kt | Email + contrasena con JWT |
| Registro | ui/auth/RegisterScreen.kt | Crear cuenta nueva |

### Panel Estudiante
| Pantalla | Archivo | Descripcion |
|---|---|---|
| Home | ui/home/HomeScreen.kt | XP, racha, cursos, accesos rapidos |
| Catalogo | ui/home/CatalogScreen.kt | Busqueda + paginacion de cursos |
| Detalle Curso | ui/home/CourseDetailScreen.kt | Info del curso + inscripcion |
| Ruta de Aprendizaje | ui/course/LearningPathScreen.kt | Modulos + ejercicios tipo Duolingo |
| Ejercicios | ui/course/ExerciseScreen.kt | Multiple choice, traduccion, listening |
| Unirse a Clase | ui/course/JoinClassScreen.kt | Codigo de acceso |
| Mis Clases | ui/student/MyClassesScreen.kt | Clases inscritas |
| Mis Certificados | ui/student/MyCertificatesScreen.kt | Certificados obtenidos |
| Logros | ui/student/AchievementsScreen.kt | Achievements desbloqueados |
| Ranking | ui/student/LeaderboardScreen.kt | Top estudiantes por XP |
| Centro de Juegos | ui/games/GameCenterScreen.kt | 6 juegos interactivos |
| Word Match | ui/games/WordMatchScreen.kt | Emparejar palabras |
| Flashcards | ui/games/FlashcardsScreen.kt | Tarjetas de vocabulario |
| Constructor | ui/games/SentenceBuilderScreen.kt | Ordenar oraciones |
| Vocab Quiz | ui/games/VocabQuizScreen.kt | Quiz con temporizador |
| Ahorcado | ui/games/HangmanScreen.kt | Adivinar palabra |
| Memory Cards | ui/games/MemoryCardsScreen.kt | Encontrar pares |
| Perfil | ui/profile/ProfileScreen.kt | Datos + stats + logros |
| Premium | ui/profile/PremiumScreen.kt | Suscripciones + pagos |
| Ajustes | ui/settings/SettingsScreen.kt | Tema oscuro, notificaciones |
| Ordenes | ui/orders/OrdersScreen.kt | Historial de compras |

### Panel Profesor
| Pantalla | Archivo | Descripcion |
|---|---|---|
| Dashboard | ui/teacher/TeacherDashboardScreen.kt | Stats + navegacion interna |
| Mis Clases | ui/teacher/screens/TeacherClassesSection.kt | CRUD clases + codigo acceso |
| Estudiantes | ui/teacher/screens/TeacherStudentsSection.kt | Lista por clase + XP |
| Examenes | ui/teacher/screens/TeacherExamsSection.kt | CRUD + resultados |
| Recursos | ui/teacher/screens/TeacherResourcesSection.kt | PDF, Video, Audio, Links |

### Panel Administrador
| Pantalla | Archivo | Descripcion |
|---|---|---|
| Dashboard | ui/admin/AdminDashboardScreen.kt | NavigationDrawer + metricas |
| Usuarios | ui/admin/sections/AdminUsersSection.kt | CRUD + cambio de rol |
| Cursos | ui/admin/sections/AdminCoursesSection.kt | Gestion academica |
| Ordenes | ui/admin/sections/AdminOrdersSection.kt | Aprobar/filtrar |
| Suscripciones | ui/admin/sections/AdminRemainingSection.kt | Ingresos |
| Roles | ui/admin/sections/AdminRemainingSection.kt | CRUD permisos |
| Auditoria | ui/admin/sections/AdminRemainingSection.kt | Bitacora del sistema |
| Gestion Cursos | ui/admin/CourseManagementScreen.kt | CRUD con modulos |
| Form Curso | ui/admin/CourseFormScreen.kt | Crear/editar curso |
| Gestion Modulos | ui/admin/ModuleManagementScreen.kt | Lista + CRUD |
| Form Modulo | ui/admin/ModuleFormScreen.kt | Crear/editar modulo |

---

## Estructura del Proyecto

```
app/src/main/java/com/ute/guamanidiomas/
|-- data/
|   |-- local/                  -> TokenDataStore (JWT + preferencias)
|   |-- remote/
|   |   |-- api/                -> 18 interfaces Retrofit
|   |   |-- dto/                -> DTOs con serializacion Gson
|   |   +-- interceptors/       -> AuthInterceptor (Bearer auto)
|   +-- repository/             -> 15 implementaciones
|-- di/
|   |-- NetworkModule.kt        -> OkHttp + Retrofit + API providers
|   +-- RepositoryModule.kt     -> Bindings Hilt
|-- domain/
|   |-- model/                  -> 13 modelos de dominio
|   +-- repository/             -> 15 interfaces
|-- navigation/
|   +-- Screen.kt               -> Sealed class con rutas
|-- ui/
|   |-- admin/                  -> Panel admin (8 secciones)
|   |-- auth/                   -> Login + Register
|   |-- course/                 -> LearningPath + Exercises + JoinClass
|   |-- games/                  -> 6 juegos
|   |-- home/                   -> Home + Catalog + CourseDetail
|   |-- navigation/             -> NavGraph + BottomNav
|   |-- profile/                -> Profile + Premium
|   |-- settings/               -> Ajustes + tema oscuro
|   |-- student/                -> MyClasses + Certificates + Achievements
|   |-- teacher/                -> Panel profesor (5 secciones)
|   +-- viewmodel/              -> ViewModels compartidos
+-- util/
    +-- JwtDecoder.kt           -> Decodificacion JWT
```

---

## Como Ejecutar la Aplicacion

### Requisitos:
- Android Studio Hedgehog (2023.1) o superior
- JDK 17
- Dispositivo o emulador Android con API 26+ (Android 8.0)

### Pasos:
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Axel-25-dg/front_idiomas_danny.git
   ```
2. Abrir en Android Studio
3. Verificar que existe `local.properties` con:
   ```properties
   API_BASE_URL=https://guaman-idiomas-ute.online/api/
   ```
4. Sync Gradle (automatico al abrir)
5. Ejecutar en emulador o dispositivo fisico
6. Usar las credenciales de prueba para ingresar

### Probar cada rol:
- Admin: Login con alexander18br17@gmail.com -> Panel con NavigationDrawer
- Teacher: Login con profe1@gmail.com -> Panel con BottomNav propio
- Student: Login con alex1234@gmail.com -> Home con juegos y clases

---

## Configuracion del Proyecto

| Propiedad | Valor |
|---|---|
| compileSdk | 35 |
| minSdk | 26 |
| targetSdk | 35 |
| applicationId | com.ute.guamanidiomas |
| versionName | 1.0 |
| JDK | 17 |

---

## Informacion Academica

**Proyecto:** Plataforma de Aprendizaje de Ingles - JumpUp UTE  
**Universidad:** Universidad Tecnologica Equinoccial (UTE)  
**Facultad:** Ciencias de la Ingenieria e Industrias  
**Carrera:** Software  
**Estudiante:** Danny Guaman  
**Backend:** Django REST Framework + PostgreSQL  
**Frontend:** Kotlin + Jetpack Compose  
**Despliegue API:** https://guaman-idiomas-ute.online/api/
