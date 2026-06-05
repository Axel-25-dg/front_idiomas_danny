package com.ute.guamanidiomas.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ute.guamanidiomas.navigation.Screen
import com.ute.guamanidiomas.ui.theme.*

data class BottomNavItem(
    val screen:       Screen?,
    val label:        String,
    val icon:         ImageVector,
    val iconSelected: ImageVector,
    val badgeCount:   Int = 0,
    val onClick:      (() -> Unit)? = null
)

@Composable
fun BottomNavBar(
    navController: NavController,
) {
    val items = listOf(
        BottomNavItem(Screen.Home,       "Inicio",     Icons.Outlined.Dashboard,     Icons.Filled.Dashboard),
        BottomNavItem(Screen.MyClasses,  "Mis Clases", Icons.Outlined.AutoStories,   Icons.Filled.AutoStories),
        BottomNavItem(Screen.GameCenter, "Juegos",     Icons.Outlined.SportsEsports, Icons.Filled.SportsEsports),
        BottomNavItem(Screen.Profile,    "Perfil",     Icons.Outlined.AccountCircle, Icons.Filled.AccountCircle),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    Surface(
        color = SurfaceColor,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            NavigationBar(
                containerColor = SurfaceColor,
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding().widthIn(max = 600.dp).height(68.dp)
            ) {
                items.forEach { item ->
                    val isSelected = if (item.screen != null) {
                        currentRoute?.startsWith(item.screen.route.substringBefore("/{")) == true
                    } else false

                    NavigationBarItem(
                        selected = isSelected,
                        onClick  = {
                            if (item.onClick != null) {
                                item.onClick.invoke()
                            } else if (item.screen != null) {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.badgeCount > 0) {
                                        Badge(containerColor = PrimaryRed) {
                                            Text(item.badgeCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.iconSelected else item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryBlue else TextFaint
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor       = PrimaryBlue,
                            selectedTextColor       = PrimaryBlue,
                            indicatorColor          = LightBlue,
                            unselectedIconColor     = TextFaint,
                            unselectedTextColor     = TextFaint,
                        ),
                    )
                }
            }
        }
    }
}
