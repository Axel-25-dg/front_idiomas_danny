package com.ute.guamanidiomas.ui.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.ui.theme.*

// ─── Secciones del panel admin ────────────────────────────────────────────────

enum class AdminSection(
    val label: String,
    val icon: ImageVector,
    val group: String
) {
    OVERVIEW("Dashboard", Icons.Default.Dashboard, "Principal"),
    USERS("Usuarios", Icons.Default.People, "Gestión"),
    COURSES("Cursos", Icons.Default.Book, "Académico"),
    ORDERS("Órdenes", Icons.Default.ShoppingBag, "Finanzas"),
    SUBSCRIPTIONS("Suscripciones", Icons.Default.Star, "Finanzas"),
    GAMIFICATION("Gamificación", Icons.Default.EmojiEvents, "Académico"),
    ROLES("Roles", Icons.Default.Security, "Sistema"),
    AUDIT("Auditoría", Icons.Default.History, "Sistema")
}

// ─── Drawer item ──────────────────────────────────────────────────────────────

@Composable
fun AdminDrawerContent(
    currentSection: AdminSection,
    adminName: String,
    adminEmail: String,
    onSectionSelected: (AdminSection) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color(0xFF0F172A))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(PrimaryBlue, Color(0xFF1E3A8A))
                    )
                )
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    adminName.ifBlank { "Administrador" }.replaceFirstChar { it.uppercase() },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    adminEmail.ifBlank { "admin@ute.edu.ec" },
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "Super Admin",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        // Navegación agrupada
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            var lastGroup = ""
            AdminSection.entries.forEach { section ->
                if (section.group != lastGroup) {
                    lastGroup = section.group
                    Text(
                        section.group.uppercase(),
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 4.dp),
                        color = Color.White.copy(alpha = 0.35f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                DrawerNavItem(
                    section  = section,
                    selected = currentSection == section,
                    onClick  = { onSectionSelected(section) }
                )
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        // Botón cerrar sesión
        TextButton(
            onClick  = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                null,
                tint     = Color(0xFFEF4444),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text("Cerrar sesión", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DrawerNavItem(
    section: AdminSection,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor     = if (selected) PrimaryBlue.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (selected) Color.White else Color.White.copy(alpha = 0.55f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .then(
                Modifier.let { m ->
                    if (selected) m else m
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick  = onClick,
            color    = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier          = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(4.dp, 20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(AccentBlue)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Icon(
                    section.icon,
                    null,
                    tint     = if (selected) AccentBlue else contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    section.label,
                    color      = contentColor,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize   = 14.sp
                )
            }
        }
    }
}

// ─── Tarjeta de estadística del admin ────────────────────────────────────────

@Composable
fun AdminStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    subtitle: String = "",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation    = 4.dp,
                shape        = RoundedCornerShape(18.dp),
                ambientColor = color.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceColor
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            if (subtitle.isNotBlank()) {
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = color)
            }
        }
    }
}

// ─── TopBar del admin ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
    title: String,
    onMenuClick: () -> Unit,
    onRefresh: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("JumpUp UTE Academy", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, "Menú", tint = TextPrimary)
            }
        },
        actions = {
            if (onRefresh != null) {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, "Actualizar", tint = PrimaryBlue)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundColor,
            titleContentColor = TextPrimary
        )
    )
}

// ─── Estado vacío ─────────────────────────────────────────────────────────────

@Composable
fun AdminEmptyState(
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
                .size(72.dp)
                .clip(CircleShape)
                .background(LightBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
        }
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        if (action != null && onAction != null) {
            Button(
                onClick = onAction,
                colors  = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape   = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(action, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Banner de error ──────────────────────────────────────────────────────────

@Composable
fun AdminErrorBanner(message: String, onDismiss: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.08f)),
        shape  = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = ErrorColor, modifier = Modifier.weight(1f))
            if (onDismiss != null) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, tint = ErrorColor, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ─── Badge de rol ─────────────────────────────────────────────────────────────

@Composable
fun RoleBadge(role: String) {
    val (bg, fg, label) = when (role.lowercase()) {
        "admin", "superuser", "administrador" -> Triple(Color(0xFF1E40AF).copy(alpha = 0.12f), Color(0xFF1E40AF), "Admin")
        "teacher", "profesor", "docente"      -> Triple(Color(0xFF059669).copy(alpha = 0.12f), Color(0xFF059669), "Profesor")
        "student", "user", "estudiante"       -> Triple(Color(0xFF7C3AED).copy(alpha = 0.12f), Color(0xFF7C3AED), "Estudiante")
        else -> Triple(TextSecondary.copy(alpha = 0.1f), TextSecondary, role.replaceFirstChar { it.uppercase() })
    }
    Surface(color = bg, shape = RoundedCornerShape(6.dp)) {
        Text(
            label,
            modifier  = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color     = fg,
            fontSize  = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
