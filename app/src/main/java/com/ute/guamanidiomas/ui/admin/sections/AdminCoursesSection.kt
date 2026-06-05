package com.ute.guamanidiomas.ui.admin.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.ui.admin.AdminMainViewModel
import com.ute.guamanidiomas.ui.admin.components.*
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun AdminCoursesSection(viewModel: AdminMainViewModel) {
    val state by viewModel.coursesState.collectAsState()
    var deletingId by remember { mutableStateOf<Int?>(null) }
    var search by remember { mutableStateOf("") }

    val filtered = remember(state.courses, search) {
        if (search.isBlank()) state.courses
        else state.courses.filter {
            it.title?.contains(search, ignoreCase = true) == true ||
            it.level?.contains(search, ignoreCase = true) == true
        }
    }

    if (state.isLoading && state.courses.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // Buscador
            OutlinedTextField(
                value         = search,
                onValueChange = { search = it },
                label         = { Text("Buscar curso…") },
                leadingIcon   = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                singleLine    = true,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape         = RoundedCornerShape(12.dp)
            )

            state.error?.let { err ->
                AdminErrorBanner(message = err, modifier = Modifier.padding(horizontal = 16.dp))
            }

            // Resumen por nivel
            if (state.courses.isNotEmpty()) {
                LevelSummaryRow(courses = state.courses)
            }

            if (filtered.isEmpty() && !state.isLoading) {
                AdminEmptyState(
                    icon     = Icons.Default.Book,
                    title    = "Sin cursos",
                    subtitle = "No hay cursos que coincidan con la búsqueda"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            "${filtered.size} cursos",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(filtered, key = { it.id }) { course ->
                        CourseAdminCard(
                            course   = course,
                            onDelete = { deletingId = course.id }
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }

    // Confirmar eliminación
    deletingId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingId = null },
            icon   = { Icon(Icons.Default.Delete, null, tint = ErrorColor) },
            title  = { Text("Eliminar curso") },
            text   = { Text("¿Confirmas eliminar este curso? Se eliminarán todos sus módulos y lecciones.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteCourse(id); deletingId = null },
                    colors  = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { deletingId = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun LevelSummaryRow(courses: List<Course>) {
    val byLevel = courses.groupBy { it.level ?: "—" }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        byLevel.entries.take(6).forEach { (level, list) ->
            Surface(
                color = LightBlue,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "$level: ${list.size}",
                    modifier  = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize  = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color     = PrimaryBlue
                )
            }
        }
    }
}

@Composable
private fun CourseAdminCard(
    course: Course,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        color    = SurfaceColor
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Nivel badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(LightBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    course.level ?: "??",
                    fontWeight = FontWeight.Black,
                    color      = PrimaryBlue,
                    fontSize   = 13.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    course.title ?: "Sin título",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = TextPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (course.isActive) "● Activo" else "○ Inactivo",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (course.isActive) Success else TextSecondary
                    )
                    Text(
                        "$ ${course.price}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, null, tint = ErrorColor, modifier = Modifier.size(18.dp))
            }
        }
    }
}
