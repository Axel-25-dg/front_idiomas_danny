package com.ute.guamanidiomas.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.admin.components.*
import com.ute.guamanidiomas.ui.admin.sections.*
import com.ute.guamanidiomas.ui.theme.BackgroundColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    // Callbacks de compatibilidad con el NavGraph existente
    onNavigateToUsers: () -> Unit = {},
    onNavigateToCourses: () -> Unit = {},
    onNavigateToRoles: () -> Unit = {},
    onNavigateToAdminOrders: () -> Unit = {},
    onNavigateToAuditLogs: () -> Unit = {},
    viewModel: AdminMainViewModel = hiltViewModel()
) {
    var currentSection by remember { mutableStateOf(AdminSection.OVERVIEW) }
    val drawerState    = rememberDrawerState(DrawerValue.Closed)
    val scope          = rememberCoroutineScope()
    val overviewState  by viewModel.overviewState.collectAsState()

    // Cargar datos al cambiar sección
    LaunchedEffect(currentSection) {
        when (currentSection) {
            AdminSection.OVERVIEW       -> viewModel.loadOverview()
            AdminSection.USERS          -> viewModel.loadUsers()
            AdminSection.COURSES        -> viewModel.loadCourses()
            AdminSection.ORDERS         -> viewModel.loadOrders()
            AdminSection.SUBSCRIPTIONS  -> viewModel.loadSubscriptions()
            AdminSection.GAMIFICATION   -> { /* usa GamificationApi existente */ }
            AdminSection.ROLES          -> viewModel.loadRoles()
            AdminSection.AUDIT          -> viewModel.loadRoles()
        }
    }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            AdminDrawerContent(
                currentSection    = currentSection,
                adminName         = overviewState.adminName,
                adminEmail        = overviewState.adminEmail,
                onSectionSelected = { section ->
                    currentSection = section
                    scope.launch { drawerState.close() }
                },
                onLogout = onLogout
            )
        }
    ) {
        Scaffold(
            containerColor = BackgroundColor,
            topBar = {
                AdminTopBar(
                    title       = currentSection.label,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onRefresh   = {
                        when (currentSection) {
                            AdminSection.OVERVIEW      -> viewModel.loadOverview()
                            AdminSection.USERS         -> viewModel.loadUsers()
                            AdminSection.COURSES       -> viewModel.loadCourses()
                            AdminSection.ORDERS        -> viewModel.loadOrders()
                            AdminSection.SUBSCRIPTIONS -> viewModel.loadSubscriptions()
                            AdminSection.ROLES         -> viewModel.loadRoles()
                            AdminSection.AUDIT         -> viewModel.loadRoles()
                            else                       -> viewModel.loadOverview()
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentSection) {
                    AdminSection.OVERVIEW      -> AdminOverviewSection(viewModel = viewModel)
                    AdminSection.USERS         -> AdminUsersSection(viewModel = viewModel)
                    AdminSection.COURSES       -> AdminCoursesSection(viewModel = viewModel)
                    AdminSection.ORDERS        -> AdminOrdersSection(viewModel = viewModel)
                    AdminSection.SUBSCRIPTIONS -> AdminSubscriptionsSection(viewModel = viewModel)
                    AdminSection.GAMIFICATION  -> AdminGamificationSection()
                    AdminSection.ROLES         -> AdminRolesSection(viewModel = viewModel)
                    AdminSection.AUDIT         -> AdminAuditSection(viewModel = viewModel)
                }
            }
        }
    }
}
