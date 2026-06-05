package com.ute.guamanidiomas.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
fun MemoryCardsScreen(
    onBack: () -> Unit,
    viewModel: MemoryCardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memory Cards", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    Surface(color = Warning.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)) {
                        Text(
                            "${uiState.score} XP",
                            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
        if (uiState.isCompleted) {
            CompletedView(
                score  = uiState.score,
                moves  = uiState.moves,
                onRestart = viewModel::restart,
                onBack    = onBack
            )
        } else {
            Column(
                Modifier.fillMaxSize().padding(padding).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Movimientos: ${uiState.moves}", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
                    Text(
                        "${uiState.cards.count { it.isMatched } / 2}/${uiState.cards.size / 2} pares",
                        color = PrimaryBlue,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Encuentra los pares inglés–español",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.cards, key = { it.id }) { card ->
                        MemoryCardItem(
                            card    = card,
                            onClick = { viewModel.flipCard(card.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryCardItem(card: MemoryCard, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(300),
        label = "flip"
    )

    val cardColor = when {
        card.isMatched -> Success.copy(alpha = 0.15f)
        card.isFlipped -> if (card.isEnglish) PrimaryBlue.copy(alpha = 0.1f) else PrimaryRed.copy(alpha = 0.1f)
        else           -> SurfaceColor
    }

    Surface(
        onClick = onClick,
        enabled = !card.isFlipped && !card.isMatched,
        modifier = Modifier
            .aspectRatio(0.75f)
            .shadow(if (card.isFlipped || card.isMatched) 0.dp else 4.dp, RoundedCornerShape(14.dp))
            .graphicsLayer { cameraDistance = 12f * density },
        shape = RoundedCornerShape(14.dp),
        color = cardColor
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationY = if (rotation > 90f) 180f else 0f }
        ) {
            if (rotation <= 90f) {
                // Cara oculta
                Text("?", fontSize = 28.sp, fontWeight = FontWeight.Black,
                    color = if (card.isFlipped || card.isMatched) Color.Transparent else TextTertiary)
            } else {
                // Cara visible
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        if (card.isEnglish) "🇬🇧" else "🇪🇸",
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        card.content,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = if (card.isEnglish) PrimaryBlue else PrimaryRed,
                        textAlign  = TextAlign.Center,
                        maxLines   = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedView(score: Int, moves: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Star, null, tint = Warning, modifier = Modifier.size(72.dp))
        Spacer(Modifier.height(16.dp))
        Text("¡Tablero completado!", fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("$moves movimientos", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        Text("$score XP", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Warning)
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onRestart,
            Modifier.fillMaxWidth().height(54.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) { Text("Jugar de nuevo", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) { Text("Volver", fontWeight = FontWeight.Bold, color = PrimaryBlue) }
    }
}
