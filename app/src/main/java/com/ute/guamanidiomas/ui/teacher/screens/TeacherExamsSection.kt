package com.ute.guamanidiomas.ui.teacher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ute.guamanidiomas.domain.model.Lesson
import com.ute.guamanidiomas.ui.teacher.TeacherMainViewModel
import com.ute.guamanidiomas.ui.teacher.components.*
import com.ute.guamanidiomas.ui.theme.*

/**
 * Los "exámenes" en el backend son Lessons con content_type = "interactive".
 * Las preguntas son Exercises asociados a esa Lesson.
 *
 * Endpoint: GET /api/lessons/ → filtrar localmente por content_type == "interactive"
 * Crear:    POST /api/lessons/ con { module, title, content_type: "interactive", xp_reward: 100 }
 */
@Composable
fun TeacherExamsSection(viewModel: TeacherMainViewModel) {
    val state by viewModel.examsState.collectAsState()

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
                    Button(
                        onClick = { viewModel.loadExams() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text("Reintentar") }
                }
            } else {
                EmptyState(
                    icon     = Icons.Default.Quiz,
                    title    = "Sin examenes",
                    subtitle = "Los examenes se crean como lecciones interactivas con ejercicios. Usa el panel de cursos para crear una leccion con tipo 'interactive'.",
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.error?.let { err ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(err, style = MaterialTheme.typography.bodySmall, color = ErrorColor)
                            }
                        }
                    }
                }

                item {
                    Surface(
                        color = LightBlue,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Los examenes son lecciones con tipo 'interactive'. Las preguntas son ejercicios asociados.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryBlue
                            )
                        }
                    }
                }

                items(state.exams, key = { it.id }) { exam ->
                    ExamLessonCard(exam = exam)
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun ExamLessonCard(exam: com.ute.guamanidiomas.domain.model.teacher.Exam) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceColor
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Warning.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Quiz, null, tint = Warning, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    exam.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    exam.classroomName.ifBlank { "Examen interactivo" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("${exam.submissionCount} entregas", fontSize = 12.sp, color = TextSecondary)
                    if (exam.isActive) {
                        Text("Activo", fontSize = 12.sp, color = Success, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
