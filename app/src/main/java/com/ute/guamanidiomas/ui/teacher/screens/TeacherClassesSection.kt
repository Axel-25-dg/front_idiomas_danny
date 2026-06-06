package com.ute.guamanidiomas.ui.teacher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.domain.model.teacher.Classroom
import com.ute.guamanidiomas.ui.teacher.TeacherMainViewModel
import com.ute.guamanidiomas.ui.teacher.components.*
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherClassesSection(viewModel: TeacherMainViewModel) {
    val state by viewModel.classroomsState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var deletingId by remember { mutableStateOf<Int?>(null) }

    // Snackbar messages
    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) viewModel.clearClassroomsMessages()
    }

    if (state.isLoading && state.classrooms.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        if (state.classrooms.isEmpty() && !state.isLoading) {
            if (state.error != null) {
                // Mostrar error con botón reintentar
                Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(state.error!!, color = ErrorColor, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadClassrooms() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text("Reintentar") }
                }
            } else {
                EmptyState(
                    icon     = Icons.Default.Class,
                    title    = "Sin clases",
                    subtitle = "Crea tu primera clase y comparte el código con tus estudiantes",
                    action   = "Crear clase",
                    onAction = { viewModel.showCreateClassroomDialog() }
                )
            }
        } else {
            LazyColumn(
                modifier        = Modifier.fillMaxSize(),
                contentPadding  = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                state.error?.let { err ->
                    item {
                        ErrorBanner(message = err)
                    }
                }

                items(state.classrooms, key = { it.id }) { classroom ->
                    ClassroomCard(
                        classroom = classroom,
                        onEdit    = { viewModel.showEditClassroomDialog(classroom) },
                        onDelete  = { deletingId = classroom.id },
                        onCopyCode = {
                            clipboardManager.setText(AnnotatedString(classroom.accessCode))
                        }
                    )
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        // FAB crear clase
        FloatingActionButton(
            onClick           = { viewModel.showCreateClassroomDialog() },
            modifier          = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor    = PrimaryBlue,
            contentColor      = androidx.compose.ui.graphics.Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Crear clase")
        }
    }

    // Diálogo crear / editar clase
    if (state.showCreateDialog) {
        ClassroomFormDialog(
            editing  = state.editingClassroom,
            courses  = state.courses,
            onDismiss = { viewModel.dismissClassroomDialog() },
            onConfirm = { courseId, name, description ->
                val editing = state.editingClassroom
                if (editing != null) {
                    viewModel.updateClassroom(editing.id, courseId, name, description)
                } else {
                    viewModel.createClassroom(courseId, name, description)
                }
            }
        )
    }

    // Confirmar eliminación
    deletingId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingId = null },
            icon             = { Icon(Icons.Default.Delete, null, tint = ErrorColor) },
            title            = { Text("Eliminar clase") },
            text             = { Text("¿Confirmas que deseas eliminar esta clase? Esta acción no se puede deshacer.") },
            confirmButton    = {
                Button(
                    onClick = {
                        viewModel.deleteClassroom(id)
                        deletingId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                ) { Text("Eliminar") }
            },
            dismissButton    = {
                TextButton(onClick = { deletingId = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ClassroomCard(
    classroom: Classroom,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyCode: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        classroom.courseTitle.take(4).ifBlank { "EN" },
                        fontWeight = FontWeight.Black,
                        color      = PrimaryBlue,
                        fontSize   = 14.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        classroom.name,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = TextPrimary,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        classroom.courseTitle.ifBlank { "Sin curso" },
                        style  = MaterialTheme.typography.bodySmall,
                        color  = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Badge activo/inactivo
                Surface(
                    color = if (classroom.isActive) Success.copy(alpha = 0.12f) else ErrorColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        if (classroom.isActive) "Activo" else "Inactivo",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (classroom.isActive) Success else ErrorColor
                    )
                }
            }

            if (classroom.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    classroom.description,
                    style  = MaterialTheme.typography.bodySmall,
                    color  = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(10.dp))

            // Código de acceso prominente
            if (classroom.accessCode.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color    = LightBlue,
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Key, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Código de acceso", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                classroom.accessCode,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Black,
                                color      = PrimaryBlue,
                                letterSpacing = 2.sp
                            )
                        }
                        IconButton(onClick = onCopyCode, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.ContentCopy, "Copiar", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onCopyCode, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Share, "Compartir", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Estudiantes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Group, null, tint = Success, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${classroom.studentCount} estudiantes", fontSize = 13.sp, color = TextSecondary)
                }

                // Acciones
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Edit, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Delete, null, tint = ErrorColor, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassroomFormDialog(
    editing: Classroom?,
    courses: List<Course>,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String) -> Unit
) {
    var name        by remember(editing) { mutableStateOf(editing?.name ?: "") }
    var description by remember(editing) { mutableStateOf(editing?.description ?: "") }
    var selectedCourse by remember(editing) {
        mutableStateOf(courses.firstOrNull { it.id == editing?.courseId } ?: courses.firstOrNull())
    }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (editing != null) "Editar clase" else "Nueva clase",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Nombre de la clase") },
                    leadingIcon   = { Icon(Icons.Default.Class, null) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                OutlinedTextField(
                    value         = description,
                    onValueChange = { description = it },
                    label         = { Text("Descripción (opcional)") },
                    leadingIcon   = { Icon(Icons.Default.Notes, null) },
                    maxLines      = 3,
                    modifier      = Modifier.fillMaxWidth()
                )

                if (courses.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded        = expandedDropdown,
                        onExpandedChange = { expandedDropdown = it }
                    ) {
                        OutlinedTextField(
                            value         = selectedCourse?.title ?: "Seleccionar curso",
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Curso asociado") },
                            leadingIcon   = { Icon(Icons.Default.Book, null) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDropdown) },
                            modifier      = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded        = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            courses.forEach { course ->
                                DropdownMenuItem(
                                    text    = { Text("${course.title} (${course.level ?: "—"})") },
                                    onClick = {
                                        selectedCourse = course
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = {
                    val courseId = selectedCourse?.id ?: return@Button
                    if (name.isNotBlank()) onConfirm(courseId, name.trim(), description.trim())
                },
                enabled  = name.isNotBlank() && selectedCourse != null,
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(if (editing != null) "Actualizar" else "Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.08f)),
        shape  = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(18.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = ErrorColor)
        }
    }
}
