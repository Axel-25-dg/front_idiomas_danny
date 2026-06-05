package com.ute.guamanidiomas.ui.admin.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.data.remote.dto.RoleDto
import com.ute.guamanidiomas.ui.admin.AdminMainViewModel
import com.ute.guamanidiomas.ui.admin.components.*
import com.ute.guamanidiomas.ui.theme.*
import java.util.Locale

// ─── Suscripciones ────────────────────────────────────────────────────────────

@Composable
fun AdminSubscriptionsSection(viewModel: AdminMainViewModel) {
    val state by viewModel.subscriptionsState.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        state.error?.let { err ->
            item { AdminErrorBanner(message = err) }
        }

        item {
            Text("Resumen de Suscripciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard(
                        label    = "Ingresos por suscripciones",
                        value    = "$ ${String.format(Locale.getDefault(), "%.2f", state.totalRevenue)}",
                        icon     = Icons.Default.MonetizationOn,
                        color    = Success,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard(
                        label    = "Total pagos",
                        value    = state.totalSubscriptions.toString(),
                        icon     = Icons.Default.Payment,
                        color    = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        label    = "Suscrip. activas",
                        value    = state.activeSubscriptions.toString(),
                        icon     = Icons.Default.Star,
                        color    = Warning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color    = LightBlue,
                shape    = RoundedCornerShape(14.dp)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Los planes de suscripción y pagos detallados están disponibles en el backend en /api/subscriptions/ y /api/payments/.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

// ─── Gamificación ─────────────────────────────────────────────────────────────

@Composable
fun AdminGamificationSection() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Gamificación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color    = LightBlue,
                shape    = RoundedCornerShape(14.dp)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.EmojiEvents, null, tint = Warning, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Gestión de Logros y XP", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            "Los logros, progreso y estadísticas de gamificación se gestionan a través de los endpoints /api/achievements/ y /api/stats/.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminStatCard(
                    label    = "Sistema XP",
                    value    = "Activo",
                    icon     = Icons.Default.Star,
                    color    = Warning,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    label    = "Logros",
                    value    = "API",
                    icon     = Icons.Default.EmojiEvents,
                    color    = Success,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─── Roles ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRolesSection(viewModel: AdminMainViewModel) {
    val state by viewModel.rolesState.collectAsState()
    var showCreateRole by remember { mutableStateOf(false) }
    var roleToAssign   by remember { mutableStateOf<RoleDto?>(null) }

    if (state.isLoading && state.roles.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        if (state.roles.isEmpty() && !state.isLoading) {
            AdminEmptyState(
                icon     = Icons.Default.Security,
                title    = "Sin roles",
                subtitle = "Crea roles personalizados para el sistema",
                action   = "Crear rol",
                onAction = { showCreateRole = true }
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.error?.let { err ->
                    item { AdminErrorBanner(message = err) }
                }

                items(state.roles, key = { it.id }) { role ->
                    RoleAdminCard(
                        role     = role,
                        onAssign = { roleToAssign = role }
                    )
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }

        FloatingActionButton(
            onClick        = { showCreateRole = true },
            modifier       = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = PrimaryBlue,
            contentColor   = androidx.compose.ui.graphics.Color.White
        ) {
            Icon(Icons.Default.Add, "Crear rol")
        }
    }

    if (showCreateRole) {
        CreateRoleAdminDialog(
            isSaving  = state.isSaving,
            onDismiss = { showCreateRole = false },
            onCreate  = { name, perms ->
                viewModel.createRole(name, perms)
                showCreateRole = false
            }
        )
    }

    roleToAssign?.let { role ->
        AssignRoleAdminDialog(
            role      = role,
            isSaving  = state.isSaving,
            onDismiss = { roleToAssign = null },
            onAssign  = { userId ->
                viewModel.assignRoleToUser(userId, role.name)
                roleToAssign = null
            }
        )
    }
}

@Composable
private fun RoleAdminCard(role: RoleDto, onAssign: (RoleDto) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        color    = SurfaceColor
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Security, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(role.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("${role.permissions.size} permisos", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            TextButton(onClick = { onAssign(role) }) {
                Text("Asignar", color = PrimaryBlue)
            }
        }
    }
}

@Composable
private fun CreateRoleAdminDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    var name     by remember { mutableStateOf("") }
    val permsMap = listOf(
        "courses.add_course"     to "Crear cursos",
        "courses.change_course"  to "Editar cursos",
        "courses.delete_course"  to "Eliminar cursos",
        "orders.change_order"    to "Aprobar órdenes",
        "auth.change_user"       to "Gestionar usuarios",
        "admin.view_logentry"    to "Ver auditoría"
    )
    val selected = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Rol", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del rol") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Text("Permisos:", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                permsMap.forEach { (code, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked         = code in selected,
                            onCheckedChange = { if (it) selected.add(code) else selected.remove(code) }
                        )
                        Text(label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = { onCreate(name.trim(), selected.toList()) },
                enabled  = name.isNotBlank() && !isSaving,
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun AssignRoleAdminDialog(
    role: RoleDto,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAssign: (Int) -> Unit
) {
    var userIdText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Asignar: ${role.name}", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value         = userIdText,
                onValueChange = { userIdText = it.filter { c -> c.isDigit() } },
                label         = { Text("ID del usuario") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick  = { onAssign(userIdText.toIntOrNull() ?: 0) },
                enabled  = userIdText.isNotBlank() && !isSaving,
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) { Text("Asignar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// ─── Auditoría ────────────────────────────────────────────────────────────────

@Composable
fun AdminAuditSection(viewModel: AdminMainViewModel) {
    val state by viewModel.rolesState.collectAsState()

    if (state.isLoading && state.auditLogs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    if (state.auditLogs.isEmpty() && !state.isLoading) {
        AdminEmptyState(
            icon     = Icons.Default.History,
            title    = "Sin registros",
            subtitle = "No hay actividad registrada en el sistema"
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        state.error?.let { err ->
            item { AdminErrorBanner(message = err) }
        }

        item {
            Text("${state.auditLogs.size} registros", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        items(state.auditLogs, key = { it.id }) { log ->
            Surface(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(14.dp)),
                shape    = RoundedCornerShape(14.dp),
                color    = SurfaceColor
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(AccentBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.History, null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(log.user, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                        Text(
                            log.changeMessage.ifBlank { log.actionFlag },
                            style    = MaterialTheme.typography.bodySmall,
                            color    = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        log.actionTime.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
