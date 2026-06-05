package com.ute.guamanidiomas.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.ute.guamanidiomas.navigation.Screen
import com.ute.guamanidiomas.ui.auth.LoginScreen
import com.ute.guamanidiomas.ui.auth.RegisterScreen
import com.ute.guamanidiomas.ui.home.CatalogScreen
import com.ute.guamanidiomas.ui.home.CourseDetailScreen
import com.ute.guamanidiomas.ui.home.HomeScreen
import com.ute.guamanidiomas.ui.course.ExerciseScreen
import com.ute.guamanidiomas.ui.course.LearningPathScreen
import com.ute.guamanidiomas.ui.course.JoinClassScreen
import com.ute.guamanidiomas.ui.teacher.TeacherDashboardScreen
import com.ute.guamanidiomas.ui.games.GameCenterScreen
import com.ute.guamanidiomas.ui.games.WordMatchScreen
import com.ute.guamanidiomas.ui.games.FlashcardsScreen
import com.ute.guamanidiomas.ui.games.SentenceBuilderScreen
import com.ute.guamanidiomas.ui.games.VocabQuizScreen
import com.ute.guamanidiomas.ui.games.HangmanScreen
import com.ute.guamanidiomas.ui.games.MemoryCardsScreen
import com.ute.guamanidiomas.ui.orders.OrdersScreen
import com.ute.guamanidiomas.ui.admin.AdminDashboardScreen
import com.ute.guamanidiomas.ui.admin.CourseManagementScreen
import com.ute.guamanidiomas.ui.admin.UserManagementScreen
import com.ute.guamanidiomas.ui.admin.CourseFormScreen
import com.ute.guamanidiomas.ui.admin.ModuleManagementScreen
import com.ute.guamanidiomas.ui.admin.ModuleFormScreen
import com.ute.guamanidiomas.ui.admin.RolesManagementScreen
import com.ute.guamanidiomas.ui.admin.OrdersManagementScreen
import com.ute.guamanidiomas.ui.admin.AuditLogScreen
import com.ute.guamanidiomas.ui.tutor.IATutorScreen
import com.ute.guamanidiomas.ui.certificate.CertificateScreen
import com.ute.guamanidiomas.ui.profile.PremiumScreen
import com.ute.guamanidiomas.ui.profile.ProfileScreen
import com.ute.guamanidiomas.ui.settings.SettingsScreen
import com.ute.guamanidiomas.ui.settings.EditProfileScreen
import com.ute.guamanidiomas.ui.viewmodel.AuthViewModel
import com.ute.guamanidiomas.ui.viewmodel.CartViewModel
import com.ute.guamanidiomas.ui.components.LoadingScreen
import com.ute.guamanidiomas.ui.components.CartBottomSheet
import com.ute.guamanidiomas.ui.student.MyClassesScreen
import com.ute.guamanidiomas.ui.student.MyCertificatesScreen
import com.ute.guamanidiomas.ui.student.AchievementsScreen
import com.ute.guamanidiomas.ui.student.LeaderboardScreen
import com.ute.guamanidiomas.ui.theme.SurfaceColor

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()

    if (isCheckingSession) {
        LoadingScreen("Iniciando JumpUp UTE...")
        return
    }

    NavGraphContent(
        authViewModel = authViewModel,
        cartViewModel = cartViewModel,
    )
}

@Composable
private fun NavGraphContent(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
) {
    val navController     = rememberNavController()
    val isAuthenticated   by authViewModel.isAuthenticated.collectAsState()
    // Leer role directamente del StateFlow del ViewModel — única fuente de verdad
    val userRole          by authViewModel.userRole.collectAsState()
    val isTeacher         by authViewModel.isTeacherRole.collectAsState()
    val isAdmin           by authViewModel.isAdminRole.collectAsState()

    var showCart by remember { mutableStateOf(false) }
    var confirmedOrderId by remember { mutableStateOf<Int?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(confirmedOrderId) {
        confirmedOrderId?.let { id ->
            val result = snackbarHostState.showSnackbar(
                message = "¡Inscripción #$id realizada con éxito!",
                actionLabel = "Ver mis cursos",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                navController.navigate(Screen.Orders.route)
            }
            confirmedOrderId = null
        }
    }

    val startDestination = remember(isAuthenticated, userRole) {
        when {
            !isAuthenticated -> Screen.Login.route
            isTeacher        -> Screen.TeacherDashboard.route
            isAdmin          -> Screen.AdminDashboard.route
            else             -> Screen.Home.route
        }
    }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val showBottomBar = isAuthenticated && !isAdmin && !isTeacher && currentRoute in listOf(
        Screen.Home.route,
        Screen.MyClasses.route,
        Screen.GameCenter.route,
        Screen.Profile.route,
    )

    Scaffold(
        containerColor = SurfaceColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController
                )
            }
        },
    ) { innerPadding ->

        if (showCart) {
            CartBottomSheet(
                cartViewModel   = cartViewModel,
                isAuthenticated = isAuthenticated,
                onDismiss       = { showCart = false },
                onLoginRequired = {
                    showCart = false
                    navController.navigate(Screen.Login.route)
                },
                onOrderSuccess = { orderId ->
                    confirmedOrderId = orderId
                    showCart = false
                },
            )
        }

        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { role, isStaff ->
                        val r = role.lowercase().trim()
                        Log.d("ROLE_DEBUG", "onLoginSuccess - role: $r, isStaff: $isStaff")
                        val dest = when (r) {
                            "teacher", "profesor", "docente", "instructor" -> Screen.TeacherDashboard.route
                            "admin", "superuser", "staff", "administrador" -> Screen.AdminDashboard.route
                            else -> Screen.Home.route
                        }
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    viewModel            = authViewModel,
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { role, isStaff ->
                        val r = role.lowercase().trim()
                        Log.d("ROLE_DEBUG", "onRegisterSuccess - role: $r, isStaff: $isStaff")
                        val dest = when (r) {
                            "teacher", "profesor", "docente", "instructor" -> Screen.TeacherDashboard.route
                            "admin", "superuser", "staff", "administrador" -> Screen.AdminDashboard.route
                            else -> Screen.Home.route
                        }
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                    viewModel         = authViewModel,
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onLogout       = { authViewModel.logout() },
                    onCourseClick  = { id -> navController.navigate("course/$id") },
                    onCatalogClick = {
                        navController.navigate(Screen.Catalog.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onJoinClassClick  = { navController.navigate(Screen.JoinClass.route) },
                    onIATutorClick    = { navController.navigate(Screen.IATutor.route) },
                    onOpenCart        = { showCart = true },
                    onMyClassesClick  = { navController.navigate(Screen.MyClasses.route) },
                    onAchievementsClick = { navController.navigate(Screen.Achievements.route) },
                    onLeaderboardClick = { navController.navigate(Screen.Leaderboard.route) },
                    onCertificatesClick = { navController.navigate(Screen.MyCertificates.route) },
                    cartViewModel     = cartViewModel
                )
            }

            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onCourseClick = { id -> navController.navigate("course/$id") },
                    onOpenCart = { showCart = true },
                    cartViewModel = cartViewModel
                )
            }

            composable(
                route     = "course/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                CourseDetailScreen(
                    courseId      = id,
                    onBack        = { navController.popBackStack() },
                    onOpenCart    = { showCart = true },
                    cartViewModel = cartViewModel,
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = { authViewModel.logout() },
                    onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                    onNavigateToCertificates = { navController.navigate(Screen.MyCertificates.route) },
                    onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = { authViewModel.logout() },
                    onNavigateToEditProfile = { navController.navigate("edit_profile") }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Premium.route) {
                PremiumScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.LearningPath.route,
                arguments = listOf(navArgument("courseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
                LearningPathScreen(
                    courseId = courseId,
                    courseTitle = "Curso",
                    onBack = { navController.popBackStack() },
                    onExerciseClick = { exId ->
                        navController.navigate(Screen.Exercise.createRoute(exId))
                    }
                )
            }

            composable(
                route = Screen.Exercise.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val exId = backStackEntry.arguments?.getInt("exerciseId") ?: 0
                ExerciseScreen(
                    exerciseId = exId,
                    onClose = { navController.popBackStack() },
                    onComplete = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Orders.route) {
                OrdersScreen()
            }

            composable(Screen.GameCenter.route) {
                GameCenterScreen(
                    onNavigateToGame = { gameId ->
                        when (gameId) {
                            "word_match"       -> navController.navigate(Screen.WordMatch.route)
                            "flashcards"       -> navController.navigate(Screen.Flashcards.route)
                            "sentence_builder" -> navController.navigate(Screen.SentenceBuilder.route)
                            "vocab_quiz"       -> navController.navigate(Screen.VocabQuiz.route)
                            "hangman"          -> navController.navigate(Screen.Hangman.route)
                            "memory_cards"     -> navController.navigate(Screen.MemoryCards.route)
                        }
                    }
                )
            }

            composable(Screen.WordMatch.route) {
                WordMatchScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Flashcards.route) {
                FlashcardsScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.SentenceBuilder.route) {
                SentenceBuilderScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.VocabQuiz.route) {
                VocabQuizScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Hangman.route) {
                HangmanScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.MemoryCards.route) {
                MemoryCardsScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.JoinClass.route) {
                JoinClassScreen(
                    onBack = { navController.popBackStack() },
                    onJoinSuccess = { courseId ->
                        navController.navigate(Screen.LearningPath.createRoute(courseId)) {
                            popUpTo(Screen.JoinClass.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.IATutor.route) {
                IATutorScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Certificate.route,
                arguments = listOf(navArgument("courseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
                CertificateScreen(
                    courseId = courseId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TeacherDashboard.route) {
                TeacherDashboardScreen(
                    onLogout = { authViewModel.logout() }
                )
            }

            composable(Screen.MyClasses.route) {
                MyClassesScreen(
                    onBack       = { navController.popBackStack() },
                    onClassDetail = { id -> navController.navigate(Screen.MyClassDetail.createRoute(id)) },
                    onJoinClass  = { navController.navigate(Screen.JoinClass.route) }
                )
            }

            composable(
                route = Screen.MyClassDetail.route,
                arguments = listOf(navArgument("classroomId") { type = NavType.IntType })
            ) { backStackEntry ->
                val classroomId = backStackEntry.arguments?.getInt("classroomId") ?: 0
                // Reutiliza la misma pantalla de detalle que podría implementarse
                // Por ahora redirige a recursos de clase
                MyClassesScreen(
                    onBack       = { navController.popBackStack() },
                    onClassDetail = {},
                    onJoinClass  = { navController.navigate(Screen.JoinClass.route) }
                )
            }

            composable(Screen.MyCertificates.route) {
                MyCertificatesScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Achievements.route) {
                AchievementsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    onLogout = { authViewModel.logout() },
                    onNavigateToUsers = { navController.navigate(Screen.UserManagement.route) },
                    onNavigateToCourses = { navController.navigate(Screen.CourseManagement.route) },
                    onNavigateToRoles = { navController.navigate(Screen.RolesManagement.route) },
                    onNavigateToAdminOrders = { navController.navigate(Screen.AdminOrders.route) },
                    onNavigateToAuditLogs = { navController.navigate(Screen.AuditLogs.route) }
                )
            }

            composable(Screen.UserManagement.route) {
                UserManagementScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CourseManagement.route) {
                CourseManagementScreen(
                    onBack = { navController.popBackStack() },
                    onEditCourse = { id -> navController.navigate(Screen.CourseEdit.createRoute(id)) },
                    onCreateCourse = { navController.navigate(Screen.CourseCreate.route) },
                    onManageModules = { id -> navController.navigate(Screen.ModuleManagement.createRoute(id)) }
                )
            }

            composable(Screen.CourseCreate.route) {
                CourseFormScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.CourseEdit.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                CourseFormScreen(
                    courseId = id,
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ModuleManagement.route,
                arguments = listOf(navArgument("courseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
                ModuleManagementScreen(
                    courseId = courseId,
                    onBack = { navController.popBackStack() },
                    onEditModule = { modId ->
                        navController.navigate(Screen.ModuleEdit.createRoute(courseId, modId))
                    },
                    onCreateModule = {
                        navController.navigate(Screen.ModuleCreate.createRoute(courseId))
                    }
                )
            }

            composable(
                route = Screen.ModuleCreate.route,
                arguments = listOf(navArgument("courseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
                ModuleFormScreen(
                    courseId = courseId,
                    viewModel = hiltViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ModuleEdit.route,
                arguments = listOf(
                    navArgument("courseId") { type = NavType.IntType },
                    navArgument("moduleId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
                val moduleId = backStackEntry.arguments?.getInt("moduleId") ?: 0
                ModuleFormScreen(
                    courseId = courseId,
                    moduleId = moduleId,
                    viewModel = hiltViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.RolesManagement.route) {
                RolesManagementScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminOrders.route) {
                OrdersManagementScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AuditLogs.route) {
                AuditLogScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
