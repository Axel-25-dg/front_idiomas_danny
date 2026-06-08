package com.ute.guamanidiomas.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.AdminViewModel

@Composable
fun ModuleFormScreen(
    courseId: Int,
    moduleId: Int? = null,
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val module = remember(moduleId, state.modules) {
        state.modules.find { it.id == moduleId }
    }

    var title by remember { mutableStateOf(module?.title ?: "") }
    var description by remember { mutableStateOf(module?.description ?: "") }
    var order by remember { mutableStateOf(module?.order?.toString() ?: "1") }
    var isActive by remember { mutableStateOf(module?.isActive ?: true) }

    LaunchedEffect(courseId) {
        if (state.modules.isEmpty()) {
            viewModel.loadModules(courseId)
        }
    }

    LaunchedEffect(module) {
        module?.let {
            title = it.title ?: ""
            description = it.description ?: ""
            order = it.order.toString()
            isActive = it.isActive
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = TextPrimary.copy(alpha = 0.05f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Volver",
                            modifier = Modifier.size(20.dp),
                            tint = TextPrimary
                        )
                    }
                    
                    Spacer(Modifier.width(16.dp))
                    
                    Text(
                        text = if (moduleId == null) "Nuevo Módulo" else "Editar Módulo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del módulo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = order,
                    onValueChange = { if (it.all { char -> char.isDigit() }) order = it },
                    label = { Text("Orden") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                    Text("Módulo activo")
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.saveModule(
                            id = moduleId,
                            courseId = courseId,
                            title = title,
                            description = description,
                            order = order.toIntOrNull() ?: 1,
                            isActive = isActive
                        )
                    },
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text("GUARDAR MÓDULO", fontWeight = FontWeight.Bold)
                    }
                }

                LaunchedEffect(state.saveSuccess) {
                    if (state.saveSuccess) {
                        viewModel.resetStatus()
                        onBack()
                    }
                }
            }
        }
    }
}