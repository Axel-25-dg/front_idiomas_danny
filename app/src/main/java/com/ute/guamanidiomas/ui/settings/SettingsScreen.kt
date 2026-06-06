package com.ute.guamanidiomas.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var showAbout by remember { mutableStateOf(false) }

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
            item { SectionHeader("Cuenta") }
            item {
                SettingsItem(
                    title = "Editar Perfil",
                    subtitle = "Nombre, foto y biografía",
                    icon = Icons.Default.Person,
                    iconColor = PrimaryBlue,
                    onClick = onNavigateToEditProfile
                )
            }

            item { SectionHeader("Apariencia") }
            item {
                SettingsItem(
                    title = "Tema Oscuro",
                    subtitle = if (isDarkMode) "Activado" else "Desactivado",
                    icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    iconColor = if (isDarkMode) Color(0xFF7C3AED) else Warning,
                    trailing = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { settingsViewModel.toggleDarkMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PrimaryBlue,
                                checkedTrackColor = LightBlue
                            )
                        )
                    }
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
                    onClick = { /* El idioma sigue la configuración del sistema */ }
                )
            }

            item { SectionHeader("Información") }
            item {
                SettingsItem(
                    title = "Centro de Ayuda",
                    subtitle = "Preguntas frecuentes",
                    icon = Icons.AutoMirrored.Filled.Help,
                    iconColor = PrimaryBlue,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://guaman-idiomas-ute.online"))
                        context.startActivity(intent)
                    }
                )
            }
            item {
                SettingsItem(
                    title = "Acerca de",
                    subtitle = "JumpUp UTE v1.0 • Plataforma de Idiomas",
                    icon = Icons.Default.Info,
                    iconColor = TextSecondary,
                    onClick = { showAbout = true }
                )
            }
            item {
                SettingsItem(
                    title = "Términos y Condiciones",
                    subtitle = "Política de uso y privacidad",
                    icon = Icons.Default.Description,
                    iconColor = TextSecondary,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://guaman-idiomas-ute.online/terms"))
                        context.startActivity(intent)
                    }
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorColor.copy(alpha = 0.1f),
                        contentColor = ErrorColor
                    ),
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

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            icon = { Icon(Icons.Default.School, null, tint = PrimaryBlue) },
            title = { Text("JumpUp UTE", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Plataforma de aprendizaje de inglés\n\n" +
                    "Versión: 1.0\n" +
                    "Desarrollador: Danny Guamán\n" +
                    "Universidad: UTE\n\n" +
                    "Stack: Kotlin + Jetpack Compose + Django REST\n" +
                    "18 APIs • 6 Juegos • 3 Roles"
                )
            },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text("Cerrar", color = PrimaryBlue)
                }
            }
        )
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
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (enabled) TextPrimary else TextTertiary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        if (trailing != null) {
            trailing()
        } else if (enabled) {
            Icon(Icons.Default.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(20.dp))
        }
    }
}
