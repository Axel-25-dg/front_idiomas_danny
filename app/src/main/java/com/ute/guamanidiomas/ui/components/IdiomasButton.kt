package com.ute.guamanidiomas.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun IdiomasButton(
    text:      String,
    onClick:   () -> Unit,
    modifier:  Modifier = Modifier,
    isLoading: Boolean  = false,
    enabled:   Boolean  = true,
) {
    val containerColor by animateColorAsState(
        targetValue = if (enabled && !isLoading) PrimaryBlue else PrimaryBlue.copy(alpha = 0.4f),
        animationSpec = tween(200),
        label = "buttonColor"
    )

    Button(
        onClick  = onClick,
        enabled  = enabled && !isLoading,
        modifier = modifier.fillMaxWidth().height(56.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = containerColor,
            contentColor           = Color.White,
            disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f),
            disabledContentColor   = Color.White.copy(alpha = 0.7f),
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp,
            hoveredElevation = 6.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color     = Color.White,
                modifier  = Modifier.size(22.dp),
                strokeWidth = 2.5.dp,
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = if (isLoading) "Cargando..." else text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IdiomasOutlinedButton(
    text:     String,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier,
    enabled:  Boolean  = true,
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryBlue,
        ),
        border   = BorderStroke(2.dp, PrimaryBlue),
        shape    = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IdiomasRedButton(
    text:      String,
    onClick:   () -> Unit,
    modifier:  Modifier = Modifier,
    isLoading: Boolean  = false,
    enabled:   Boolean  = true,
) {
    Button(
        onClick  = onClick,
        enabled  = enabled && !isLoading,
        modifier = modifier.fillMaxWidth().height(56.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = PrimaryRed,
            contentColor           = Color.White,
            disabledContainerColor = PrimaryRed.copy(alpha = 0.4f),
            disabledContentColor   = Color.White.copy(alpha = 0.7f),
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color     = Color.White,
                modifier  = Modifier.size(22.dp),
                strokeWidth = 2.5.dp,
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = if (isLoading) "Cargando..." else text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
