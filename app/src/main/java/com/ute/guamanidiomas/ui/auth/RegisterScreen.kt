package com.ute.guamanidiomas.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.components.IdiomasTextField
import com.ute.guamanidiomas.ui.components.IdiomasButton
import com.ute.guamanidiomas.ui.viewmodel.AuthViewModel
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: (role: String, isStaff: Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }

    val passwordMismatch = password.isNotEmpty() &&
            password2.isNotEmpty() &&
            password != password2

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val user = (uiState as AuthUiState.Success).user
            onRegisterSuccess(user.role, user.isStaff)
        }
    }

    val isLoading = uiState is AuthUiState.Loading
    val errorMsg = (uiState as? AuthUiState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- CABECERA DE GRADIENTE ADAPTATIVA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // <-- CAMBIO CLAVE: Evita que el título se corte
            ) {
                // Fondo con degradado dinámico
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 40.dp,
                                bottomEnd = 40.dp
                            )
                        )
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryBlue, DarkBlue)
                            )
                        )
                )

                // Decoraciones geométricas
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 40.dp, y = (-20).dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.15f))
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-15).dp, y = (-15).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.04f))
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = (-30).dp, y = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryRed.copy(alpha = 0.2f))
                )

                // Contenido de la cabecera (Logo, Título, Subtítulo)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        // Agregamos padding bottom de 56.dp para tolerar el solapamiento de la tarjeta blanca
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(14.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(PrimaryRed, PrimaryBlue)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "J",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Crea tu cuenta",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Comienza tu aprendizaje hoy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }

            // --- SECCIÓN DEL FORMULARIO (TARJETA BLANCA) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-32).dp) // Desplazamiento controlado hacia arriba
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = SurfaceColor,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 450.dp)
                ) {
                    Column(modifier = Modifier.padding(28.dp)) {

                        // Alerta de Error optimizada
                        if (errorMsg != null) {
                            Surface(
                                color = LightRed,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                border = BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = PrimaryRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = errorMsg,
                                        color = PrimaryRed,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }

                        IdiomasTextField(
                            value = username,
                            onValueChange = { username = it; viewModel.clearError() },
                            label = "Nombre de usuario",
                            placeholder = "Mínimo 3 caracteres",
                            enabled = !isLoading,
                            isError = username.isNotEmpty() && username.length < 3,
                            errorMessage = "Mínimo 3 caracteres",
                            imeAction = ImeAction.Next,
                        )
                        Spacer(Modifier.height(16.dp))

                        IdiomasTextField(
                            value = email,
                            onValueChange = { email = it; viewModel.clearError() },
                            label = "Correo electrónico",
                            placeholder = "ejemplo@dominio.com",
                            enabled = !isLoading,
                            keyboardType = KeyboardType.Email,
                            isError = email.isNotEmpty() && !email.contains("@"),
                            errorMessage = "Email inválido",
                            imeAction = ImeAction.Next,
                        )
                        Spacer(Modifier.height(16.dp))

                        IdiomasTextField(
                            value = password,
                            onValueChange = { password = it; viewModel.clearError() },
                            label = "Contraseña",
                            placeholder = "Mínimo 8 caracteres",
                            isPassword = true,
                            enabled = !isLoading,
                            keyboardType = KeyboardType.Password,
                            isError = password.isNotEmpty() && password.length < 8,
                            errorMessage = "Mínimo 8 caracteres",
                            imeAction = ImeAction.Next,
                        )
                        Spacer(Modifier.height(16.dp))

                        IdiomasTextField(
                            value = password2,
                            onValueChange = { password2 = it; viewModel.clearError() },
                            label = "Confirmar contraseña",
                            placeholder = "Repite la contraseña",
                            isPassword = true,
                            enabled = !isLoading,
                            keyboardType = KeyboardType.Password,
                            isError = passwordMismatch,
                            errorMessage = "Las contraseñas no coinciden",
                            imeAction = ImeAction.Done,
                        )

                        Spacer(Modifier.height(24.dp))

                        val canSubmit = username.length >= 3 &&
                                email.contains("@") &&
                                password.length >= 8 &&
                                !passwordMismatch &&
                                !isLoading

                        IdiomasButton(
                            text = "Crear Cuenta",
                            onClick = { viewModel.register(username.trim(), email.trim(), password, password2) },
                            isLoading = isLoading,
                            enabled = canSubmit,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Footer (Volver al login)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "¿Ya tienes una cuenta? ",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Inicia sesión",
                            color = PrimaryRed,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}