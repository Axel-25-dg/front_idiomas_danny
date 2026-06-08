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
fun ListeningChallengeScreen(
    onBack: () -> Unit,
    viewModel: ListeningChallengeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listening Challenge", fontWeight = FontWeight.Bold) },
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
                        color = Color(0xFF059669)
                    )
                    Spacer(Modifier.height(28.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = SurfaceColor,
                        shadowElevation = 6.dp
                    ) {
                        Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier.size(64.dp).clip(CircleShape).background(Color(0xFF059669).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Hearing, null, tint = Color(0xFF059669), modifier = Modifier.size(32.dp))
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("Escucha la frase:", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "\"${question.phrase}\"",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))
                    Text("Selecciona la traduccion correcta:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(Modifier.height(16.dp))

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
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            onClick = { viewModel.answer(option) },
                            enabled = !uiState.isAnswered,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = bgColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                        ) {
                            Text(
                                option,
                                Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}