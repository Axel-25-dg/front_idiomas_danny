package com.ute.guamanidiomas.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabQuizScreen(
    onBack: () -> Unit,
    viewModel: VocabQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz de Vocabulario", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    Surface(color = Warning.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)) {
                        Text(
                            "${uiState.score} XP",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Black,
                            color = Warning,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor, titleContentColor = TextPrimary)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isCompleted -> QuizResultScreen(
                    score        = uiState.score,
                    correctCount = uiState.correctCount,
                    total        = uiState.questions.size,
                    onRestart    = viewModel::restart,
                    onBack       = onBack
                )
                uiState.questions.isEmpty() -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = PrimaryBlue)
                }
                else -> {
                    val question = uiState.questions[uiState.currentIndex]
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress + timer row
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${uiState.currentIndex + 1}/${uiState.questions.size}",
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelLarge
                            )
                            TimerCircle(timeLeft = uiState.timeLeft)
                        }

                        Spacer(Modifier.height(8.dp))
                        val progress by animateFloatAsState(
                            (uiState.currentIndex + 1f) / uiState.questions.size,
                            tween(400), label = "progress"
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = PrimaryBlue
                        )

                        Spacer(Modifier.height(32.dp))

                        // Word card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(24.dp),
                            color    = SurfaceColor,
                            shadowElevation = 6.dp
                        ) {
                            Column(
                                Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "¿Cómo se traduce?",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextSecondary
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    question.word,
                                    fontSize   = 42.sp,
                                    fontWeight = FontWeight.Black,
                                    color      = PrimaryBlue
                                )
                            }
                        }

                        Spacer(Modifier.height(28.dp))

                        // Options
                        question.options.forEach { option ->
                            val bgColor = when {
                                !uiState.isAnswered -> SurfaceColor
                                option == question.correctAnswer -> Success.copy(alpha = 0.15f)
                                option == uiState.selectedOption -> ErrorColor.copy(alpha = 0.15f)
                                else -> SurfaceColor
                            }
                            val borderColor = when {
                                !uiState.isAnswered -> Border
                                option == question.correctAnswer -> Success
                                option == uiState.selectedOption -> ErrorColor
                                else -> Border
                            }
                            val textColor = when {
                                !uiState.isAnswered -> TextPrimary
                                option == question.correctAnswer -> Success
                                option == uiState.selectedOption -> ErrorColor
                                else -> TextSecondary
                            }

                            Spacer(Modifier.height(10.dp))
                            Surface(
                                onClick      = { viewModel.answer(option) },
                                enabled      = !uiState.isAnswered,
                                modifier     = Modifier.fillMaxWidth(),
                                shape        = RoundedCornerShape(16.dp),
                                color        = bgColor,
                                border       = androidx.compose.foundation.BorderStroke(
                                    if (uiState.isAnswered && (option == question.correctAnswer || option == uiState.selectedOption)) 2.dp else 1.dp,
                                    borderColor
                                ),
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        option,
                                        fontWeight = FontWeight.SemiBold,
                                        color      = textColor,
                                        modifier   = Modifier.weight(1f)
                                    )
                                    if (uiState.isAnswered) {
                                        when (option) {
                                            question.correctAnswer -> Icon(Icons.Default.CheckCircle, null, tint = Success, modifier = Modifier.size(20.dp))
                                            uiState.selectedOption -> Icon(Icons.Default.Cancel, null, tint = ErrorColor, modifier = Modifier.size(20.dp))
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerCircle(timeLeft: Int) {
    val color = when {
        timeLeft > 6 -> Success
        timeLeft > 3 -> Warning
        else         -> ErrorColor
    }
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$timeLeft",
            fontWeight = FontWeight.Black,
            fontSize   = 16.sp,
            color      = color
        )
    }
}

@Composable
private fun QuizResultScreen(
    score: Int,
    correctCount: Int,
    total: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    val pct = (correctCount * 100) / total.coerceAtLeast(1)
    val emoji = when {
        pct >= 90 -> "🏆"
        pct >= 70 -> "⭐"
        pct >= 50 -> "👍"
        else      -> "💪"
    }
    Column(
        Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text("¡Quiz completado!", fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("$correctCount/$total correctas", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            "$score XP",
            fontSize = 36.sp, fontWeight = FontWeight.Black, color = Warning
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) { Text("Jugar de nuevo", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) { Text("Volver", fontWeight = FontWeight.Bold, color = PrimaryBlue) }
    }
}
