package com.ute.guamanidiomas.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ute.guamanidiomas.domain.model.Course
import com.ute.guamanidiomas.ui.components.ErrorScreen
import com.ute.guamanidiomas.ui.components.LoadingScreen
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.CartViewModel
import com.ute.guamanidiomas.ui.viewmodel.CourseDetailUiState
import com.ute.guamanidiomas.ui.viewmodel.CourseDetailViewModel
import kotlinx.coroutines.delay

@Composable
fun CourseDetailScreen(
    courseId: Int,
    onBack: () -> Unit,
    onOpenCart: () -> Unit = {},
    cartViewModel: CartViewModel,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val cartCount by cartViewModel.totalItems.collectAsState()

    LaunchedEffect(courseId) { viewModel.load(courseId) }

    when (val s = state) {
        is CourseDetailUiState.Loading -> LoadingScreen("Cargando detalles del curso...")
        is CourseDetailUiState.Error -> ErrorScreen(s.message, onRetry = { viewModel.load(courseId) })
        is CourseDetailUiState.Success -> CourseDetailContent(
            course = s.course,
            modules = s.modules,
            onBack = onBack,
            cartViewModel = cartViewModel,
            cartCount = cartCount,
            onOpenCart = onOpenCart
        )
    }
}

@Composable
private fun ModuleItem(module: com.ute.guamanidiomas.domain.model.Module, index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = PrimaryBlue.copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = index.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = module.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (module.description.isNotBlank()) {
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDetailContent(
    course: Course,
    modules: List<com.ute.guamanidiomas.domain.model.Module>,
    onBack: () -> Unit,
    cartViewModel: CartViewModel,
    cartCount: Int,
    onOpenCart: () -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var added by remember { mutableStateOf(false) }
    val subtotal = course.price * quantity

    val (flag, heroBg) = when (course.languageName?.lowercase() ?: "") {
        "inglés" -> "🇺🇸" to listOf(Color(0xFFDBEAFE), Color(0xFFBFD7FF))
        "francés" -> "🇫🇷" to listOf(LightRed, Color(0xFFFECACA))
        "alemán" -> "🇩🇪" to listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
        "italiano" -> "🇮🇹" to listOf(Color(0xFFDCFCE7), Color(0xFFBBF7D0))
        "portugués" -> "🇧🇷" to listOf(Color(0xFFFED7AA), Color(0xFFFDBA74))
        else -> "📚" to listOf(LightBlue, SkyBlue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Brush.verticalGradient(heroBg))
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 20.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.08f))
            )
            if (course.imageUrl != null) {
                AsyncImage(
                    model = course.imageUrl,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(flag, fontSize = 96.sp)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceColor)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
                Box {
                    IconButton(
                        onClick = onOpenCart,
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceColor)
                    ) {
                        Icon(Icons.Default.ShoppingCart, "Carrito", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                    if (cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-1).dp, y = 1.dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(PrimaryRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cartCount.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
            if (!course.isActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(PrimaryRed.copy(alpha = 0.92f))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("CURSO NO DISPONIBLE", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(SurfaceColor)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp)) {
                        Text(
                            course.languageName?.uppercase() ?: "IDIOMA",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Surface(color = PageBg, shape = RoundedCornerShape(10.dp)) {
                        Text(
                            course.level ?: "N/A",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    course.title ?: "Sin título",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    lineHeight = 34.sp
                )

                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("$${"%.2f".format(course.price)}", fontSize = 34.sp, fontWeight = FontWeight.Black, color = PrimaryBlue)
                    Text("+ IVA", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.padding(bottom = 7.dp))
                }

                if (!course.description.isNullOrBlank()) {
                    Spacer(Modifier.height(22.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(Modifier.height(22.dp))
                    Text("Sobre este curso", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(10.dp))
                    Text(course.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 22.sp)
                }

                if (modules.isNotEmpty()) {
                    Spacer(Modifier.height(22.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(Modifier.height(22.dp))
                    Text("Contenido del curso", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(14.dp))
                    modules.forEachIndexed { index, module ->
                        ModuleItem(module, index + 1)
                        if (index < modules.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Divider)
                        }
                    }
                }

                if (course.isActive) {
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(Modifier.height(22.dp))
                    Text("Inscripciones", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(PageBg)
                                .padding(4.dp)
                        ) {
                            IconButton(onClick = { if (quantity > 1) quantity-- }, enabled = quantity > 1, modifier = Modifier.size(38.dp)) {
                                Icon(Icons.Default.Remove, null, tint = if (quantity > 1) PrimaryBlue else TextFaint, modifier = Modifier.size(18.dp))
                            }
                            Text(quantity.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
                            IconButton(onClick = { if (quantity < 10) quantity++ }, enabled = quantity < 10, modifier = Modifier.size(38.dp)) {
                                Icon(Icons.Default.Add, null, tint = if (quantity < 10) PrimaryBlue else TextFaint, modifier = Modifier.size(18.dp))
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Subtotal", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("$${"%.2f".format(subtotal)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { cartViewModel.addItem(course, quantity); added = true },
                        enabled = !added,
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (added) Success else PrimaryBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(18.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
                    ) {
                        Icon(if (added) Icons.Default.Check else Icons.Default.ShoppingCart, null, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            if (added) "¡Añadido al carrito!" else "Añadir${if (quantity > 1) " $quantity" else ""} al carrito",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    LaunchedEffect(added) {
                        if (added) { delay(2000); added = false }
                    }
                } else {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Curso no disponible", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
