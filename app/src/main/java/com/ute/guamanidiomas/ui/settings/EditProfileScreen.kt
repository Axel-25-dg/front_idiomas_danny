package com.ute.guamanidiomas.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState()
    
    var username by remember(user) { mutableStateOf(user?.username ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var bio by remember { mutableStateOf("Estudiante apasionado por los idiomas en la UTE.") }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = { /* TODO: Save changes via ViewModel */ }) {
                        Text("Guardar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(BlueGradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        username.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                IconButton(
                    onClick = { /* TODO: Image Picker */ },
                    modifier = Modifier
                        .size(32.dp)
                        .background(PrimaryBlue, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Person, null, tint = PrimaryBlue) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Border
                )
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Border,
                    disabledLabelColor = TextTertiary,
                    disabledTextColor = TextSecondary
                )
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Biografía") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Border
                )
            )

            Spacer(Modifier.height(24.dp))
            
            Text(
                "La información académica (Universidad y Carrera) no puede ser editada manualmente. Contacta a secretaría si hay un error.",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}