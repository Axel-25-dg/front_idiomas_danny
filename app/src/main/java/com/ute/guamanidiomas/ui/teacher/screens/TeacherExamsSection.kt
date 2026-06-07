package com.ute.guamanidiomas.ui.teacher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.domain.model.teacher.Exam
import com.ute.guamanidiomas.ui.teacher.TeacherMainViewModel
import com.ute.guamanidiomas.ui.teacher.components.*
import com.ute.guamanidiomas.ui.theme.*

/**
 * Lecciones Interactivas = Lessons con content_type = "interactive"
 * Se crean con POST /api/lessons/ y las preguntas con POST /api/exercises/
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherExamsSection(viewModel: TeacherMainViewModel) {
    val state by viewModel.examsState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showAddQuestion by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) viewModel.clearExamsMessages()
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        if (state.exams.isEmpty() && !state.isLoading) {
            if (state.error != null) {
                Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(state.error!!, color = ErrorColor, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadExams() }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) { Text("Reintentar") }
                }
            } else {
                EmptyState(
                    icon     = Icons.Default.Quiz,
                    title    = "Sin lecciones interactivas",
                    subtitle = "Crea lecciones con ejercicios para evaluar a tus estudiantes",
                    action   = "Crear leccion",
                    onAction = { showCreateDialog = true }
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.exams, key = { it.id }) { lesson ->
                    InteractiveLessonCard(
                        lesson       = lesson,
                        onAddQuestion = { showAddQuestion = lesson.id }
                    )
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }

        // FAB
        FloatingActionButton(
            onClick        = { showCreateDialog = true },
            modifier       = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = PrimaryBlue,
            contentColor   = Color.White
        ) { Icon(Icons.Default.Add, "Crear leccion interactiva") }
    }

    // Dialog crear leccion interactiva
    if (showCreateDialog) {
        CreateInteractiveLessonDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false },
            onCreate  = { moduleId, title, description, xp ->
                viewModel.createExam(moduleId, title, description, 60, xp, true, null, null)
                showCreateDialog = false
            }
        )
    }

    // Dialog agregar pregunta
    showAddQuestion?.let { lessonId ->
        AddQuestionDialog(
            onDismiss = { showAddQuestion = null },
            onAdd     = { question, type, answer ->
                viewModel.addQuestionToExam(lessonId, question, type, answer)
                showAddQuestion = null
            }
        )
    }
}

@Composable
private fun InteractiveLessonCard(lesson: Exam, onAddQuestion: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceColor
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Quiz, null, tint = PrimaryBlue, modifier = Modifier.size(24.dp)) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(lesson.title ?: "Lección sin título", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text((lesson.classroomName ?: "").ifBlank { "Leccion interactiva" }, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                if (lesson.isActive) {
                    Surface(color = Success.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)) {
                        Text("Activa", Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Success)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick  = onAddQuestion,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.1f), contentColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Agregar pregunta", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateInteractiveLessonDialog(viewModel: TeacherMainViewModel, onDismiss: () -> Unit, onCreate: (Int, String, String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var xp by remember { mutableStateOf("50") }
    var selectedModuleId by remember { mutableIntStateOf(0) }
    var selectedModuleTitle by remember { mutableStateOf("") }

    // Estado para los selectores dinámicos de curso y módulo
    val examsState by viewModel.examsState.collectAsState()
    val courses = examsState.courses
    val modules = examsState.modules

    var selectedCourseName by remember { mutableStateOf("Selecciona un Curso") }
    var selectedModuleName by remember { mutableStateOf("Selecciona un Módulo") }
    var expandedCourse by remember { mutableStateOf(false) }
    var expandedModule by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Leccion Interactiva", fontWeight = FontWeight.Bold) },
        text = {
            val dialogScrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp).verticalScroll(dialogScrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titulo *") },
                    leadingIcon = { Icon(Icons.Default.Title, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripcion") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector desplegable de Curso
                ExposedDropdownMenuBox(
                    expanded = expandedCourse,
                    onExpandedChange = { expandedCourse = !expandedCourse }
                ) {
                    OutlinedTextField(
                        value = selectedCourseName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCourse) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCourse,
                        onDismissRequest = { expandedCourse = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.title ?: "Curso #${course.id}") },
                                onClick = {
                                    selectedCourseName = course.title ?: "Curso #${course.id}"
                                    expandedCourse = false
                                    // Limpiar módulo anterior y cargar módulos del curso seleccionado
                                    selectedModuleName = "Selecciona un Módulo"
                                    selectedModuleId = 0
                                    viewModel.loadModulesForCourse(course.id)
                                }
                            )
                        }
                    }
                }

                // Selector desplegable de Módulo (habilitado solo si hay módulos cargados)
                ExposedDropdownMenuBox(
                    expanded = expandedModule,
                    onExpandedChange = { if (modules.isNotEmpty()) expandedModule = !expandedModule }
                ) {
                    OutlinedTextField(
                        value = selectedModuleName,
                        onValueChange = {},
                        readOnly = true,
                        enabled = modules.isNotEmpty(),
                        label = { Text("Módulo *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedModule) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedModule,
                        onDismissRequest = { expandedModule = false }
                    ) {
                        modules.forEach { module ->
                            DropdownMenuItem(
                                text = { Text(module.title ?: "Módulo sin título") },
                                onClick = {
                                    selectedModuleName = module.title ?: "Módulo sin título"
                                    selectedModuleId = module.id
                                    expandedModule = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = xp,
                    onValueChange = { if (it.all { c -> c.isDigit() }) xp = it },
                    label = { Text("XP Recompensa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank() && selectedModuleId > 0) onCreate(selectedModuleId, title.trim(), description.trim(), xp.toIntOrNull() ?: 50) },
                enabled = title.isNotBlank() && selectedModuleId > 0,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddQuestionDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("multiple_choice") }
    var expandedType by remember { mutableStateOf(false) }
    val types = listOf("multiple_choice" to "Opcion multiple", "translate" to "Traduccion", "fill_blank" to "Completar", "listen" to "Escuchar")
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Pregunta", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Pregunta") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Respuesta correcta") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = it }) {
                    OutlinedTextField(
                        value = types.find { it.first == type }?.second ?: type,
                        onValueChange = {}, readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedType) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        types.forEach { (key, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = { type = key; expandedType = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (question.isNotBlank() && answer.isNotBlank()) onAdd(question.trim(), type, answer.trim()) },
                enabled = question.isNotBlank() && answer.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
