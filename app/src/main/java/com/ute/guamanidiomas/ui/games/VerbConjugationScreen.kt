package com.ute.guamanidiomas.ui.games

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
fun VerbConjugationScreen(
    onBack: () -> Unit,
    viewModel: VerbConjugationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conjugacion de Verbos", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    Surface(color = Warning.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)) {
                        Text("${uiState.score} XP", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Black, color = Warning, fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isCompleted) {
                GameCompletedView(score = uiState.score, onRestart = viewModel::startGame, onFinish = onBack)
            } else if (uiState.questions.isNotEmpty()) {
                val question = uiState.questions[uiState.currentIndex]
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${uiState.currentIndex + 1}/${uiState.questions.size}", color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (uiState.currentIndex + 1f) / uiState.questions.size },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = Color(0xFFDC2626)
                    )
                    Spacer(Modifier.height(28.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = SurfaceColor,
                        shadowElevation = 6.dp
                    ) {
                        Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(color = Color(0xFFDC2626).copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                                Text(question.tense, Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("Conjuga el verbo:", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${question.subject} _____ (${question.verb})",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

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
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            onClick = { viewModel.answer(option) },
                            enabled = !uiState.isAnswered,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = bgColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(option, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.weight(1f))
                                if (uiState.isAnswered && option == question.correctAnswer) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Success, modifier = Modifier.size(20.dp))
                                }
                                if (uiState.isAnswered && option == uiState.selectedOption && option != question.correctAnswer) {
                                    Icon(Icons.Default.Cancel, null, tint = ErrorColor, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}