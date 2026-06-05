package com.ute.guamanidiomas.ui.teacher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.domain.model.teacher.Classroom
import com.ute.guamanidiomas.ui.teacher.TeacherMainViewModel
import com.ute.guamanidiomas.ui.teacher.components.EmptyState
import com.ute.guamanidiomas.ui.teacher.components.TeacherSection
import com.ute.guamanidiomas.ui.teacher.components.TeacherStatCard
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun TeacherHomeSection(
    viewModel: TeacherMainViewModel,
    onSectionClick: (TeacherSection) -> Unit
) {
    val state by viewModel.homeState.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Saludo personalizado
        item {
            GreetingCard(
                teacherName  = state.teacherName,
                teacherEmail = state.teacherEmail
            )
        }

        // Error si existe
        state.error?.let { err ->
            item {
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
                        Text(err, style = MaterialTheme.typography.bodySmall, color = ErrorColor)
                    }
                }
            }
        }

        // Estadísticas principales — 2 filas de 2 tarjetas
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TeacherStatCard(
                        label    = "Mis Clases",
                        value    = state.stats.totalClassrooms.toString(),
                        icon     = Icons.Default.Class,
                        color    = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    TeacherStatCard(
                        label    = "Estudiantes",
                        value    = state.stats.totalStudents.toString(),
                        icon     = Icons.Default.Group,
                        color    = Success,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TeacherStatCard(
                        label    = "Exámenes Activos",
                        value    = state.stats.activeExams.toString(),
                        icon     = Icons.Default.Assignment,
                        color    = Warning,
                        modifier = Modifier.weight(1f)
                    )
                    TeacherStatCard(
                        label    = "Recursos",
                        value    = state.stats.totalResources.toString(),
                        icon     = Icons.Default.Folder,
                        color    = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Accesos rápidos
        item {
            Text(
                "Acceso Rápido",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
        }

        item {
            QuickActionsRow(onSectionClick = onSectionClick)
        }

        // Clases recientes
        if (state.recentClassrooms.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Clases Recientes",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary
                    )
                    TextButton(onClick = { onSectionClick(TeacherSection.CLASSES) }) {
                        Text("Ver todas", color = PrimaryBlue, fontSize = 13.sp)
                    }
                }
            }

            items(state.recentClassrooms) { classroom ->
                ClassroomSummaryCard(classroom = classroom)
            }
        } else {
            item {
                EmptyState(
                    icon     = Icons.Default.Class,
                    title    = "Sin clases aún",
                    subtitle = "Crea tu primera clase para comenzar",
                    action   = "Crear clase",
                    onAction = { onSectionClick(TeacherSection.CLASSES) }
                )
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun GreetingCard(teacherName: String, teacherEmail: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 10.dp,
                shape        = RoundedCornerShape(22.dp),
                ambientColor = PrimaryBlue.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.horizontalGradient(listOf(PrimaryBlue, AccentBlue)))
                .padding(20.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "¡Hola, ${teacherName.replaceFirstChar { it.uppercase() }}!",
                        color      = Color.White,
                        fontSize   = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        teacherEmail,
                        color    = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Profesor",
                            modifier  = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            color     = Color.White,
                            fontSize  = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(onSectionClick: (TeacherSection) -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickActionItem(
            icon    = Icons.Default.Add,
            label   = "Nueva Clase",
            color   = PrimaryBlue,
            onClick = { onSectionClick(TeacherSection.CLASSES) },
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            icon    = Icons.Default.Assignment,
            label   = "Examen",
            color   = Warning,
            onClick = { onSectionClick(TeacherSection.EXAMS) },
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            icon    = Icons.Default.AttachFile,
            label   = "Recurso",
            color   = Color(0xFF8B5CF6),
            onClick = { onSectionClick(TeacherSection.RESOURCES) },
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            icon    = Icons.Default.Group,
            label   = "Alumnos",
            color   = Success,
            onClick = { onSectionClick(TeacherSection.STUDENTS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick  = onClick,
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape  = RoundedCornerShape(16.dp),
        color  = SurfaceColor
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(
                label,
                fontSize  = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color     = TextSecondary,
                maxLines  = 1
            )
        }
    }
}

@Composable
private fun ClassroomSummaryCard(classroom: Classroom) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceColor
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    classroom.courseLevel.ifBlank { "??" },
                    fontWeight = FontWeight.Black,
                    color      = PrimaryBlue,
                    fontSize   = 13.sp
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    classroom.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = TextPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    "${classroom.studentCount} estudiantes • ${classroom.courseTitle}",
                    style  = MaterialTheme.typography.bodySmall,
                    color  = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (classroom.accessCode.isNotBlank()) {
                Surface(
                    color = LightBlue,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        classroom.accessCode,
                        modifier  = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize  = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color     = PrimaryBlue
                    )
                }
            }
        }
    }
}
