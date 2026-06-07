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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.domain.model.teacher.ResourceType
import com.ute.guamanidiomas.domain.model.teacher.TeacherResource
import com.ute.guamanidiomas.ui.teacher.TeacherMainViewModel
import com.ute.guamanidiomas.ui.teacher.components.*
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherResourcesSection(viewModel: TeacherMainViewModel) {
    val state by viewModel.resourcesState.collectAsState()
    var deletingId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) viewModel.clearResourcesMessages()
    }

    if (state.isLoading && state.resources.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        if (state.resources.isEmpty() && !state.isLoading) {
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
                        onClick = { viewModel.loadResources() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text("Reintentar") }
                }
            } else {
                EmptyState(
                    icon     = Icons.Default.Folder,
                    title    = "Sin recursos",
                    subtitle = "Sube materiales para tus estudiantes: PDFs, videos, enlaces y más",
                    action   = "Agregar recurso",
                    onAction = { viewModel.showCreateResourceDialog() }
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
                            shape  = RoundedCornerShape(12.dp)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(18.dp))
                                Text(err, style = MaterialTheme.typography.bodySmall, color = ErrorColor)
                            }
                        }
                    }
                }

                // Agrupar por tipo
                val grouped = state.resources.groupBy { it.resourceType }
                grouped.forEach { (type, resources) ->
                    item {
                        ResourceGroupHeader(type)
                    }
                    items(resources, key = { it.id }) { resource ->
                        ResourceCard(
                            resource = resource,
                            onDelete = { deletingId = resource.id }
                        )
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        FloatingActionButton(
            onClick        = { viewModel.showCreateResourceDialog() },
            modifier       = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = Color(0xFF8B5CF6),
            contentColor   = Color.White
        ) {
            Icon(Icons.Default.Add, "Agregar recurso")
        }
    }

    // Diálogo nuevo recurso
    if (state.showCreateDialog) {
        ResourceFormDialog(
            courses    = state.courses,
            onDismiss  = { viewModel.dismissResourceDialog() },
            onConfirm  = { courseId, title, desc, type, url ->
                viewModel.createResource(courseId, title, desc, type, url)
            }
        )
    }

    // Confirmar eliminación
    deletingId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingId = null },
            icon   = { Icon(Icons.Default.Delete, null, tint = ErrorColor) },
            title  = { Text("Eliminar recurso") },
            text   = { Text("¿Deseas eliminar este recurso?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteResource(id); deletingId = null },
                    colors  = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { deletingId = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ResourceGroupHeader(type: ResourceType) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            resourceTypeIcon(type),
            null,
            tint     = resourceTypeColor(type),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            type.label,
            style      = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color      = TextSecondary
        )
    }
}

@Composable
private fun ResourceCard(resource: TeacherResource, onDelete: () -> Unit) {
    val typeColor = resourceTypeColor(resource.resourceType)

    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        color    = SurfaceColor
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(typeColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    resourceTypeIcon(resource.resourceType),
                    null,
                    tint     = typeColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    resource.title ?: "Recurso sin título",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = TextPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                if (!(resource.description ?: "").isBlank()) {
                    Text(
                        resource.description ?: "",
                        style  = MaterialTheme.typography.bodySmall,
                        color  = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    (resource.classroomName ?: "").ifBlank { resource.url ?: "Sin enlace" },
                    style  = MaterialTheme.typography.bodySmall,
                    color  = PrimaryBlue.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.Delete, null, tint = ErrorColor, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResourceFormDialog(
    courses: List<com.ute.guamanidiomas.domain.model.Course>,
    onDismiss: () -> Unit,
    onConfirm: (Int?, String, String, String, String) -> Unit
) {
    var title        by remember { mutableStateOf("") }
    var description  by remember { mutableStateOf("") }
    var fileUrl      by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ResourceType.LINK) }
    var expandedType by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<com.ute.guamanidiomas.domain.model.Course?>(null) }
    var expandedCourse by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Recurso", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Selector de Curso
                ExposedDropdownMenuBox(
                    expanded         = expandedCourse,
                    onExpandedChange = { expandedCourse = it }
                ) {
                    OutlinedTextField(
                        value        = selectedCourse?.title ?: "Seleccionar curso",
                        onValueChange = {},
                        readOnly     = true,
                        label        = { Text("Curso *") },
                        leadingIcon  = { Icon(Icons.Default.School, null, tint = PrimaryBlue) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCourse) },
                        modifier     = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded         = expandedCourse,
                        onDismissRequest = { expandedCourse = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text    = { Text(course.title ?: "Curso #${course.id}") },
                                onClick = { selectedCourse = course; expandedCourse = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value         = title,
                    onValueChange = { title = it },
                    label         = { Text("Titulo *") },
                    leadingIcon   = { Icon(Icons.Default.Title, null) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value         = description,
                    onValueChange = { description = it },
                    label         = { Text("Descripcion (opcional)") },
                    maxLines      = 2,
                    modifier      = Modifier.fillMaxWidth()
                )

                // Tipo de recurso
                ExposedDropdownMenuBox(
                    expanded         = expandedType,
                    onExpandedChange = { expandedType = it }
                ) {
                    OutlinedTextField(
                        value        = selectedType.label,
                        onValueChange = {},
                        readOnly     = true,
                        label        = { Text("Tipo de recurso") },
                        leadingIcon  = { Icon(resourceTypeIcon(selectedType), null, tint = resourceTypeColor(selectedType)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedType) },
                        modifier     = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded         = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        ResourceType.entries.forEach { type ->
                            DropdownMenuItem(
                                leadingIcon = { Icon(resourceTypeIcon(type), null, tint = resourceTypeColor(type), modifier = Modifier.size(18.dp)) },
                                text        = { Text(type.label) },
                                onClick     = { selectedType = type; expandedType = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value         = fileUrl,
                    onValueChange = { fileUrl = it },
                    label         = { Text("URL del archivo o enlace") },
                    leadingIcon   = { Icon(Icons.Default.Link, null) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    placeholder   = { Text("https://drive.google.com/...") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            selectedCourse?.id,
                            title.trim(),
                            description.trim(),
                            selectedType.value,
                            fileUrl.trim()
                        )
                    }
                },
                enabled = title.isNotBlank() && fileUrl.isNotBlank() && selectedCourse != null,
                colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
            ) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun resourceTypeIcon(type: ResourceType): ImageVector = when (type) {
    ResourceType.PDF   -> Icons.Default.PictureAsPdf
    ResourceType.WORD  -> Icons.Default.Description
    ResourceType.AUDIO -> Icons.Default.MusicNote
    ResourceType.VIDEO -> Icons.Default.Videocam
    ResourceType.IMAGE -> Icons.Default.Image
    ResourceType.LINK  -> Icons.Default.Link
    ResourceType.OTHER -> Icons.Default.InsertDriveFile
}

private fun resourceTypeColor(type: ResourceType): Color = when (type) {
    ResourceType.PDF   -> Color(0xFFEF4444)
    ResourceType.WORD  -> Color(0xFF2563EB)
    ResourceType.AUDIO -> Color(0xFF7C3AED)
    ResourceType.VIDEO -> Color(0xFF0891B2)
    ResourceType.IMAGE -> Color(0xFFEA580C)
    ResourceType.LINK  -> Color(0xFF059669)
    ResourceType.OTHER -> Color(0xFF6B7280)
}
