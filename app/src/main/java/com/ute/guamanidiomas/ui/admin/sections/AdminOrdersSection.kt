package com.ute.guamanidiomas.ui.admin.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.data.remote.dto.OrderDto
import com.ute.guamanidiomas.ui.admin.AdminMainViewModel
import com.ute.guamanidiomas.ui.admin.components.*
import com.ute.guamanidiomas.ui.theme.*
import java.util.Locale

private val STATUS_FILTERS = listOf("" to "Todas", "pending" to "Pendiente", "completed" to "Completada", "cancelled" to "Cancelada")

@Composable
fun AdminOrdersSection(viewModel: AdminMainViewModel) {
    val state by viewModel.ordersState.collectAsState()

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) viewModel.clearOrdersMessages()
    }

    if (state.isLoading && state.orders.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        IngresosSummaryCard(
            totalRevenue     = state.totalRevenue,
            totalOrders      = state.orders.size,
            pendingCount     = state.orders.count { it.status?.lowercase() == "pending" },
            completedCount   = state.orders.count { it.status?.lowercase() == "completed" }
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(STATUS_FILTERS) { (statusKey, label) ->
                FilterChip(
                    selected = state.filterStatus == statusKey,
                    onClick  = { viewModel.loadOrders(statusKey) },
                    label    = { Text(label) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor     = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
        }

        state.error?.let { err ->
            AdminErrorBanner(
                message   = err,
                onDismiss = { viewModel.clearOrdersMessages() }
            )
        }

        if (state.orders.isEmpty() && !state.isLoading) {
            AdminEmptyState(
                icon     = Icons.Default.ShoppingBag,
                title    = "Sin órdenes",
                subtitle = "No hay órdenes que coincidan con el filtro"
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        "${state.orders.size} órdenes",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(state.orders, key = { it.id }) { order ->
                    OrderAdminCard(
                        order      = order,
                        onApprove  = { viewModel.approveOrder(order.id) }
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun IngresosSummaryCard(
    totalRevenue: Double,
    totalOrders: Int,
    pendingCount: Int,
    completedCount: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceColor
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            SummaryItem("Ingresos", "$ ${String.format(Locale.US, "%.2f", totalRevenue)}", Success)
            VerticalDivider(Modifier.height(40.dp))
            SummaryItem("Total", totalOrders.toString(), PrimaryBlue)
            VerticalDivider(Modifier.height(40.dp))
            SummaryItem("Pendientes", pendingCount.toString(), Warning)
            VerticalDivider(Modifier.height(40.dp))
            SummaryItem("Completadas", completedCount.toString(), Success)
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun OrderAdminCard(order: OrderDto, onApprove: () -> Unit) {
    val status    = order.status?.uppercase() ?: "PENDING"
    val color     = when (status) {
        "COMPLETED", "COMPLETADO", "PAID" -> Success
        "CANCELLED", "CANCELADO"          -> ErrorColor
        else                              -> Warning
    }
    val total     = order.totalPrice ?: order.total ?: 0.0
    val course    = order.courseTitle ?: order.items?.firstOrNull()?.courseTitle ?: "Inscripción"
    val isPending = status in setOf("PENDING", "PENDIENTE")

    Surface(
        modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        color    = SurfaceColor
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingBag, null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "#${order.id} — $course",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        color      = TextPrimary,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        order.userEmail ?: "Usuario #${order.userId ?: 0}",
                        style    = MaterialTheme.typography.bodySmall,
                        color    = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$${"%.2f".format(Locale.US, total)}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)) {
                        Text(
                            status,
                            modifier  = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color     = color,
                            fontSize  = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (isPending) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick  = onApprove,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Success)
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Aprobar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}