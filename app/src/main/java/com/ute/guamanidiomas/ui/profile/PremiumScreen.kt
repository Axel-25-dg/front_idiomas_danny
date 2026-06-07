package com.ute.guamanidiomas.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale
import com.ute.guamanidiomas.domain.model.Payment
import com.ute.guamanidiomas.domain.model.SubscriptionPlan
import com.ute.guamanidiomas.ui.theme.*
import com.ute.guamanidiomas.ui.viewmodel.SubscribeResult
import com.ute.guamanidiomas.ui.viewmodel.SubscriptionViewModel

@Composable
fun PremiumScreen(
    onBack: () -> Unit,
    onSubscribe: (String) -> Unit = {},
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.subscribeResult) {
        when (val result = state.subscribeResult) {
            is SubscribeResult.Success -> {
                snackbarHostState.showSnackbar("¡Suscripción a ${result.planName} activada!")
                onSubscribe(result.planName)
                viewModel.clearSubscribeResult()
            }
            is SubscribeResult.Error -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.clearSubscribeResult()
            }
            null -> Unit
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { PremiumTopBar(onBack = onBack) }
    ) { padding ->
        if (state.isLoading && state.plans.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { PremiumHero(isPremium = state.isPremium) }

                if (state.isPremium && state.activeSubscription != null) {
                    item {
                        ActiveSubscriptionBanner(
                            planName = state.activeSubscription!!.planName,
                            endDate  = state.activeSubscription!!.endDate
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Elige tu plan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }

                if (state.plans.isEmpty()) {
                    item {
                        PlanCard(
                            title     = "Mensual",
                            price     = "$9.99",
                            period    = "/ mes",
                            features  = listOf("Todos los cursos", "Sin anuncios", "Certificados"),
                            isPopular = false,
                            isLoading = state.isLoading,
                            onClick   = { viewModel.subscribe(1) }
                        )
                    }
                    item {
                        Spacer(Modifier.height(16.dp))
                        PlanCard(
                            title     = "Anual",
                            price     = "$79.99",
                            period    = "/ año",
                            features  = listOf("Todos los cursos", "Sin anuncios", "Certificados", "Soporte prioritario", "2 meses gratis"),
                            isPopular = true,
                            isLoading = state.isLoading,
                            onClick   = { viewModel.subscribe(2) }
                        )
                    }
                } else {
                    items(state.plans) { plan ->
                        Spacer(Modifier.height(if (plan == state.plans.first()) 0.dp else 16.dp))
                        PlanCard(
                            title     = plan.name,
                            price     = "$${"%.2f".format(Locale.US, plan.price)}",
                            period    = "/ ${plan.durationDays} días",
                            features  = plan.features.ifEmpty {
                                listOf("Acceso completo", "Sin anuncios", "Certificado digital")
                            },
                            isPopular = plan.price == state.plans.maxOfOrNull { it.price },
                            isLoading = state.isLoading,
                            onClick   = { viewModel.subscribe(plan.id) }
                        )
                    }
                }

                if (state.paymentHistory.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(32.dp))
                        Text(
                            "Historial de pagos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    items(state.paymentHistory) { payment ->
                        PaymentRow(payment)
                    }
                }

                if (state.error != null) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            state.error!!,
                            color = PrimaryRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = viewModel::loadDashboard) {
                            Text("Reintentar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveSubscriptionBanner(planName: String, endDate: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        color = Success.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Success.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Check, null, tint = Success, modifier = Modifier.size(24.dp))
            Column {
                Text(
                    "Plan $planName activo",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF166534),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Válido hasta $endDate",
                    color = Color(0xFF166534).copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun PremiumHero(isPremium: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .height(210.dp)
            .shadow(10.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(HeroGradient))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Decorative elements
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(PrimaryRed.copy(alpha = 0.2f))
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(52.dp))
            Spacer(Modifier.height(14.dp))
            Text(
                if (isPremium) "Ya tienes Premium" else "Desbloquea tu potencial",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Aprende sin límites con JumpUp Premium",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PremiumTopBar(onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = SurfaceColor, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .background(PrimaryBlue.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(
                    "JumpUp Premium",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = TextPrimary
                )
                Text(
                    "MEMBRESÍA EXCLUSIVA",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    features: List<String>,
    isPopular: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = if (isPopular) 2.dp else 1.dp,
            color = if (isPopular) PrimaryBlue else Border
        ),
        color = SurfaceColor,
        shadowElevation = if (isPopular) 8.dp else 2.dp,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            if (isPopular) {
                Surface(
                    color = PrimaryRed,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        "MÁS POPULAR",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = PrimaryBlue)
                Text(period, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
            }
            Spacer(Modifier.height(18.dp))
            HorizontalDivider(color = Divider)
            Spacer(Modifier.height(18.dp))
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
                    Icon(Icons.Default.Check, null, tint = Success, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(feature, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isPopular) 4.dp else 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) PrimaryBlue else PrimaryBlue.copy(alpha = 0.08f),
                    contentColor   = if (isPopular) Color.White else PrimaryBlue
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = if (isPopular) Color.White else PrimaryBlue, strokeWidth = 2.dp)
                } else {
                    Text("Seleccionar Plan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PaymentRow(payment: Payment) {
    val statusColor = when (payment.status) {
        "succeeded" -> Success
        "failed"    -> PrimaryRed
        else        -> Warning
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Pago #${payment.id}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(payment.createdAt.take(10), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("$${"%.2f".format(Locale.US, payment.amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black, color = statusColor)
            Text(payment.status, style = MaterialTheme.typography.labelSmall, color = statusColor)
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Divider)
}
