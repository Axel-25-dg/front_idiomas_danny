package com.ute.guamanidiomas.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.ui.theme.*

data class GameInfo(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val xpReward: Int,
    val difficulty: String  // Fácil / Medio / Difícil
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCenterScreen(
    onNavigateToGame: (String) -> Unit
) {
    val games = listOf(
        GameInfo(
            id          = "word_match",
            title       = "Word Match",
            description = "Une palabras con su traducción",
            icon        = Icons.Default.Translate,
            color       = PrimaryBlue,
            xpReward    = 90,
            difficulty  = "Fácil"
        ),
        GameInfo(
            id          = "flashcards",
            title       = "Flashcards",
            description = "Memoriza vocabulario nuevo",
            icon        = Icons.Default.Style,
            color       = PrimaryRed,
            xpReward    = 160,
            difficulty  = "Fácil"
        ),
        GameInfo(
            id          = "sentence_builder",
            title       = "Constructor",
            description = "Forma oraciones correctas",
            icon        = Icons.Default.Extension,
            color       = Warning,
            xpReward    = 100,
            difficulty  = "Medio"
        ),
        GameInfo(
            id          = "vocab_quiz",
            title       = "Vocab Quiz",
            description = "Quiz con tiempo y opciones",
            icon        = Icons.Default.Quiz,
            color       = Color(0xFF8B5CF6),
            xpReward    = 150,
            difficulty  = "Medio"
        ),
        GameInfo(
            id          = "hangman",
            title       = "Ahorcado",
            description = "Adivina la palabra letra a letra",
            icon        = Icons.Default.Psychology,
            color       = Color(0xFF0891B2),
            xpReward    = 120,
            difficulty  = "Medio"
        ),
        GameInfo(
            id          = "memory_cards",
            title       = "Memory",
            description = "Encuentra los pares en el tablero",
            icon        = Icons.Default.GridView,
            color       = Color(0xFF059669),
            xpReward    = 120,
            difficulty  = "Difícil"
        )
    )

    Scaffold(
        topBar = {
            Surface(
                color           = SurfaceColor,
                shadowElevation = 2.dp,
                modifier        = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Centro de Juegos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("${games.size} juegos disponibles", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Surface(color = Warning.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)) {
                        Text(
                            "🎮 ${games.sumOf { it.xpReward }} XP total",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = Warning, fontWeight = FontWeight.Bold, fontSize = 12.sp
                        )
                    }
                }
            }
        },
        containerColor = BackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(listOf(PrimaryBlue, DarkBlue)))
                    .padding(22.dp)
            ) {
                Box(
                    Modifier.size(90.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-20).dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.2f))
                )
                Column {
                    Text("Aprende jugando 🎮", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text("Gana XP completando retos • Sube de nivel", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.75f))
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Elige tu juego", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns             = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding      = PaddingValues(bottom = 24.dp)
            ) {
                items(games) { game ->
                    GameGridCard(game = game, onClick = { onNavigateToGame(game.id) })
                }
            }
        }
    }
}

@Composable
fun GameGridCard(game: GameInfo, onClick: () -> Unit) {
    Surface(
        onClick         = onClick,
        shape           = RoundedCornerShape(20.dp),
        color           = SurfaceColor,
        shadowElevation = 4.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(game.color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(game.icon, null, tint = game.color, modifier = Modifier.size(26.dp))
                }
                Surface(
                    color = difficultyColor(game.difficulty).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        game.difficulty,
                        modifier  = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        color     = difficultyColor(game.difficulty),
                        fontSize  = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(game.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            Spacer(Modifier.height(3.dp))
            Text(game.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)

            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(color = Warning.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        "⚡ ${game.xpReward} XP",
                        modifier  = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color     = Warning,
                        fontSize  = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(Icons.Default.PlayArrow, null, tint = game.color, modifier = Modifier.size(22.dp))
            }
        }
    }
}

// Mantener compatibilidad con la función GameCard existente
@Composable
fun GameCard(game: GameInfo, onClick: () -> Unit) = GameGridCard(game = game, onClick = onClick)

private fun difficultyColor(d: String): Color = when (d) {
    "Fácil"  -> Color(0xFF10B981)
    "Medio"  -> Color(0xFFF59E0B)
    "Difícil" -> Color(0xFFEF4444)
    else     -> Color(0xFF6B7280)
}
