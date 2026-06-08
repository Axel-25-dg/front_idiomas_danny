package com.ute.guamanidiomas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun VerificationScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "JumpUp App",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = AccentBlue,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "Módulo de Verificación",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface, RoundedCornerShape(16.dp))
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Estado del entorno",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                
                val items = listOf(
                    "Kotlin" to "2.0.21",
                    "Hilt" to "2.52",
                    "Retrofit" to "2.11.0",
                    "Dominio" to "https://guaman-idiomas-ute.online",
                )

                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.first, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(text = item.second, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Entidades del Dominio",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp).align(Alignment.Start),
            )

            listOf("Auth", "Course", "Exercise", "Progress").forEach { file ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "domain/model/$file.kt", style = MaterialTheme.typography.bodySmall, color = AccentBlue)
                    Text(text = "✓", color = Success, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}