package com.ute.guamanidiomas.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

private val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HangmanScreen(
    onBack: () -> Unit,
    viewModel: HangmanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Ahorcado", fontWeight = FontWeight.Bold)
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    Surface(color = Warning.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)) {
                        Text(
                            "${uiState.score} XP",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Black, color = Warning, fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor, titleContentColor = TextPrimary)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Ronda ${uiState.round}/${uiState.totalRounds}", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
                Text("Vidas: ${"❤️".repeat(uiState.remainingLives.coerceAtLeast(0))}${"🖤".repeat(uiState.wrongGuesses.coerceAtLeast(0))}", fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            HangmanDrawing(wrongGuesses = uiState.wrongGuesses)

            Spacer(Modifier.height(16.dp))

            Surface(color = LightBlue, shape = RoundedCornerShape(10.dp)) {
                Text(
                    "💡 ${uiState.hint}",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = PrimaryBlue,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                uiState.displayWord,
                fontSize   = 30.sp,
                fontWeight = FontWeight.Black,
                color      = TextPrimary,
                letterSpacing = 6.sp,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            if (uiState.isGameOver) {
                Surface(
                    color = if (uiState.isWon) Success.copy(alpha = 0.1f) else ErrorColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (uiState.isWon) "🎉 ¡Correcto!" else "💀 Era: ${uiState.word}",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Black,
                            color      = if (uiState.isWon) Success else ErrorColor
                        )
                        Spacer(Modifier.height(12.dp))
                        if (uiState.round < uiState.totalRounds) {
                            Button(
                                onClick = viewModel::nextRound,
                                colors  = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape   = RoundedCornerShape(12.dp)
                            ) { Text("Siguiente →", fontWeight = FontWeight.Bold) }
                        } else {
                            Text("¡Juego completo! ${uiState.score} XP", fontWeight = FontWeight.Bold, color = Warning, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = viewModel::restart,
                                colors  = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape   = RoundedCornerShape(12.dp)
                            ) { Text("Jugar de nuevo", fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ALPHABET) { letter ->
                        val isGuessed = letter in uiState.guessedLetters
                        val isCorrect = isGuessed && letter in uiState.word
                        val isWrong   = isGuessed && letter !in uiState.word
                        val bgColor = when {
                            isCorrect -> Success.copy(alpha = 0.15f)
                            isWrong   -> ErrorColor.copy(alpha = 0.15f)
                            else      -> SurfaceColor
                        }
                        val txtColor = when {
                            isCorrect -> Success
                            isWrong   -> ErrorColor.copy(alpha = 0.5f)
                            else      -> TextPrimary
                        }
                        Surface(
                            onClick  = { viewModel.guess(letter) },
                            enabled  = !isGuessed,
                            shape    = RoundedCornerShape(8.dp),
                            color    = bgColor,
                            shadowElevation = if (!isGuessed) 2.dp else 0.dp,
                            modifier = Modifier.aspectRatio(1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(letter.toString(), fontWeight = FontWeight.Bold, color = txtColor, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HangmanDrawing(wrongGuesses: Int) {
    val lineColor = if (wrongGuesses >= 6) ErrorColor else TextPrimary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("   |———|", fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = TextSecondary, fontSize = 18.sp)
            }
            Text("   |    ${if (wrongGuesses >= 1) "😵" else "  "}", fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = lineColor, fontSize = 18.sp)
            Text("   |   ${if (wrongGuesses >= 3) "\\${if (wrongGuesses >= 2) "|" else " "}" else if (wrongGuesses >= 2) " |" else "  "}/", fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = lineColor, fontSize = 18.sp)
            Text("   |   ${if (wrongGuesses >= 4) "/" else " "} ${if (wrongGuesses >= 5) "\\" else " "}", fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = lineColor, fontSize = 18.sp)
            Text("  _|_", fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = TextSecondary, fontSize = 18.sp)
        }
    }
}