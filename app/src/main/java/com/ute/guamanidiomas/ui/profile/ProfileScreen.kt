package com.ute.guamanidiomas.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.ui.viewmodel.AuthViewModel
import com.ute.guamanidiomas.ui.theme.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    gamificationViewModel: com.ute.guamanidiomas.ui.viewmodel.GamificationViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToPremium: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToCertificates: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {}
) {
    val user by authViewModel.currentUser.collectAsState()
    val gamificationState by gamificationViewModel.state.collectAsState()

    val stats = gamificationState.stats
    val achievements = gamificationState.achievements
    
    val achievementRows = remember(achievements) { achievements.chunked(2) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        gamificationViewModel.loadDashboard()
    }

    Scaffold(
        containerColor = BackgroundColor,
        contentWindowInsets = WindowInsets(0),
        topBar = { ProfileTopBar(onNavigateToSettings) }
    ) { padding ->
        if (gamificationState.isLoading && stats == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    ProfileHeroSection(
                        username = user?.username,
                        email = user?.email,
                        level = if ((stats?.totalXp ?: 0) > 1000) "Avanzado" else "Principiante"
                    )
                }

                item {
                    ProfileStatsSection(
                        xp = stats?.totalXp ?: 0,
                        streak = stats?.currentStreak ?: 0,
                        achievementsCount = achievements.size
                    )
                    Spacer(Modifier.height(28.dp))
                }

                item {
                    PremiumBannerCard(onNavigateToPremium)
                    Spacer(Modifier.height(16.dp))
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileQuickButton(
                            label   = "Logros",
                            icon    = Icons.Default.EmojiEvents,
                            onClick = onNavigateToAchievements,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileQuickButton(
                            label   = "Ranking",
                            icon    = Icons.Default.Leaderboard,
                            onClick = onNavigateToLeaderboard,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileQuickButton(
                            label   = "Diplomas",
                            icon    = Icons.Default.Verified,
                            onClick = onNavigateToCertificates,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    SectionHeader("Información Académica")
                    InfoRow(icon = Icons.Default.School, label = "Institución", value = "UTE Universidad")
                    InfoRow(icon = Icons.Default.Book, label = "Carrera", value = "Idiomas Internacionales")
                    InfoRow(icon = Icons.Default.CalendarToday, label = "Miembro desde", value = "Enero 2024")
                    Spacer(Modifier.height(24.dp))
                }

                if (achievements.isNotEmpty()) {
                    item {
                        Text(
                            text = "Mis logros obtenidos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    items(achievementRows) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            row.forEach { userAchievement ->
                                AchievementCard(
                                    achievement = Achievement(
                                        id = userAchievement.id.toString(),
                                        title = userAchievement.title.orEmpty(),
                                        description = userAchievement.description.orEmpty(),
                                        icon = userAchievement.icon.orEmpty().ifBlank { "🏆" },
                                        isUnlocked = true
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Aún no tienes logros. ¡Sigue aprendiendo!", color = TextSecondary)
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(36.dp))
                    LogoutButton(onLogout)
                }
            }
        }
    }
}

@Composable
private fun ProfileTopBar(onNavigateToSettings: () -> Unit) {
    val borderColor = Border
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = borderColor.copy(alpha = 0.3f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            },
        color = SurfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                ),
                color = TextPrimary
            )
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .size(40.dp)
                    .background(PageBg, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    modifier = Modifier.size(18.dp),
                    tint = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ProfileHeroSection(username: String?, email: String?, level: String) {
    val gradientColors = listOf(SurfaceColor, BackgroundColor)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(gradientColors))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .border(2.5.dp, Brush.linearGradient(listOf(PrimaryBlue, AccentBlue)), CircleShape)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(PrimaryBlue, DarkBlue))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username?.take(1)?.uppercase() ?: "U",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = username ?: "Usuario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            if (!email.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(14.dp))

            Surface(
                color = PrimaryBlue.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.15f))
            ) {
                Text(
                    text = "Nivel $level",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsSection(xp: Int, streak: Int, achievementsCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileStatCard(
            value = xp.toString(),
            label = "XP Total",
            icon = Icons.Default.Bolt,
            iconColor = Warning,
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            value = streak.toString(),
            label = "Días racha",
            icon = Icons.Default.LocalFireDepartment,
            iconColor = PrimaryRed,
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            value = achievementsCount.toString(),
            label = "Logros",
            icon = Icons.Default.EmojiEvents,
            iconColor = PrimaryBlue,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileStatCard(
    value: String,
    label: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor,
        border = BorderStroke(1.dp, Border.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PremiumBannerCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(2.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(HeroGradient))
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "JumpUp Premium",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Acceso ilimitado sin anuncios",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Ver",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement, modifier: Modifier = Modifier) {
    val unlocked = achievement.isUnlocked

    Surface(
        color = if (unlocked) SurfaceColor else PageBg.copy(alpha = 0.7f),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (unlocked) Border.copy(alpha = 0.4f) else Color.Transparent
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (unlocked) PrimaryBlue.copy(alpha = 0.06f)
                        else Border.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val iconValue = achievement.icon
                if (iconValue.startsWith("http://") || iconValue.startsWith("https://")) {
                    AsyncImage(
                        model = iconValue,
                        contentDescription = "Logro: ${achievement.title}",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .alpha(if (unlocked) 1f else 0.3f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = iconValue.ifBlank { "🏆" },
                        fontSize = 24.sp,
                        modifier = Modifier.alpha(if (unlocked) 1f else 0.3f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = achievement.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = if (unlocked) TextPrimary else TextSecondary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.labelSmall,
                color = if (unlocked) TextSecondary else TextTertiary.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )

            if (unlocked) {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Success.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Desbloqueado",
                        fontSize = 9.sp,
                        color = Success,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Black,
        color = TextPrimary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(PrimaryBlue.copy(alpha = 0.06f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    OutlinedButton(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryRed),
        border = BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.25f))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = PrimaryRed
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Cerrar sesión",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ProfileQuickButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick  = onClick,
        modifier = modifier,
        shape    = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        color    = LightBlue
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
        }
    }
}