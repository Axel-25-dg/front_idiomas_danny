package com.ute.guamanidiomas.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.domain.model.Achievement
import com.ute.guamanidiomas.domain.model.UserAchievement
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBack: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val state by viewModel.achievementsState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadAchievements() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logros", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar")
                    }
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
                        Button(onClick = { viewModel.loadAchievements() }) { Text("Reintentar") }
                    }
                }
                else -> {
                    val unlockedIds = state.myAchievements.map { it.title }.toSet()

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Summary
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AchievementStatCard(
                                    label = "Desbloqueados",
                                    value = state.myAchievements.size.toString(),
                                    color = Success,
                                    modifier = Modifier.weight(1f)
                                )
                                AchievementStatCard(
                                    label = "Total",
                                    value = state.allAchievements.size.toString(),
                                    color = PrimaryBlue,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Spacer(Modifier.height(4.dp))
                            Text("Todos los logros", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        items(state.allAchievements, key = { it.id }) { achievement ->
                            val isUnlocked = achievement.title in unlockedIds
                            AchievementCard(achievement = achievement, isUnlocked = isUnlocked)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.shadow(3.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        color    = SurfaceColor
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement, isUnlocked: Boolean) {
    val alpha = if (isUnlocked) 1f else 0.5f
    val iconColor = if (isUnlocked) Warning else TextTertiary

    Surface(
        modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(18.dp)),
        shape    = RoundedCornerShape(18.dp),
        color    = SurfaceColor
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    null,
                    tint     = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    achievement.title,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = TextPrimary.copy(alpha = alpha)
                )
                Text(
                    achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary.copy(alpha = alpha)
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (achievement.xpRequired > 0) {
                        Text("${achievement.xpRequired} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Warning.copy(alpha = alpha))
                    }
                    if (achievement.streakRequired > 0) {
                        Text("${achievement.streakRequired} días", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ErrorColor.copy(alpha = alpha))
                    }
                }
            }
            if (isUnlocked) {
                Icon(Icons.Default.CheckCircle, null, tint = Success, modifier = Modifier.size(22.dp))
            }
        }
    }
}
