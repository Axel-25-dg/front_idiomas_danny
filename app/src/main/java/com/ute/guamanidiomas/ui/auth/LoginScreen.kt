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
fun LoginScreen(
    onLoginSuccess: (role: String, isStaff: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isEmailValid = remember(username) {
        username.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val user = (uiState as AuthUiState.Success).user
            onLoginSuccess(user.role, user.isStaff)
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
                    .wrapContentHeight() // <-- CAMBIO: Evita que el título se corte
            ) {
                // Fondo con degradado
                Box(
                    modifier = Modifier
                        .matchParentSize() // Se adapta al tamaño real del contenido
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
                        .size(180.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 50.dp, y = (-30).dp)
                        .clip(CircleShape)
                        .background(PrimaryRed.copy(alpha = 0.1f))
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-20).dp, y = (-10).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.04f))
                )

                // Contenido de la cabecera (Logo + Títulos)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        // Agregamos padding inferior extra para compensar el solapamiento de la tarjeta
                        .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 56.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Contenedor del Logo
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .shadow(16.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryBlue, DarkBlue))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "J",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "JumpUp UTE",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Tu puerta al mundo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // --- SECCIÓN DEL FORMULARIO (TARJETA BLANCA) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-32).dp) // Solapamiento sutil sobre el gradiente
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
                        Text(
                            text = "Bienvenido",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Inicia sesión para continuar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(Modifier.height(24.dp))

                        // Alerta de Error corregida
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
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        IdiomasTextField(
                            value = username,
                            onValueChange = { username = it; viewModel.clearError() },
                            label = "Correo electrónico",
                            placeholder = "ejemplo@dominio.com",
                            enabled = !isLoading,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                            isError = !isEmailValid,
                            errorMessage = if (!isEmailValid) "Formato de correo inválido" else null
                        )
                        Spacer(Modifier.height(18.dp))

                        IdiomasTextField(
                            value = password,
                            onValueChange = { password = it; viewModel.clearError() },
                            label = "Contraseña",
                            placeholder = "Tu contraseña segura",
                            isPassword = true,
                            enabled = !isLoading,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        )

                        Spacer(Modifier.height(8.dp))

                        TextButton(
                            onClick = { },
                            modifier = Modifier.align(Alignment.End),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "¿Olvidaste tu contraseña?",
                                style = MaterialTheme.typography.labelLarge,
                                color = PrimaryRed
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        IdiomasButton(
                            text = "Iniciar Sesión",
                            onClick = { viewModel.login(username.trim(), password) },
                            isLoading = isLoading,
                            enabled = username.isNotBlank() && password.isNotBlank() && isEmailValid,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Divisor
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 450.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Border)
                    Text(
                        "  o  ",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Border)
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "¿No tienes una cuenta? ",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(
                        onClick = onNavigateToRegister,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Regístrate ahora",
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}