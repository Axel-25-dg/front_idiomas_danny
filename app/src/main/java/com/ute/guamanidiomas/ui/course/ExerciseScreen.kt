package com.ute.guamanidiomas.ui.course

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ute.guamanidiomas.domain.model.Exercise
import com.ute.guamanidiomas.domain.model.ExerciseType
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.GamificationViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseScreen(
    exerciseId: Int,
    onClose: () -> Unit,
    onComplete: (Int) -> Unit, // Pasa el ID del ejercicio completado
    viewModel: ExerciseViewModel = hiltViewModel(),
    gamificationViewModel: GamificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    val exercise = state.exercise
    if (exercise == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se pudo cargar el ejercicio")
        }
        return
    }

    val gson = remember { Gson() }
    
    // Estados para la respuesta
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var selectedWords by remember { mutableStateOf(listOf<String>()) }
    var availableWords by remember { 
        mutableStateOf(
            if (exercise.type == ExerciseType.TRANSLATION) {
                try {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson<List<String>>(exercise.contextData, type)
                } catch (e: Exception) {
                    emptyList()
                }
            } else emptyList()
        )
    }

    var isVerified by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, null, tint = TextSecondary)
            }
            Spacer(Modifier.width(8.dp))
            val animatedProgress by animateFloatAsState(targetValue = if (isVerified) 1f else 0.5f)
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.weight(1f).height(12.dp).clip(CircleShape),
                color = Success,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(Modifier.height(40.dp))

        // Question
        Text(
            text = when (exercise.type) {
                ExerciseType.TRANSLATION -> "Traduce esta frase"
                ExerciseType.LISTENING -> "Escucha y escribe"
                else -> "Selecciona la opción correcta"
            },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = exercise.question,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(32.dp))

        // Body
        Box(modifier = Modifier.weight(1f)) {
            when (exercise.type) {
                ExerciseType.MULTIPLE_CHOICE -> {
                    val options = remember(exercise.contextData) {
                        try {
                            val type = object : TypeToken<List<String>>() {}.type
                            gson.fromJson<List<String>>(exercise.contextData, type)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        options.forEach { option ->
                            OptionButton(
                                text = option,
                                isSelected = selectedOption == option,
                                isCorrect = if (isVerified) option == exercise.correctAnswer else null,
                                enabled = !isVerified,
                                onClick = { selectedOption = option }
                            )
                        }
                    }
                }
                ExerciseType.TRANSLATION -> {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        // Area de Respuesta
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedWords.forEach { word ->
                                    WordChip(word = word, enabled = !isVerified) {
                                        selectedWords = selectedWords - word
                                        availableWords = availableWords + word
                                    }
                                }
                            }
                        }

                        // Pool de Palabras
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            availableWords.forEach { word ->
                                WordChip(word = word, enabled = !isVerified) {
                                    availableWords = availableWords - word
                                    selectedWords = selectedWords + word
                                }
                            }
                        }
                    }
                }
                else -> { /* TODO: Listening */ }
            }
        }

        // Bottom Bar
        Button(
            onClick = {
                if (isVerified) {
                    if (isCorrect) {
                        gamificationViewModel.completeLesson(exercise.id)
                        onComplete(exercise.id)
                    } else {
                        onClose()
                    }
                } else {
                    isCorrect = when (exercise.type) {
                        ExerciseType.MULTIPLE_CHOICE -> selectedOption == exercise.correctAnswer
                        ExerciseType.TRANSLATION -> selectedWords.joinToString(" ").trim().lowercase() == exercise.correctAnswer.trim().lowercase()
                        else -> false
                    }
                    isVerified = true
                }
            },
            enabled = (selectedOption != null || selectedWords.isNotEmpty()) || isVerified,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isVerified) (if (isCorrect) Success else PrimaryRed) else PrimaryBlue
            )
        ) {
            Text(
                text = if (isVerified) "CONTINUAR" else "COMPROBAR",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val color = when {
        isCorrect == true -> Success
        isCorrect == false && isSelected -> PrimaryRed
        isSelected -> PrimaryBlue
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, color),
        color = if (isSelected) color.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun WordChip(word: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = if (enabled) onClick else ({}),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Text(
            text = word,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
