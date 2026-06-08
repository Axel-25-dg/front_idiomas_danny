package com.ute.guamanidiomas.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFormScreen(
    viewModel: AdminViewModel,
    courseId: Int? = null,
    onNavigateBack: () -> Unit
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("A1") }
    var isActive by remember { mutableStateOf(true) }
    var expandedLevelDropdown by remember { mutableStateOf(false) }

    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    val uiState by viewModel.state.collectAsState()
    val errorMessage = uiState.error
    val isSuccess = uiState.saveSuccess

    LaunchedEffect(courseId) {
        viewModel.resetStatus()

        if (courseId != null) {
            val courseToEdit = uiState.courses.find { it.id == courseId }
            courseToEdit?.let { course ->
                title = course.title ?: ""
                description = course.description ?: ""
                price = course.price.toString()
                level = course.level ?: "A1"
                isActive = course.isActive
            }
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.resetStatus()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (courseId == null) "Nuevo Curso" else "Editar Curso",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AdminBg
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AdminBg)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del curso") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio") },
                        modifier = Modifier.weight(1f),
                        prefix = { Text("$ ") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = AccentBlue,
                            unfocusedLabelColor = Color.Gray
                        )
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedLevelDropdown,
                            onExpandedChange = { expandedLevelDropdown = !expandedLevelDropdown }
                        ) {
                            OutlinedTextField(
                                value = level,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Nivel") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLevelDropdown) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = AccentBlue,
                                    unfocusedLabelColor = Color.Gray
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedLevelDropdown,
                                onDismissRequest = { expandedLevelDropdown = false }
                            ) {
                                levels.forEach { selectedLevel ->
                                    DropdownMenuItem(
                                        text = { Text(selectedLevel) },
                                        onClick = {
                                            level = selectedLevel
                                            expandedLevelDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = CheckboxDefaults.colors(checkedColor = AccentBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Curso activo (visible en catálogo)",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val parsedPrice = price.toDoubleOrNull() ?: 0.0
                        viewModel.saveCourse(
                            id = courseId,
                            title = title,
                            description = description,
                            price = parsedPrice,
                            level = level,
                            isActive = isActive,
                            languageId = 1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isSaving,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "GUARDAR CURSO",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}