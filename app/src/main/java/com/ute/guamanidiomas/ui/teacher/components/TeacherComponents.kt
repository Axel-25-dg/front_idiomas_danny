package com.ute.guamanidiomas.ui.teacher.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.R
import com.ute.guamanidiomas.ui.theme.*

enum class TeacherSection(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME("Inicio", Icons.Default.Home, Icons.Default.Home),
    CLASSES("Clases", Icons.Default.Class, Icons.Default.Class),
    EXAMS("Lecciones", Icons.Default.Quiz, Icons.Default.Quiz),
    RESOURCES("Recursos", Icons.Default.Folder, Icons.Default.Folder),
    STUDENTS("Estudiantes", Icons.Default.Group, Icons.Default.Group)
}

@Composable
fun TeacherBottomNav(
    currentSection: TeacherSection,
    onSectionSelected: (TeacherSection) -> Unit
) {
    Surface(
        modifier    = Modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color       = SurfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            TeacherSection.entries.forEach { section ->
                TeacherNavItem(
                    section  = section,
                    selected = currentSection == section,
                    onClick  = { onSectionSelected(section) }
                )
            }
        }
    }
}

@Composable
private fun TeacherNavItem(
    section: TeacherSection,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (selected) PrimaryBlue else TextTertiary,
        animationSpec = tween(200),
        label = "nav_color"
    )

    Column(
        modifier              = Modifier
            .clip(RoundedCornerShape(12.dp))
            .let {
                if (selected) it.background(LightBlue.copy(alpha = 0.6f)) else it
            }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(2.dp)
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = if (selected) section.selectedIcon else section.icon,
                contentDescription = section.label,
                tint   = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text     = section.label,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color    = color
        )
    }
}

@Composable
fun TeacherTopBar(
    title: String,
    subtitle: String = "UTE ACADEMY",
    onLogout: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        color           = Color.Transparent,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.horizontalGradient(BlueGradient))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        colors  = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.jumpup_logo),
                            contentDescription = "JumpUp Logo",
                            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        color      = Color.White,
                        fontSize   = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines   = 1
                    )
                    Text(
                        subtitle,
                        color      = Color.White.copy(alpha = 0.75f),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
                Row(content = actions)
                if (onLogout != null) {
                    IconButton(
                        onClick = onLogout,
                        colors  = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.12f)
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation    = 6.dp,
                shape        = RoundedCornerShape(20.dp),
                ambientColor = color.copy(alpha = 0.08f),
                spotColor    = color.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceColor
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(LightBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint     = PrimaryBlue,
                modifier = Modifier.size(40.dp)
            )
        }
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        if (action != null && onAction != null) {
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onAction,
                colors  = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape   = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(action, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun LevelBadge(level: String, modifier: Modifier = Modifier) {
    val color = when (level.uppercase()) {
        "A1" -> Color(0xFF10B981)
        "A2" -> Color(0xFF3B82F6)
        "B1" -> Color(0xFF8B5CF6)
        "B2" -> Color(0xFFF59E0B)
        "C1" -> Color(0xFFEF4444)
        "C2" -> Color(0xFF1E40AF)
        else -> TextSecondary
    }
    Surface(
        modifier = modifier,
        color    = color.copy(alpha = 0.12f),
        shape    = RoundedCornerShape(6.dp)
    ) {
        Text(
            level.ifBlank { "N/A" },
            modifier  = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color     = color,
            fontSize  = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeacherPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add
) {
    Button(
        onClick   = onClick,
        modifier  = modifier
            .height(52.dp)
            .shadow(
                elevation    = 8.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = PrimaryBlue.copy(alpha = 0.2f)
            ),
        shape  = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}