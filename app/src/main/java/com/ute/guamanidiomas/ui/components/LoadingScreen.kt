package com.ute.guamanidiomas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun LoadingScreen(message: String = "Cargando...") {
    Box(
        modifier          = Modifier.fillMaxSize().background(BackgroundColor),
        contentAlignment  = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = PrimaryBlue,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text      = message,
                color     = TextSecondary,
                style     = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: (() -> Unit)? = null) {
    Box(
        modifier          = Modifier.fillMaxSize().background(BackgroundColor),
        contentAlignment  = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp),
        ) {
            Text("⚠️", fontSize = 56.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text      = "Algo salió mal",
                color     = TextPrimary,
                style     = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = message,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            if (onRetry != null) {
                Spacer(Modifier.height(28.dp))
                IdiomasButton(text = "Reintentar", onClick = onRetry, modifier = Modifier.width(200.dp))
            }
        }
    }
}