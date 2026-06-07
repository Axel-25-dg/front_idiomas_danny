package com.ute.guamanidiomas.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.domain.model.StudentStats
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val state by viewModel.leaderboardState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadLeaderboard() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor, titleContentColor = TextPrimary)
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = PrimaryBlue, strokeWidth = 3.dp)
                state.error != null -> {
                    Column(Modifier.align(Alignment.Center).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.error!!, color = ErrorColor)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadLeaderboard() }) { Text("Reintentar") }
                    }
                }
                state.students.isEmpty() -> {
                    Column(Modifier.align(Alignment.Center).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Leaderboard, null, tint = PrimaryBlue, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Sin datos de ranking", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Completa actividades para aparecer aquí", color = TextSecondary)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Top 3 banner
                        if (state.students.size >= 3) {
                            item { TopThreeBanner(state.students.take(3)) }
                            item { Spacer(Modifier.height(8.dp)) }
                        }

                        itemsIndexed(state.students) { index, student ->
                            LeaderboardRow(position = index + 1, student = student)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopThreeBanner(top3: List<StudentStats>) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(22.dp)),
        shape    = RoundedCornerShape(22.dp),
        color    = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PrimaryBlue, Color(0xFF4F46E5))), RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                top3.forEachIndexed { index, student ->
                    val medal = when (index) { 0 -> "🥇"; 1 -> "🥈"; else -> "🥉" }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(medal, fontSize = 28.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            student.userEmail.orEmpty().substringBefore("@"),
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 12.sp,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        Text(
                            "${student.totalXp} XP",
                            color    = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(position: Int, student: StudentStats) {
    val posColor = when (position) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> TextSecondary
    }

    Surface(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        color    = SurfaceColor
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(posColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "#$position",
                    fontWeight = FontWeight.Black,
                    fontSize   = 13.sp,
                    color      = posColor
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    student.userEmail.orEmpty().substringBefore("@"),
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = TextPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🔥 ${student.currentStreak}d", fontSize = 11.sp, color = TextSecondary)
                    Text("📚 ${student.modulesCompleted} módulos", fontSize = 11.sp, color = TextSecondary)
                }
            }
            Text(
                "${student.totalXp} XP",
                fontWeight = FontWeight.Black,
                fontSize   = 16.sp,
                color      = Warning
            )
        }
    }
}
