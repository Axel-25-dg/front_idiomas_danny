package com.ute.guamanidiomas.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.domain.model.User
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.UsersAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBack: () -> Unit,
    viewModel: UsersAdminViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    val darkBackground = AdminBg
    val cardBackground = AdminCard
    val primaryColor = AccentBlue
    val textColor = AdminText
    val textMutedColor = AdminMuted

    // Reset submit success and refresh list if needed
    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            showSheet = false
            viewModel.resetSubmitSuccess()
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = darkBackground,
            surface = cardBackground,
            primary = primaryColor,
            onBackground = textColor,
            onSurface = textColor
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Personal", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = textColor
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Registrar Personal",
                                tint = primaryColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = darkBackground,
                        titleContentColor = textColor
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showSheet = true },
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Personal")
                }
            },
            containerColor = darkBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryColor
                    )
                } else if (state.error != null && state.users.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadStaffUsers() },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Text("Reintentar")
                        }
                    }
                } else if (state.users.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Sin personal",
                            tint = textMutedColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay personal registrado en el sistema.",
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Los estudiantes se registran de manera pública.",
                            style = MaterialTheme.typography.bodySmall,
                            color = textMutedColor,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Lista de Personal (${state.users.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textMutedColor,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(state.users, key = { it.id }) { user ->
                            StaffUserRow(
                                user = user,
                                onActiveToggle = { viewModel.toggleUserActive(user.id, user.isActive) }
                            )
                        }
                    }
                }
            }

            if (showSheet) {
                UserFormSheet(
                    onDismiss = { showSheet = false },
                    onSubmit = { username, email, firstName, lastName, role, password ->
                        viewModel.createUser(username, email, firstName, lastName, role, password)
                    },
                    isSubmitting = state.isSubmitting,
                    errorMessage = state.error
                )
            }
        }
    }
}

@Composable
fun StaffUserRow(
    user: User,
    onActiveToggle: () -> Unit
) {
    // Determine Badge colors depending on role
    // Teacher: Blue, Manager: Purple, Employee: Orange
    val (badgeBg, badgeText, roleLabel) = when (user.role?.lowercase() ?: "user") {
        "teacher", "profesor" -> Triple(Color(0xFF3B82F6).copy(alpha = 0.15f), Color(0xFF60A5FA), "Profesor")
        "manager", "jefe" -> Triple(Color(0xFF8B5CF6).copy(alpha = 0.15f), Color(0xFFA78BFA), "Jefe")
        "employee", "empleado" -> Triple(Color(0xFFF59E0B).copy(alpha = 0.15f), Color(0xFFFBBF24), "Empleado")
        else -> Triple(Color(0xFF64748B).copy(alpha = 0.15f), Color(0xFF94A3B8), (user.role ?: "Usuario").replaceFirstChar { it.uppercase() })
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val fullName = listOf(user.firstName, user.lastName).filter { !it.isNullOrBlank() }.joinToString(" ")
                    Text(
                        text = fullName.ifBlank { user.username.orEmpty() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                    
                    // Badge de Rol
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = roleLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = badgeText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "@${user.username.orEmpty()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
                
                Text(
                    text = user.email.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
            
            // Switch de Activación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (user.isActive) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (user.isActive) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Switch(
                    checked = user.isActive,
                    onCheckedChange = { onActiveToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF10B981),
                        checkedTrackColor = Color(0xFF10B981).copy(alpha = 0.3f),
                        uncheckedThumbColor = Color(0xFF64748B),
                        uncheckedTrackColor = Color(0xFF334155)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormSheet(
    onDismiss: () -> Unit,
    onSubmit: (username: String, email: String, firstName: String, lastName: String, role: String, passwordProvisional: String) -> Unit,
    isSubmitting: Boolean,
    errorMessage: String?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val roles = listOf(
        "teacher" to "Profesor",
        "manager" to "Jefe",
        "employee" to "Empleado"
    )
    var selectedRolePair by remember { mutableStateOf(roles[0]) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var formErrors by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E293B),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF64748B)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(24.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Registrar Nuevo Personal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC)
            )

            if (formErrors != null || errorMessage != null) {
                Text(
                    text = formErrors ?: errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña provisional") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Exposed Dropdown Menu Box for Roles
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedRolePair.second,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(Color(0xFF1E293B))
                ) {
                    roles.forEach { rolePair ->
                        DropdownMenuItem(
                            text = { Text(rolePair.second, color = Color.White) },
                            onClick = {
                                selectedRolePair = rolePair
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF94A3B8)
                    )
                ) {
                    Text("Cancelar")
                }
                
                Button(
                    onClick = {
                        // Form validations
                        if (username.isBlank() || email.isBlank() || firstName.isBlank() || lastName.isBlank() || password.isBlank()) {
                            formErrors = "Todos los campos son obligatorios."
                            return@Button
                        }
                        if (!email.contains("@") || !email.contains(".")) {
                            formErrors = "Por favor ingresa un correo electrónico válido."
                            return@Button
                        }
                        formErrors = null
                        onSubmit(username.trim(), email.trim(), firstName.trim(), lastName.trim(), selectedRolePair.first, password)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    ),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrar", color = Color.White)
                    }
                }
            }
        }
    }
}
