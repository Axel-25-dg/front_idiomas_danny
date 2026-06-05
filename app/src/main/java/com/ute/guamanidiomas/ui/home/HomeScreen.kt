package com.ute.guamanidiomas.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.HomeViewModel
import com.ute.guamanidiomas.ui.viewmodel.CartViewModel
import com.ute.guamanidiomas.ui.home.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onCourseClick: (Int) -> Unit = {},
    onCatalogClick: () -> Unit = {},
    onJoinClassClick: () -> Unit = {},
    onIATutorClick: () -> Unit = {},
    onOpenCart: () -> Unit = {},
    onMyClassesClick: () -> Unit = {},
    onAchievementsClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onCertificatesClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartCount by cartViewModel.totalItems.collectAsState()

    // Recargar datos al entrar para asegurar que las stats estén actualizadas (juegos, etc)
    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    val featuredCourses = remember(uiState.courses) { uiState.courses.take(6) }
    val completedModules = remember(uiState.lessonProgressList) {
        uiState.lessonProgressList.count { it.status == "completed" }
    }

    Scaffold(
        containerColor = BackgroundColor,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBar(
                cartCount = cartCount,
                onOpenCart = onOpenCart,
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.courses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
            }
        } else {
            // Fondo degradado orgánico de ambiente para romper la monotonía azul/blanca plana
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PrimaryRed.copy(alpha = 0.03f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = 700f
                        )
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Barra de Estadísticas Estilo Bento Moderno
                    item {
                        HomeStatsBar(
                            streak = uiState.stats?.currentStreak ?: 0,
                            xp = uiState.stats?.totalXp ?: 0,
                            achievements = uiState.unlockedAchievementsCount
                        )
                    }

                    // Banner Destacado / Hero
                    item {
                        HeroBannerCard(
                            onCatalogClick = onCatalogClick,
                            onJoinClassClick = onJoinClassClick,
                            onIATutorClick = onIATutorClick
                        )
                    }

                    // Sección: Explorar Idiomas
                    if (uiState.languages.isNotEmpty()) {
                        item {
                            HomeSectionHeader(
                                title = "Explorar idiomas",
                                subtitle = "${uiState.languages.size} disponibles",
                                onSeeAll = onCatalogClick
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.languages, key = { it.name }) { lang ->
                                    LanguageCard(
                                        name = lang.name,
                                        count = lang.totalCourses,
                                        onClick = onCatalogClick
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    // ── Accesos rápidos del estudiante ────────────────────────────────
                    item {
                        HomeSectionHeader(
                            title    = "Mi academia",
                            subtitle = "Acceso rápido",
                            onSeeAll = {}
                        )
                    }
                    item {
                        StudentQuickAccessRow(
                            onMyClasses     = onMyClassesClick,
                            onAchievements  = onAchievementsClick,
                            onLeaderboard   = onLeaderboardClick,
                            onCertificates  = onCertificatesClick
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    // Sección: Cursos Destacados
                    if (featuredCourses.isNotEmpty()) {
                        item {
                            HomeSectionHeader(
                                title = "Cursos destacados",
                                subtitle = "Los más populares ahora",
                                onSeeAll = onCatalogClick
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(featuredCourses, key = { it.id }) { course ->
                                    ModernCourseCard(
                                        course = course,
                                        onClick = { onCourseClick(course.id) },
                                        modifier = Modifier.width(240.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    // Nueva Sección: Tareas del Profesor
                    if (uiState.teacherTasks.isNotEmpty()) {
                        item {
                            HomeSectionHeader(
                                title = "Tareas del Profesor",
                                subtitle = "Pendientes por revisar",
                                onSeeAll = {}
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.teacherTasks, key = { it.id }) { task ->
                                    TeacherTaskCard(task = task)
                                }
                            }
                        }
                    }

                    // Tarjeta de Progreso General Rediseñada
                    item {
                        ProgressCard(
                            username = uiState.userName,
                            xp = uiState.stats?.totalXp ?: 0,
                            streak = uiState.stats?.currentStreak ?: 0,
                            modulesCompleted = completedModules
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(cartCount: Int, onOpenCart: () -> Unit, onLogout: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = Border.copy(alpha = 0.3f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        color = SurfaceColor,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(PrimaryBlue, DarkBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "J",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "JumpUp",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.3).sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "UTE ACADEMY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = PrimaryRed
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    IconButton(
                        onClick = onOpenCart,
                        modifier = Modifier
                            .size(40.dp)
                            .background(PageBg, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 1.dp, y = (-1).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(PrimaryRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (cartCount > 9) "9+" else cartCount.toString(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(40.dp)
                        .background(PageBg, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Salir",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeStatsBar(streak: Int, xp: Int, achievements: Int) {
    // Diseño Bento Box integrado sin bordes duros artificiales
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatBentoBox(
            icon = Icons.Default.LocalFireDepartment,
            color = PrimaryRed,
            value = "$streak",
            label = "Racha",
            modifier = Modifier.weight(1f)
        )
        StatBentoBox(
            icon = Icons.Default.Bolt,
            color = Warning,
            value = "$xp",
            label = "XP total",
            modifier = Modifier.weight(1f)
        )
        StatBentoBox(
            icon = Icons.Default.EmojiEvents,
            color = Success, // Cambiado a Success (Verde/Esmeralda) para romper el monocromo azul
            value = "$achievements",
            label = "Logros",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatBentoBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor,
        border = BorderStroke(1.dp, Border.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun HeroBannerCard(
    onCatalogClick: () -> Unit,
    onJoinClassClick: () -> Unit,
    onIATutorClick: () -> Unit
) {
    
    val darkPremiumGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1E222B), Color(0xFF11141A))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
            .shadow(2.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(darkPremiumGradient)
    ) {
        // Decoraciones fluidas usando rojo energético para máxima personalidad visual
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(PrimaryRed.copy(alpha = 0.18f))
        )
        Box(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = 20.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.15f))
        )

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(PrimaryRed)
                    )
                    Text(
                        text = "APRENDE SIN LÍMITES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp
                    )
                }

                IconButton(
                    onClick = onIATutorClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "IA Tutor",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Domina idiomas\ncon confianza",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-0.5).sp,
                lineHeight = 30.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "120+ lecciones  •  Retos diarios  •  Garantía UTE",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onCatalogClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Ver catálogo",
                        color = Color(0xFF11141A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Button(
                    onClick = onJoinClassClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Unirme a clase",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSectionHeader(title: String, subtitle: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = (-0.3).sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
        TextButton(
            onClick = onSeeAll,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Ver todo",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
    }
}

@Composable
private fun TeacherTaskCard(task: TeacherTask) {
    Surface(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor,
        border = BorderStroke(1.dp, Border.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (task.priority == "Alta") PrimaryRed.copy(alpha = 0.1f)
                            else PrimaryBlue.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = task.priority,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (task.priority == "Alta") PrimaryRed else PrimaryBlue
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = task.title,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = task.description,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Vence en",
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = task.dueDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Button(
                    onClick = { /* TODO: Abrir tarea */ },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Abrir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(username: String, xp: Int, streak: Int, modulesCompleted: Int) {
    val progressValue = remember(modulesCompleted) { (modulesCompleted / 10f).coerceIn(0f, 1f) }
    val percent = remember(progressValue) { (progressValue * 100).toInt() }

    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp)) {
        Text(
            text = "Tu progreso",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Spacer(Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceColor,
            border = BorderStroke(1.dp, Border.copy(alpha = 0.4f))
        ) {
            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Warning.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = Warning,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (username.isNotBlank()) "¡Hola, $username!" else "Continuar aprendiendo",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$modulesCompleted módulos completados",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Text(
                            text = "$percent%",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Línea de Progreso con Extremos Redondeados Limpios
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(PageBg)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressValue)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Brush.horizontalGradient(listOf(PrimaryBlue, AccentBlue)))
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Estadísticas secundarias limpias sin exceso de chips de colores pesados
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Bolt, null, tint = Warning, modifier = Modifier.size(16.dp))
                            Text("$xp XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.LocalFireDepartment, null, tint = PrimaryRed, modifier = Modifier.size(16.dp))
                            Text("$streak días", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.MilitaryTech, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Text("$modulesCompleted módulos", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentQuickAccessRow(
    onMyClasses: () -> Unit,
    onAchievements: () -> Unit,
    onLeaderboard: () -> Unit,
    onCertificates: () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickAccessCard(
            label   = "Mis Clases",
            icon    = Icons.Default.Class,
            color   = PrimaryBlue,
            onClick = onMyClasses,
            modifier = Modifier.weight(1f)
        )
        QuickAccessCard(
            label   = "Logros",
            icon    = Icons.Default.EmojiEvents,
            color   = Warning,
            onClick = onAchievements,
            modifier = Modifier.weight(1f)
        )
        QuickAccessCard(
            label   = "Ranking",
            icon    = Icons.Default.Leaderboard,
            color   = Success,
            onClick = onLeaderboard,
            modifier = Modifier.weight(1f)
        )
        QuickAccessCard(
            label   = "Diplomas",
            icon    = Icons.Default.Verified,
            color   = Color(0xFF8B5CF6),
            onClick = onCertificates,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickAccessCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick  = onClick,
        modifier = modifier.shadow(3.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        color    = SurfaceColor,
        border   = BorderStroke(1.dp, Border.copy(alpha = 0.3f))
    ) {
        Column(
            modifier            = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(
                label,
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextSecondary,
                maxLines   = 1
            )
        }
    }
}
