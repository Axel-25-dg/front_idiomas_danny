package com.ute.guamanidiomas.ui.admin.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.ute.guamanidiomas.domain.model.User
import com.ute.guamanidiomas.ui.admin.AdminMainViewModel
import com.ute.guamanidiomas.ui.admin.components.*
import com.ute.guamanidiomas.ui.theme.*

private val ROLE_FILTERS = listOf("" to "Todos", "teacher" to "Profesores", "student" to "Estudiantes", "admin" to "Admins")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersSection(viewModel: AdminMainViewModel) {
    val state by viewModel.usersState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedUser    by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) viewModel.clearUsersMessages()
    }

    if (state.isLoading && state.allUsers.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    // Calcular la lista filtrada dentro del composable
    val displayUsers = remember(state.allUsers, state.searchQuery, state.filterRole) {
        state.allUsers.filter { user ->
            val matchesSearch = state.searchQuery.isBlank() ||
                user.username.contains(state.searchQuery, ignoreCase = true) ||
                user.email.contains(state.searchQuery, ignoreCase = true) ||
                (user.firstName + " " + user.lastName).contains(state.searchQuery, ignoreCase = true)
            val matchesRole = state.filterRole.isBlank() ||
                user.role?.lowercase() == state.filterRole.lowercase()
            matchesSearch && matchesRole
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // Barra de búsqueda
            OutlinedTextField(
                value         = state.searchQuery,
                onValueChange = { viewModel.setUserSearchQuery(it) },
                label         = { Text("Buscar usuario…") },
                leadingIcon   = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                singleLine    = true,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape         = RoundedCornerShape(12.dp)
            )

            // Filtros por rol
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ROLE_FILTERS) { (roleKey, label) ->
                    FilterChip(
                        selected = state.filterRole == roleKey,
                        onClick  = { viewModel.setUserRoleFilter(roleKey) },
                        label    = { Text(label) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor  = PrimaryBlue,
                            selectedLabelColor      = androidx.compose.ui.graphics.Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            state.error?.let { err ->
                AdminErrorBanner(
                    message   = err,
                    onDismiss = { viewModel.clearUsersMessages() },
                )
            }

            if (displayUsers.isEmpty() && !state.isLoading) {
                AdminEmptyState(
                    icon     = Icons.Default.People,
                    title    = "Sin usuarios",
                    subtitle = "No hay usuarios que coincidan con los filtros",
                    action   = "Crear usuario",
                    onAction = { showCreateDialog = true }
                )
            } else {
                // Resumen
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.People, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${displayUsers.size} usuarios",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayUsers, key = { it.id }) { user ->
                        UserAdminCard(
                            user          = user,
                            onToggleActive = { viewModel.toggleUserActive(user.id, user.isActive) },
                            onChangeRole  = { role -> viewModel.changeUserRole(user.id, role) },
                            onViewProfile = { selectedUser = user }
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick        = { showCreateDialog = true },
            modifier       = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            containerColor = PrimaryBlue,
            contentColor   = androidx.compose.ui.graphics.Color.White
        ) {
            Icon(Icons.Default.PersonAdd, "Crear usuario")
        }
    }

    // Diálogo crear usuario
    if (showCreateDialog) {
        CreateUserDialog(
            isSubmitting = state.isSubmitting,
            error        = state.error,
            onDismiss    = { showCreateDialog = false },
            onConfirm    = { username, email, firstName, lastName, role, pass ->
                viewModel.createUser(username, email, firstName, lastName, role, pass)
                showCreateDialog = false
            }
        )
    }

    // Detalle usuario
    selectedUser?.let { user ->
        UserDetailSheet(
            user      = user,
            onDismiss = { selectedUser = null }
        )
    }
}

@Composable
private fun UserAdminCard(
    user: User,
    onToggleActive: () -> Unit,
    onChangeRole: (String) -> Unit,
    onViewProfile: () -> Unit
) {
    val fullName = listOf(user.firstName, user.lastName).filter { it.isNotBlank() }.joinToString(" ")
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        color    = SurfaceColor
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    (user.username.firstOrNull() ?: user.email.firstOrNull() ?: '?').uppercase(),
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PrimaryBlue
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        fullName.ifBlank { user.username },
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        color      = TextPrimary,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f, fill = false)
                    )
                    RoleBadge(role = user.role ?: "user")
                }
                Text(
                    user.email,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    if (user.isActive) "● Activo" else "○ Inactivo",
                    style  = MaterialTheme.typography.labelSmall,
                    color  = if (user.isActive) Success else ErrorColor
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Cambiar rol
                Box {
                    IconButton(onClick = { showRoleMenu = true }, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.ManageAccounts, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = showRoleMenu, onDismissRequest = { showRoleMenu = false }) {
                        listOf("student", "teacher", "admin").forEach { role ->
                            DropdownMenuItem(
                                text    = { Text(role.replaceFirstChar { it.uppercase() }) },
                                onClick = { onChangeRole(role); showRoleMenu = false }
                            )
                        }
                    }
                }
                // Activar / Desactivar
                Switch(
                    checked       = user.isActive,
                    onCheckedChange = { onToggleActive() },
                    modifier       = Modifier.size(36.dp, 24.dp),
                    colors         = SwitchDefaults.colors(
                        checkedTrackColor = Success,
                        uncheckedTrackColor = ErrorColor.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateUserDialog(
    isSubmitting: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var username  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var role      by remember { mutableStateOf("student") }
    var expanded  by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Usuario", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                error?.let { Text(it, color = ErrorColor, style = MaterialTheme.typography.bodySmall) }

                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value        = role.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly     = true,
                        label        = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier     = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("student", "teacher", "admin").forEach { r ->
                            DropdownMenuItem(
                                text    = { Text(r.replaceFirstChar { it.uppercase() }) },
                                onClick = { role = r; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = {
                    if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        onConfirm(username.trim(), email.trim(), firstName.trim(), lastName.trim(), role, password)
                    }
                },
                enabled  = !isSubmitting && username.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isSubmitting) CircularProgressIndicator(Modifier.size(16.dp), color = androidx.compose.ui.graphics.Color.White, strokeWidth = 2.dp)
                else Text("Crear")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserDetailSheet(user: User, onDismiss: () -> Unit) {
    val fullName = listOf(user.firstName, user.lastName).filter { it.isNotBlank() }.joinToString(" ")
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = SurfaceColor) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user.username.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(fullName.ifBlank { user.username }, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            HorizontalDivider()
            UserDetailRow("ID", user.id.toString())
            UserDetailRow("Usuario", "@${user.username}")
            UserDetailRow("Correo", user.email)
            UserDetailRow("Rol", user.role ?: "—")
            UserDetailRow("Estado", if (user.isActive) "Activo" else "Inactivo")
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UserDetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        Text(value, color = TextPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
    }
}
