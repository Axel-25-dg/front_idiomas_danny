package com.ute.guamanidiomas.ui.teacher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import com.ute.guamanidiomas.domain.model.teacher.ExamResult
import com.ute.guamanidiomas.ui.teacher.TeacherMainViewModel
import com.ute.guamanidiomas.ui.teacher.components.*
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherExamsSection(viewModel: TeacherMainViewModel) {
    val state by viewModel.examsState.collectAsState()
    var deletingId by remember { mutableStateOf<Int?>(null) }
    var showResultsFor by remember { mutableStateOf<Exam?>(null) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) viewModel.clearExamsMessages()
    }

    if (state.isLoading && state.exams.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        if (state.exams.isEmpty() && !state.isLoading) {
            EmptyState(
                icon     = Icons.Default.Assignment,
                title    = "Sin exámenes",
                subtitle = "Crea un examen para evaluar a tus estudiantes",
                action   = "Crear examen",
                onAction = { viewModel.showCreateExamDialog() }
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.error?.let { err ->
                    item { ErrorCard(err) }
                }

                items(state.exams, key = { it.id }) { exam ->
                    ExamCard(
                        exam        = exam,
                        onEdit      = { viewModel.showEditExamDialog(exam) },
                        onDelete    = { deletingId = exam.id },
                        onResults   = {
                            showResultsFor = exam
                            viewModel.loadExamResults(exam.id)
                        }
                    )
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        FloatingActionButton(
            onClick        = { viewModel.showCreateExamDialog() },
            modifier       = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = Warning,
            contentColor   = Color.White
        ) {
            Icon(Icons.Default.Add, "Crear examen")
        }
    }

    // Diálogo crear examen
    if (state.showCreateDialog) {
        ExamFormDialog(
            classrooms = state.classrooms,
            onDismiss  = { viewModel.dismissExamDialog() },
            onConfirm  = { classId, title, desc, timeLimit, passing, auto, start, end ->
                viewModel.createExam(classId, title, desc, timeLimit, passing, auto, start, end)
            }
        )
    }

    // Confirmar eliminación
    deletingId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingId = null },
            icon             = { Icon(Icons.Default.Delete, null, tint = ErrorColor) },
            title            = { Text("Eliminar examen") },
            text             = { Text("¿Confirmas eliminar este examen? Se perderán todos los resultados.") },
            confirmButton    = {
                Button(
                    onClick = { viewModel.deleteExam(id); deletingId = null },
                    colors  = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                ) { Text("Eliminar") }
            },
            dismissButton    = { TextButton(onClick = { deletingId = null }) { Text("Cancelar") } }
        )
    }

    // Panel de resultados
    showResultsFor?.let { exam ->
        ExamResultsSheet(
            exam      = exam,
            results   = state.examResults,
            onDismiss = { showResultsFor = null }
        )
    }
}

@Composable
private fun ExamCard(
    exam: Exam,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onResults: () -> Unit
) {
    val activeColor = if (exam.isActive) Success else TextSecondary
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(5.dp, RoundedCornerShape(18.dp)),
        shape    = RoundedCornerShape(18.dp),
        color    = SurfaceColor
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Warning.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Assignment, null, tint = Warning, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(exam.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(exam.classroomName, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Surface(
                    color = activeColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (exam.isActive) "Activo" else "Inactivo",
                        modifier  = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize  = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color     = activeColor
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExamInfoChip(Icons.Default.Timer, "${exam.timeLimitMinutes} min")
                ExamInfoChip(Icons.Default.Score, "${exam.passingScore}% mín")
                ExamInfoChip(Icons.Default.HowToReg, "${exam.submissionCount} entregas")
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick  = onResults,
                    modifier = Modifier.weight(1f),
                    border   = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(PrimaryBlue)
                    )
                ) {
                    Icon(Icons.Default.BarChart, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Resultados", fontSize = 13.sp, color = PrimaryBlue)
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, null, tint = ErrorColor, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun ExamInfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamFormDialog(
    classrooms: List<com.ute.guamanidiomas.domain.model.teacher.Classroom>,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String, Int, Int, Boolean, String?, String?) -> Unit
) {
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var timeLimit   by remember { mutableStateOf("60") }
    var passing     by remember { mutableStateOf("70") }
    var autoGrade   by remember { mutableStateOf(true) }
    var selectedClassroom by remember {
        mutableStateOf(classrooms.firstOrNull())
    }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Examen", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = title,
                    onValueChange = { title = it },
                    label         = { Text("Título del examen") },
                    leadingIcon   = { Icon(Icons.Default.Assignment, null) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value         = description,
                    onValueChange = { description = it },
                    label         = { Text("Descripción (opcional)") },
                    maxLines      = 2,
                    modifier      = Modifier.fillMaxWidth()
                )

                if (classrooms.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded        = expandedDropdown,
                        onExpandedChange = { expandedDropdown = it }
                    ) {
                        OutlinedTextField(
                            value        = selectedClassroom?.name ?: "Seleccionar clase",
                            onValueChange = {},
                            readOnly     = true,
                            label        = { Text("Clase") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDropdown) },
                            modifier     = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded        = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            classrooms.forEach { c ->
                                DropdownMenuItem(
                                    text    = { Text(c.name) },
                                    onClick = { selectedClassroom = c; expandedDropdown = false }
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value         = timeLimit,
                        onValueChange = { if (it.all { c -> c.isDigit() }) timeLimit = it },
                        label         = { Text("Tiempo (min)") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value         = passing,
                        onValueChange = { if (it.all { c -> c.isDigit() }) passing = it },
                        label         = { Text("Nota mín %") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment  = Alignment.CenterVertically,
                    modifier           = Modifier.fillMaxWidth()
                ) {
                    Text("Calificación automática", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Switch(checked = autoGrade, onCheckedChange = { autoGrade = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = {
                    val classId = selectedClassroom?.id ?: return@Button
                    onConfirm(
                        classId, title.trim(), description.trim(),
                        timeLimit.toIntOrNull() ?: 60,
                        passing.toIntOrNull() ?: 70,
                        autoGrade, null, null
                    )
                },
                enabled  = title.isNotBlank() && selectedClassroom != null,
                colors   = ButtonDefaults.buttonColors(containerColor = Warning)
            ) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamResultsSheet(
    exam: Exam,
    results: List<ExamResult>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = SurfaceColor
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Resultados: ${exam.title}",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text("${results.size} entregas", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(Modifier.height(16.dp))

            if (results.isEmpty()) {
                Text("Ningún estudiante ha enviado este examen todavía.", color = TextSecondary)
            } else {
                LazyColumn(
                    modifier        = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(results) { result ->
                        ResultRow(result)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ResultRow(result: ExamResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color    = SurfaceColor,
        shape    = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (result.passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                null,
                tint     = if (result.passed) Success else ErrorColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(result.studentName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                Text(result.studentEmail, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(
                "${result.score}%",
                fontWeight = FontWeight.Black,
                fontSize   = 16.sp,
                color      = if (result.passed) Success else ErrorColor
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
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
