package com.ute.guamanidiomas.ui.student

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.domain.model.teacher.Classroom
import com.ute.guamanidiomas.domain.model.teacher.ResourceType
import com.ute.guamanidiomas.domain.model.teacher.TeacherResource
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomDetailScreen(
    classroomId: Int,
    onBack: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val state by viewModel.classDetailState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(classroomId) { viewModel.loadClassDetail(classroomId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.classroom?.name ?: "Clase", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor, titleContentColor = TextPrimary)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
            }
            return@Scaffold
        }

        state.error?.let { err ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(err.orEmpty(), color = ErrorColor)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadClassDetail(classroomId) }) { Text("Reintentar") }
                }
            }
            return@Scaffold
        }

        val classroom = state.classroom ?: return@Scaffold

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { ClassInfoCard(classroom) }

            if (state.resources.isNotEmpty()) {
                item {
                    Text(
                        "Recursos y Materiales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                items(state.resources, key = { it.id }) { resource ->
                    ResourceItemCard(resource = resource, onOpen = {
                        val url = resource.fileUrl ?: resource.url
                        if (!url.isNullOrBlank()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    })
                }
            } else {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = LightBlue,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("El profesor aun no ha publicado recursos para esta clase.", style = MaterialTheme.typography.bodySmall, color = PrimaryBlue)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ClassInfoCard(classroom: Classroom) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Class, null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(classroom.name.orEmpty(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                    Text(classroom.courseTitle.orEmpty(), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(12.dp))

            if (!classroom.description.isNullOrBlank()) {
                Text(classroom.description.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(12.dp))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoChip(Icons.Default.Person, "Prof. ${classroom.teacherName.orEmpty()}")
                InfoChip(Icons.Default.Group, "${classroom.studentCount} alumnos")
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoChip(Icons.Default.Key, classroom.accessCode.orEmpty())
                if (classroom.isActive) {
                    Surface(color = Success.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
                        Text("Activa", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = Success, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ResourceItemCard(resource: TeacherResource, onOpen: () -> Unit) {
    val typeColor = when (resource.resourceType) {
        ResourceType.PDF   -> Color(0xFFEF4444)
        ResourceType.AUDIO -> Color(0xFF7C3AED)
        ResourceType.VIDEO -> Color(0xFF0891B2)
        ResourceType.WORD  -> Color(0xFF2563EB)
        ResourceType.IMAGE -> Color(0xFFEA580C)
        ResourceType.LINK  -> Color(0xFF059669)
        ResourceType.OTHER -> Color(0xFF6B7280)
    }
    val typeIcon = when (resource.resourceType) {
        ResourceType.PDF   -> Icons.Default.PictureAsPdf
        ResourceType.AUDIO -> Icons.Default.MusicNote
        ResourceType.VIDEO -> Icons.Default.Videocam
        ResourceType.WORD  -> Icons.Default.Description
        ResourceType.IMAGE -> Icons.Default.Image
        ResourceType.LINK  -> Icons.Default.Link
        ResourceType.OTHER -> Icons.Default.InsertDriveFile
    }

    Surface(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceColor
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(typeColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(typeIcon, null, tint = typeColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(resource.title.orEmpty(), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (!resource.description.isNullOrBlank()) {
                    Text(resource.description.orEmpty(), style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text(resource.resourceType.label, style = MaterialTheme.typography.labelSmall, color = typeColor, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.OpenInNew, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
    }
}