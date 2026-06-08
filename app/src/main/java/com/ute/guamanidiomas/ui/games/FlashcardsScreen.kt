package com.ute.guamanidiomas.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    onBack: () -> Unit,
    viewModel: FlashcardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashcards", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceColor,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isCompleted) {
                GameCompletedView(
                    score = uiState.score,
                    onRestart = { viewModel.startSession() },
                    onFinish = onBack
                )
            } else if (uiState.cards.isNotEmpty()) {
                val currentCard = uiState.cards[uiState.currentIndex]
                
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Carta ${uiState.currentIndex + 1} de ${uiState.cards.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (uiState.currentIndex + 1).toFloat() / uiState.cards.size },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = PrimaryBlue,
                        trackColor = Border.copy(alpha = 0.2f)
                    )
                    
                    Spacer(Modifier.height(48.dp))

                    FlashcardItem(
                        card = currentCard,
                        isFlipped = uiState.isFlipped,
                        onClick = { viewModel.flipCard() }
                    )

                    Spacer(Modifier.height(48.dp))

                    if (uiState.isFlipped) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = { viewModel.nextCard(false) },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Cancel, null, tint = PrimaryRed)
                                    Spacer(Modifier.width(8.dp))
                                    Text("No la sabía", color = PrimaryRed, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { viewModel.nextCard(true) },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Success.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Success)
                                    Spacer(Modifier.width(8.dp))
                                    Text("¡La sabía!", color = Success, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Text(
                            "Toca la carta para ver la traducción",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FlashcardItem(
    card: Flashcard,
    isFlipped: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "flip"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                if (rotation <= 90f || rotation >= 270f) Color.White else PrimaryBlue
            ),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f || rotation >= 270f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = card.english,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Inglés",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.spanish,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Español",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GameCompletedView(score: Int, onRestart: () -> Unit, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(120.dp).clip(RoundedCornerShape(40.dp)).background(Success.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Star, null, tint = Success, modifier = Modifier.size(60.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("¡Sesión completada!", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text("Has ganado $score XP", fontSize = 16.sp, color = TextSecondary)
        
        Spacer(Modifier.height(40.dp))
        
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Repasar de nuevo", fontWeight = FontWeight.Bold)
        }
        
        Spacer(Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, PrimaryBlue)
        ) {
            Text("Volver al inicio", color = PrimaryBlue, fontWeight = FontWeight.Bold)
        }
    }
}