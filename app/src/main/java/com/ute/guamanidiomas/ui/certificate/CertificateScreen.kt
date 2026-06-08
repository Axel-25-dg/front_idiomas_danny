package com.ute.guamanidiomas.ui.certificate

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.ui.theme.PrimaryBlue
import com.ute.guamanidiomas.ui.theme.AccentBlue

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(
    courseId: Int,
    onBack: () -> Unit,
    viewModel: CertificateViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userState = authViewModel.currentUser.collectAsState()
    val studentName = userState.value?.username ?: "Estudiante JumpUp"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Certificado", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.downloadCertificate(
                            context = context,
                            studentName = studentName,
                            courseName = "Inglés Nivel A1",
                            certificateId = "CERT-2024-$courseId"
                        )
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Descargar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .border(4.dp, PrimaryBlue, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "JUMPUP",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Black
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CERTIFICADO DE FINALIZACIÓN",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Otorgado a:",
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = "ESTUDIANTE JUMPUP",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryBlue,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = "Por haber completado satisfactoriamente el curso de\nInglés Nivel A1",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.width(100.dp).height(1.dp).background(Color.Gray))
                            Text("Director Académico", fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.width(100.dp).height(1.dp).background(Color.Gray))
                            Text("Sello Digital", fontSize = 10.sp)
                        }
                    }

                    Text(
                        text = "ID: CERT-2024-${courseId}",
                        fontSize = 10.sp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}