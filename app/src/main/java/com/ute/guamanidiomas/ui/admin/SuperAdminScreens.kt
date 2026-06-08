package com.ute.guamanidiomas.ui.admin

import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.data.remote.dto.AuditLogDto
import com.ute.guamanidiomas.data.remote.dto.OrderDto
import com.ute.guamanidiomas.data.remote.dto.RoleDto
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.SuperAdminViewModel

private val AdminScreenBlue = AccentBlue
private val AdminScreenGreen = Success
private val AdminScreenYellow = Warning
private val AdminScreenRed = PrimaryRed

private val PermissionOptions = listOf(
    "courses.add_course" to "Crear cursos",
    "courses.change_course" to "Editar cursos",
    "courses.delete_course" to "Eliminar cursos",
    "orders.change_order" to "Aprobar inscripciones",
    "auth.change_user" to "Gestionar personal",
    "admin.view_logentry" to "Ver bitacora"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesManagementScreen(
    onBack: () -> Unit,
    viewModel: SuperAdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showCreateRole by remember { mutableStateOf(false) }
    var showAssignRole by remember { mutableStateOf(false) }
    var roleToAssign by remember { mutableStateOf<RoleDto?>(null) }

    LaunchedEffect(Unit) { viewModel.loadRoles() }

    AdminScaffold(
        title = "Roles y Permisos",
        onBack = onBack,
        action = {
            IconButton(onClick = { showCreateRole = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear rol", tint = AccentBlue)
            }
        }
    ) {
        AdminStateBody(
            isLoading = state.isLoading,
            error = state.error,
            onRetry = viewModel::loadRoles
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.roles, key = { it.id }) { role ->
                    RoleCard(role = role, onAssign = {
                        roleToAssign = it
                        showAssignRole = true
                    })
                }
            }
        }
    }

    if (showCreateRole) {
        CreateRoleSheet(
            isSaving = state.isSaving,
            onDismiss = { showCreateRole = false },
            onCreate = { name, permissions ->
                viewModel.createRole(name, permissions)
                showCreateRole = false
            }
        )
    }

    if (showAssignRole && roleToAssign != null) {
        AssignRoleSheet(
            role = roleToAssign!!,
            isSaving = state.isSaving,
            onDismiss = {
                showAssignRole = false
                roleToAssign = null
            },
            onAssign = { userId ->
                viewModel.assignRoleToUser(userId, roleToAssign!!.name)
                showAssignRole = false
                roleToAssign = null
            }
        )
    }
}

@Composable
private fun RoleCard(role: RoleDto, onAssign: (RoleDto) -> Unit) {
    AdminCardSurface {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentBlue.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = AccentBlue)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(role.name, color = AdminText, fontWeight = FontWeight.Bold)
                Text(
                    "${role.permissions.size} permisos asignados",
                    color = AdminMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TextButton(onClick = { onAssign(role) }) {
                Text("Asignar", color = AccentBlue)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRoleSheet(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf<String>()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AdminCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Nuevo rol", color = AdminText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del rol") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            PermissionOptions.forEach { (code, label) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = code in selected,
                        onCheckedChange = { checked ->
                            selected = if (checked) selected + code else selected - code
                        }
                    )
                    Text(label, color = AdminText)
                }
            }
            Button(
                onClick = { onCreate(name.trim(), selected.toList()) },
                enabled = name.isNotBlank() && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSaving) "Guardando..." else "Crear rol")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignRoleSheet(
    role: RoleDto,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAssign: (Int) -> Unit
) {
    var userIdText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AdminCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Asignar rol: ${role.name}", color = AdminText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = userIdText,
                onValueChange = { userIdText = it.filter { char -> char.isDigit() } },
                label = { Text("ID de usuario de personal") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Usa el ID del usuario para asignar este rol. También puedes crear rutas de búsqueda de personal si deseas UX más avanzada.",
                color = AdminMuted,
                style = MaterialTheme.typography.bodySmall
            )
            Button(
                onClick = { onAssign(userIdText.toIntOrNull() ?: 0) },
                enabled = userIdText.isNotBlank() && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSaving) "Asignando..." else "Asignar rol")
            }
        }
    }
}

@Composable
fun OrdersManagementScreen(
    onBack: () -> Unit,
    viewModel: SuperAdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadOrders() }

    AdminScaffold(title = "Ordenes de Venta", onBack = onBack) {
        AdminStateBody(
            isLoading = state.isLoading,
            error = state.error,
            onRetry = viewModel::loadOrders
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.orders, key = { it.id }) { order ->
                    OrderCard(order = order, onApprove = { viewModel.approveOrder(order.id) })
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderDto, onApprove: () -> Unit) {
    val normalized = order.status?.uppercase() ?: "PENDIENTE"
    val color = when (normalized) {
        "COMPLETADO", "COMPLETED", "PAID" -> Success
        "CANCELADO", "CANCELLED" -> PrimaryRed
        else -> Warning
    }
    val total = order.totalPrice ?: order.total ?: 0.0
    val courseTitle = order.courseTitle ?: order.items?.firstOrNull()?.courseTitle ?: "Curso"

    AdminCardSurface {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = color)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("#${order.id} - $courseTitle", color = AdminText, fontWeight = FontWeight.Bold)
                Text(order.userEmail ?: "Usuario #${order.userId ?: 0}", color = AdminMuted, style = MaterialTheme.typography.bodySmall)
                Text("$${"%.2f".format(Locale.US, total)}", color = AdminText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Surface(color = color.copy(alpha = 0.14f), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        normalized,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (normalized in setOf("PENDIENTE", "PENDING")) {
                TextButton(onClick = onApprove) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Aprobar")
                }
            }
        }
    }
}

@Composable
fun AuditLogScreen(
    onBack: () -> Unit,
    viewModel: SuperAdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadAuditLogs() }

    AdminScaffold(title = "Bitacora", onBack = onBack) {
        AdminStateBody(
            isLoading = state.isLoading,
            error = state.error,
            onRetry = viewModel::loadAuditLogs
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.auditLogs, key = { it.id }) { log ->
                    AuditLogItem(log)
                }
            }
        }
    }
}

@Composable
private fun AuditLogItem(log: AuditLogDto) {
    Row(Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(56.dp)
                    .background(AdminBorder)
            )
        }
        Spacer(Modifier.width(12.dp))
        AdminCardSurface(modifier = Modifier.weight(1f)) {
            Text(log.user, color = AdminText, fontWeight = FontWeight.Bold)
            Text(log.changeMessage.ifBlank { log.actionFlag }, color = AdminMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(log.actionTime, color = AdminMuted.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminScaffold(
    title: String,
    onBack: () -> Unit,
    action: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = action,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AdminBg,
                    titleContentColor = AdminText,
                    navigationIconContentColor = AdminText
                )
            )
        },
        containerColor = AdminBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            content = content
        )
    }
}

@Composable
private fun AdminStateBody(
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    when {
        isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentBlue)
        }
        error != null -> Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(error, color = PrimaryRed)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRetry) { Text("Reintentar") }
        }
        else -> content()
    }
}

@Composable
private fun AdminCardSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, AdminBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
