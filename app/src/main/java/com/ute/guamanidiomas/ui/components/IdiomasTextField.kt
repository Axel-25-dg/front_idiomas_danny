package com.ute.guamanidiomas.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.ute.guamanidiomas.ui.theme.*

@Composable
fun IdiomasTextField(
    value:         String,
    onValueChange: (String) -> Unit,
    label:         String,
    modifier:      Modifier = Modifier,
    placeholder:   String   = "",
    isError:       Boolean  = false,
    errorMessage:  String?  = null,
    isPassword:    Boolean  = false,
    trailingIcon:  @Composable (() -> Unit)? = null,
    keyboardType:  KeyboardType = KeyboardType.Text,
    imeAction:     ImeAction    = ImeAction.Next,
    enabled:       Boolean      = true,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val visualTransformation = when {
        isPassword && !passwordVisible -> PasswordVisualTransformation()
        else                           -> VisualTransformation.None
    }

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> ErrorColor
            else -> Border
        },
        animationSpec = tween(200),
        label = "borderColor"
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value           = value,
            onValueChange   = onValueChange,
            label           = { Text(label, style = MaterialTheme.typography.labelLarge) },
            placeholder     = { Text(placeholder, color = TextFaint, style = MaterialTheme.typography.bodyLarge) },
            isError         = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            enabled         = enabled,
            singleLine      = true,
            modifier        = Modifier.fillMaxWidth(),
            shape           = RoundedCornerShape(14.dp),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor     = PrimaryBlue,
                focusedLabelColor      = PrimaryBlue,
                cursorColor            = PrimaryBlue,
                unfocusedBorderColor   = Border,
                unfocusedLabelColor    = TextSecondary,
                errorBorderColor       = ErrorColor,
                errorLabelColor        = ErrorColor,
                focusedContainerColor  = SurfaceColor,
                unfocusedContainerColor = SurfaceColor,
            ),
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = TextSecondary,
                        )
                    }
                }
            } else trailingIcon,
        )
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = errorMessage,
                color = ErrorColor,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
