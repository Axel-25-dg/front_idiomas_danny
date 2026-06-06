package com.ute.guamanidiomas.ui.student

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.domain.model.teacher.Classroom
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClassesScreen(
    onBack: () -> Unit,
    onClassDetail: (Int) -> Unit,
    onJoinClass: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val state by viewModel.classesState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadMyClasses() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Clases", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick        = onJoinClass,
                containerColor = PrimaryBlue,
                contentColor   = Color.White
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Unirse", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = BackgroundColor
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center),
                        color = PrimaryBlue,
                        strokeWidth = 3.dp
                    )
                }
                state.error != null && state.classrooms.isEmpty() -> {
                    Column(
                        Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(state.error!!, color = ErrorColor, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMyClasses() }) { Text("Reintentar") }
                    }
                }
                state.classrooms.isEmpty() -> {
                    Column(
                        Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier.size(80.dp).clip(CircleShape).background(LightBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Class, null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Sin clases", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        Text("Únete a una clase con el código de tu profesor", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onJoinClass,
                            colors  = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape   = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Unirme a una clase", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "${state.classrooms.size} clases activas",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        items(state.classrooms, key = { it.id }) { classroom ->
                            StudentClassCard(
                                classroom   = classroom,
                                onClick     = { onClassDetail(classroom.id) }
                            )
                        }
                        item { Spacer(Modifier.height(72.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentClassCard(classroom: Classroom, onClick: () -> Unit) {
    Surface(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape    = RoundedCornerShape(20.dp),
        color    = SurfaceColor
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    classroom.courseTitle.take(3).ifBlank { "EN" },
                    fontWeight = FontWeight.Black,
                    color      = PrimaryBlue,
                    fontSize   = 14.sp
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    classroom.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = TextPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Prof. ${classroom.teacherName.ifBlank { "—" }}",
                    style  = MaterialTheme.typography.bodySmall,
                    color  = TextSecondary,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(color = LightBlue, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            classroom.courseTitle.ifBlank { "N/A" },
                            modifier  = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize  = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color     = PrimaryBlue
                        )
                    }
                    Text(
                        "${classroom.studentCount} compañeros",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextTertiary)
        }
    }
}
