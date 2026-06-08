package com.ute.guamanidiomas.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.domain.model.Certificate
import com.ute.guamanidiomas.domain.model.CertificateStatus
import com.ute.guamanidiomas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCertificatesScreen(
    onBack: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val state by viewModel.certificatesState.collectAsState()
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(Unit) { viewModel.loadMyCertificates() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Certificados", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = PrimaryBlue, strokeWidth = 3.dp)
                }
                state.error != null && state.certificates.isEmpty() -> {
                    Column(
                        Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(state.error!!, color = ErrorColor)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMyCertificates() }) { Text("Reintentar") }
                    }
                }
                state.certificates.isEmpty() -> {
                    Column(
                        Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier.size(80.dp).clip(CircleShape).background(LightBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Sin certificados", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        Text("Completa tus cursos para obtener certificados", color = TextSecondary)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "${state.certificates.size} certificados",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        items(state.certificates, key = { it.id }) { cert ->
                            CertificateCard(
                                certificate = cert,
                                onCopyCode  = { clipboard.setText(AnnotatedString(cert.verificationCode)) }
                            )
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CertificateCard(certificate: Certificate, onCopyCode: () -> Unit) {
    val statusColor = when (certificate.status) {
        CertificateStatus.ISSUED  -> Success
        CertificateStatus.REVOKED -> ErrorColor
        CertificateStatus.PENDING -> Warning
    }

    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape    = RoundedCornerShape(20.dp),
        color    = SurfaceColor
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(statusColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Verified, null, tint = statusColor, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        certificate.courseTitle.ifBlank { certificate.classroomName.ifBlank { "Certificado" } },
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = TextPrimary,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        "Nivel: ${certificate.level.ifBlank { "—" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Surface(color = statusColor.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        certificate.status.label,
                        modifier  = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color     = statusColor,
                        fontSize  = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Código de verificación", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            certificate.verificationCode.ifBlank { "—" },
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp,
                            color      = PrimaryBlue
                        )
                        if (certificate.verificationCode.isNotBlank()) {
                            IconButton(onClick = onCopyCode, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.ContentCopy, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
                certificate.issuedAt?.let { date ->
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Emitido", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text(date.take(10), style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}