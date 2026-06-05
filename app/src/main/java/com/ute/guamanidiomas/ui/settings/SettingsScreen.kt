package com.ute.guamanidiomas.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { SectionHeader("Cuenta y Seguridad") }
            item {
                SettingsItem(
                    title = "Editar Perfil",
                    subtitle = "Nombre, foto y biografía",
                    icon = Icons.Default.Person,
                    iconColor = PrimaryBlue,
                    onClick = { onNavigateToEditProfile() }
                )
            }
            item {
                SettingsItem(
                    title = "Privacidad",
                    subtitle = "Control de visibilidad de datos",
                    icon = Icons.Default.Lock,
                    iconColor = Color(0xFF6366F1),
                    onClick = { /* Implementación futura */ }
                )
            }

            item { SectionHeader("Preferencias") }
            item {
                SettingsItem(
                    title = "Notificaciones",
                    subtitle = "Alertas de clases y progreso",
                    icon = Icons.Default.Notifications,
                    iconColor = AccentBlue,
                    trailing = { 
                        Switch(
                            checked = notificationsEnabled, 
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue, checkedTrackColor = LightBlue)
                        ) 
                    }
                )
            }
            item {
                SettingsItem(
                    title = "Sonido y Efectos",
                    subtitle = "Feedback auditivo en juegos",
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    iconColor = Success,
                    trailing = { 
                        Switch(
                            checked = soundEnabled, 
                            onCheckedChange = { soundEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue, checkedTrackColor = LightBlue)
                        ) 
                    }
                )
            }
            item {
                SettingsItem(
                    title = "Idioma de la App",
                    subtitle = "Español (Castellano)",
                    icon = Icons.Default.Language,
                    iconColor = Success,
                    onClick = { /* Selector de idioma */ }
                )
            }

            item { SectionHeader("Soporte") }
            item {
                SettingsItem(
                    title = "Centro de Ayuda",
                    subtitle = "Preguntas frecuentes y tutoriales",
                    icon = Icons.AutoMirrored.Filled.Help,
                    iconColor = PrimaryBlue,
                    onClick = { /* Link externo o pantalla de ayuda */ }
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
            item {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor.copy(alpha = 0.1f), contentColor = ErrorColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = null
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Black,
        color = PrimaryBlue,
        letterSpacing = 1.sp
    )
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (enabled) TextPrimary else TextTertiary
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        if (trailing != null) {
            trailing()
        } else if (enabled) {
            Icon(Icons.Default.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(20.dp))
        }
    }
}
