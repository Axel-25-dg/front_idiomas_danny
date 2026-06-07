package com.ute.guamanidiomas.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.teacher.components.*
import com.ute.guamanidiomas.ui.teacher.screens.*
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun TeacherDashboardScreen(
    onLogout: () -> Unit,
    // Las siguientes lambdas se mantienen por compatibilidad con NavGraph existente
    onNavigateToClasses: () -> Unit = {},
    onNavigateToStudents: () -> Unit = {},
    onManageCourse: (Int) -> Unit = {},
    viewModel: TeacherMainViewModel = hiltViewModel()
) {
    var currentSection by remember { mutableStateOf(TeacherSection.HOME) }

    val homeState      by viewModel.homeState.collectAsState()
    val classroomsState by viewModel.classroomsState.collectAsState()

    // Cargar datos de la sección activa al cambiar
    LaunchedEffect(currentSection) {
        when (currentSection) {
            TeacherSection.HOME      -> viewModel.loadHome()
            TeacherSection.CLASSES   -> viewModel.loadClassrooms()
            TeacherSection.STUDENTS  -> viewModel.loadStudents()
            TeacherSection.EXAMS     -> viewModel.loadExams()
            TeacherSection.RESOURCES -> viewModel.loadResources()
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TeacherTopBar(
                title    = sectionTitle(currentSection),
                subtitle = "UTE ACADEMY — PROFESOR",
                onLogout = onLogout
            )
        },
        bottomBar = {
            TeacherBottomNav(
                currentSection    = currentSection,
                onSectionSelected = { currentSection = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSection) {
                TeacherSection.HOME -> TeacherHomeSection(
                    viewModel      = viewModel,
                    onSectionClick = { currentSection = it }
                )
                TeacherSection.CLASSES   -> TeacherClassesSection(viewModel = viewModel)
                TeacherSection.STUDENTS  -> TeacherStudentsSection(viewModel = viewModel)
                TeacherSection.EXAMS     -> TeacherExamsSection(viewModel = viewModel)
                TeacherSection.RESOURCES -> TeacherResourcesSection(viewModel = viewModel)
            }
        }
    }
}

private fun sectionTitle(section: TeacherSection): String = when (section) {
    TeacherSection.HOME      -> "Panel del Profesor"
    TeacherSection.CLASSES   -> "Mis Clases"
    TeacherSection.STUDENTS  -> "Estudiantes"
    TeacherSection.EXAMS     -> "Lecciones Interactivas"
    TeacherSection.RESOURCES -> "Recursos"
}
