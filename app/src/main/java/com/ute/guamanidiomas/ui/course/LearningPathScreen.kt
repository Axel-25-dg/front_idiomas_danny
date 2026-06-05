package com.ute.guamanidiomas.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.domain.model.Module
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningPathScreen(
    courseId: Int,
    courseTitle: String,
    onBack: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    viewModel: LearningPathViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.loadPath(courseId)
    }

    Scaffold(
        containerColor = BackgroundColor,
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error!!, color = PrimaryRed)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(42.dp)
                                    .shadow(2.dp, CircleShape)
                                    .background(
                                        color = SurfaceColor,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Column {
                                Text(
                                    courseTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    "RUTA DE APRENDIZAJE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                state.modules.forEachIndexed { index, module ->
                    item {
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                            ModuleHeader(module = module, index = index + 1)
                        }
                    }

                    val moduleExercises = state.exercisesByModule[module.id] ?: emptyList()

                    itemsIndexed(moduleExercises) { exIndex, exercise ->
                        val isLocked = index > 0 && state.completedExerciseIds.size < 1
                        LessonNode(
                            index = exIndex,
                            isLocked = isLocked,
                            isCompleted = state.completedExerciseIds.contains(exercise.id),
                            onClick = { onExerciseClick(exercise.id) }
                        )
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ModuleHeader(module: Module, index: Int) {
    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "MÓDULO $index",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = PrimaryBlue,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                module.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                module.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun LessonNode(
    index: Int,
    isLocked: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val horizontalOffset = when (index % 3) {
        0 -> 0.dp
        1 -> 50.dp
        else -> (-50).dp
    }

    Column(
        modifier = Modifier
            .offset(x = horizontalOffset)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            enabled = !isLocked,
            modifier = Modifier.size(76.dp),
            shape = CircleShape,
            color = when {
                isLocked -> PageBg
                isCompleted -> Success
                else -> PrimaryBlue
            },
            shadowElevation = if (isLocked) 0.dp else 6.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLocked) {
                    Icon(Icons.Default.Lock, null, tint = TextFaint, modifier = Modifier.size(28.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }
}
