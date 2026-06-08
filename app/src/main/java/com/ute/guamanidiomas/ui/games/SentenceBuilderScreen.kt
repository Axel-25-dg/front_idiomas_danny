package com.ute.guamanidiomas.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SentenceBuilderScreen(
    onBack: () -> Unit,
    viewModel: SentenceBuilderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Constructor", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isCompleted) {
                GameCompletedView(
                    score = uiState.score,
                    onRestart = { viewModel.loadLevel(0) },
                    onFinish = onBack
                )
            } else if (uiState.currentSentence != null) {
                val sentence = uiState.currentSentence!!
                
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Traduce la oración:",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = SurfaceColor,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sentence.spanishTranslation,
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = TextPrimary
                        )
                    }
                    
                    Spacer(Modifier.height(40.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PageBg)
                            .border(1.dp, Border.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(12.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.selectedWords.forEach { word ->
                                WordChip(word = word, onClick = { viewModel.removeWord(word) })
                            }
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.availableWords.forEach { word ->
                            WordChip(
                                word = word, 
                                onClick = { if (uiState.isCorrect == null) viewModel.selectWord(word) },
                                isOutline = true
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    AnimatedVisibility(visible = uiState.isCorrect != null) {
                        val isCorrect = uiState.isCorrect == true
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isCorrect) Success.copy(alpha = 0.1f) else PrimaryRed.copy(alpha = 0.1f))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isCorrect) Success else PrimaryRed
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = if (isCorrect) "¡Excelente! +25 XP" else "Inténtalo de nuevo",
                                    color = if (isCorrect) Success else PrimaryRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    if (uiState.isCorrect == true) {
                        Button(
                            onClick = { viewModel.loadLevel(uiState.currentLevel + 1) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Siguiente nivel", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.checkSentence() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = uiState.selectedWords.isNotEmpty(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Comprobar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordChip(word: String, onClick: () -> Unit, isOutline: Boolean = false) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isOutline) SurfaceColor else Color.White,
        border = if (isOutline) borderStroke() else null,
        shadowElevation = if (isOutline) 0.dp else 2.dp,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = word,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Border.copy(alpha = 0.5f))