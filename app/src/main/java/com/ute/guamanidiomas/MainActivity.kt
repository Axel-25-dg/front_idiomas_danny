package com.ute.guamanidiomas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.ute.guamanidiomas.ui.components.LoadingScreen
import com.ute.guamanidiomas.navigation.Screen
import com.ute.guamanidiomas.ui.auth.LoginScreen
import com.ute.guamanidiomas.ui.auth.RegisterScreen
import com.ute.guamanidiomas.ui.home.HomeScreen
import com.ute.guamanidiomas.ui.home.CatalogScreen
import com.ute.guamanidiomas.ui.navigation.BottomNavBar
import com.ute.guamanidiomas.ui.viewmodel.AuthViewModel
import com.ute.guamanidiomas.ui.viewmodel.CartViewModel
import com.ute.guamanidiomas.ui.viewmodel.HomeViewModel
import com.ute.guamanidiomas.ui.theme.GuamanIdiomasTheme
import dagger.hilt.android.AndroidEntryPoint

import com.ute.guamanidiomas.ui.navigation.NavGraph

import androidx.activity.SystemBarStyle

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            GuamanIdiomasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    NavGraph(authViewModel = authViewModel)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardTestScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Panel de Administración", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) { Text("Cerrar sesión") }
    }
}
