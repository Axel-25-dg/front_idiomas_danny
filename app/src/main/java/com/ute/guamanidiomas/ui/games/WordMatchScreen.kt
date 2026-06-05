package com.ute.guamanidiomas.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordMatchScreen(
    onBack: () -> Unit,
    viewModel: WordMatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(color = SurfaceColor, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .background(PrimaryBlue.copy(alpha = 0.08f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Word Match", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = TextPrimary)
                    Spacer(Modifier.weight(1f))
                    Surface(
                        color = PrimaryBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Score: ${uiState.score}",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        },
        containerColor = BackgroundColor
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isGameOver) {
                GameOverView(
                    score = uiState.score,
                    onRestart = { viewModel.startGame() },
                    onBack = onBack
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Une las parejas",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        "Selecciona una palabra en inglés y su traducción",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Row(modifier = Modifier.weight(1f)) {
                        // English Column
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            uiState.wordsEnglish.forEach { word ->
                                val isMatched = uiState.matchedPairs.contains(word)
                                val isSelected = uiState.selectedEnglish == word

                                WordCard(
                                    word = word,
                                    isSelected = isSelected,
                                    isMatched = isMatched,
                                    color = PrimaryBlue,
                                    onClick = { if (!isMatched) viewModel.selectEnglish(word) }
                                )
                            }
                        }

                        // Spanish Column
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            uiState.wordsSpanish.forEach { word ->
                                val isMatched = uiState.matchedSpanish.contains(word)
                                val isSelected = uiState.selectedSpanish == word

                                WordCard(
                                    word = word,
                                    isSelected = isSelected,
                                    isMatched = isMatched,
                                    color = PrimaryRed,
                                    onClick = { if (!isMatched) viewModel.selectSpanish(word) }
                                )
                            }
                        }
                    }

                    uiState.message?.let { msg ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (msg.contains("Correcto")) Success.copy(alpha = 0.1f) else LightRed
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = msg,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                                color = if (msg.contains("Correcto")) Success else PrimaryRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordCard(
    word: String,
    isSelected: Boolean,
    isMatched: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isMatched -> PageBg
        isSelected -> color
        else -> SurfaceColor
    }

    val textColor = when {
        isMatched -> TextFaint
        isSelected -> Color.White
        else -> TextPrimary
    }

    val borderColor = when {
        isSelected -> color
        isMatched -> Border
        else -> Border
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable(enabled = !isMatched) { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = word,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun GameOverView(
    score: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎉", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "¡Juego Terminado!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Puntuación final",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "$score XP",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth(0.75f).height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Jugar de nuevo", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.75f).height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryRed.copy(alpha = 0.4f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryRed)
        ) {
            Text("Salir al centro de juegos", fontWeight = FontWeight.Bold)
        }
    }
}
