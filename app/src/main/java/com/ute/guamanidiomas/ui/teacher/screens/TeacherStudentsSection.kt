package com.ute.guamanidiomas.ui.teacher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*import androidx.compose.material3.*
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
import com.ute.guamanidiomas.domain.model.teacher.Enrollment
import com.ute.guamanidiomas.ui.teacher.TeacherMainViewModel
import com.ute.guamanidiomas.ui.teacher.components.*
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun TeacherStudentsSection(viewModel: TeacherMainViewModel) {
    val state by viewModel.studentsState.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        // Selector de clase
        if (state.classrooms.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.classrooms) { classroom ->
                    val isSelected = state.selectedClassroomId == classroom.id
                    FilterChip(
                        selected = isSelected,
                        onClick  = { viewModel.selectClassroomForStudents(classroom.id) },
                        label    = { Text(classroom.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = {
                            if (isSelected) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor     = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }
        }

        if (state.enrollments.isEmpty()) {
            EmptyState(
                icon     = Icons.Default.Group,
                title    = "Sin estudiantes",
                subtitle = if (state.classrooms.isEmpty())
                    "Crea una clase primero para ver los estudiantes"
                else
                    "Esta clase no tiene estudiantes inscritos aún"
            )
        } else {
            // Resumen
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Group, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "${state.enrollments.size} estudiantes inscritos",
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextSecondary
                )
            }

            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.enrollments, key = { it.id }) { enrollment ->
                    StudentCard(enrollment = enrollment)
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun StudentCard(enrollment: Enrollment) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceColor
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    (enrollment.studentName.firstOrNull() ?: enrollment.studentEmail.firstOrNull() ?: '?')
                        .uppercase(),
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PrimaryBlue
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    enrollment.studentName.ifBlank { enrollment.studentEmail },
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = TextPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    enrollment.studentEmail,
                    style  = MaterialTheme.typography.bodySmall,
                    color  = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StudentStatChip(
                        icon  = Icons.Default.Star,
                        label = "${enrollment.totalXp} XP",
                        color = Color(0xFFF59E0B)
                    )
                    StudentStatChip(
                        icon  = Icons.Default.Whatshot,
                        label = "${enrollment.currentStreak}d",
                        color = Color(0xFFEF4444)
                    )
                    StudentStatChip(
                        icon  = Icons.Default.CheckCircle,
                        label = "${enrollment.modulesCompleted} módulos",
                        color = Success
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(12.dp))
        Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}
