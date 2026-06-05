package com.ute.guamanidiomas.ui.admin.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ute.guamanidiomas.data.remote.dto.AuditLogDto
import com.ute.guamanidiomas.data.remote.dto.OrderDto
import com.ute.guamanidiomas.ui.admin.AdminMainViewModel
import com.ute.guamanidiomas.ui.admin.components.AdminErrorBanner
import com.ute.guamanidiomas.ui.admin.components.AdminStatCard
import com.ute.guamanidiomas.ui.theme.*
import java.util.Locale

@Composable
fun AdminOverviewSection(viewModel: AdminMainViewModel) {
    val state by viewModel.overviewState.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
        }
        return
    }

    LazyColumn(
        modifier        = Modifier.fillMaxSize(),
        contentPadding  = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner de bienvenida
        item {
            WelcomeBanner(
                adminName    = state.adminName,
                totalUsers   = state.totalUsers,
                totalCourses = state.totalCourses,
                totalRevenue = state.totalRevenue
            )
        }

        state.error?.let { err ->
            item { AdminErrorBanner(message = err) }
        }

        // Grid de estadísticas principales — 2x3
        item {
            Text(
                "Métricas Globales",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        label    = "Usuarios totales",
                        value    = state.totalUsers.toString(),
                        icon     = Icons.Default.People,
                        color    = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        label    = "Profesores",
                        value    = state.totalTeachers.toString(),
                        icon     = Icons.Default.School,
                        color    = Color(0xFF059669),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        label    = "Estudiantes",
                        value    = state.totalStudents.toString(),
                        icon     = Icons.Default.Person,
                        color    = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        label    = "Cursos",
                        value    = state.totalCourses.toString(),
                        icon     = Icons.Default.Book,
                        color    = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        label    = "Ingresos",
                        value    = String.format(Locale.getDefault(), "$ %.2f", state.totalRevenue),
                        icon     = Icons.Default.MonetizationOn,
                        color    = Success,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        label    = "Órdenes",
                        value    = state.totalOrders.toString(),
                        icon     = Icons.Default.ShoppingBag,
                        color    = AccentBlue,
                        subtitle = "${state.pendingOrders} pendientes",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Órdenes recientes
        if (state.recentOrders.isNotEmpty()) {
            item {
                Text(
                    "Órdenes Recientes",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
            }
            items(state.recentOrders) { order ->
                RecentOrderRow(order = order)
            }
        }

        // Actividad del sistema
        if (state.recentAuditLogs.isNotEmpty()) {
            item {
                Text(
                    "Actividad Reciente",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
            }
            items(state.recentAuditLogs) { log ->
                RecentAuditRow(log = log)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun WelcomeBanner(
    adminName: String,
    totalUsers: Int,
    totalCourses: Int,
    totalRevenue: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(22.dp),
        color    = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(PrimaryBlue, Color(0xFF4F46E5))
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    "¡Hola, ${adminName.replaceFirstChar { it.uppercase() }}!",
                    color      = Color.White,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Panel de Control Académico",
                    color    = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    MiniStat("Usuarios", totalUsers.toString())
                    MiniStat("Cursos", totalCourses.toString())
                    MiniStat("Ingresos", "$ ${String.format(Locale.getDefault(), "%.0f", totalRevenue)}")
                }
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
    }
}

@Composable
private fun RecentOrderRow(order: OrderDto) {
    val status    = order.status?.uppercase() ?: "PENDIENTE"
    val color     = when (status) {
        "COMPLETED", "COMPLETADO", "PAID" -> Success
        "CANCELLED", "CANCELADO"          -> ErrorColor
        else                              -> Warning
    }
    val total     = order.totalPrice ?: order.total ?: 0.0
    val course    = order.courseTitle ?: order.items.firstOrNull()?.courseTitle ?: "Inscripción"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        color    = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "#${order.id} — $course",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    color      = TextPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    order.userEmail ?: "Usuario #${order.userId ?: 0}",
                    style  = MaterialTheme.typography.bodySmall,
                    color  = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$${"%.2f".format(total)}",
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    fontSize   = 13.sp
                )
                Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        status,
                        modifier  = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color     = color,
                        fontSize  = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentAuditRow(log: AuditLogDto) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        color    = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.History, null, tint = AccentBlue, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    log.user,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    color      = TextPrimary
                )
                Text(
                    log.changeMessage.ifBlank { log.actionFlag },
                    style    = MaterialTheme.typography.bodySmall,
                    color    = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                log.actionTime.take(10),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}
