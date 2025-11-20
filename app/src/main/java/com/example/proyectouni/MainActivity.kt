package com.example.proyectouni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyectouni.ui.screens.*
import com.example.proyectouni.ui.theme.ProyectoUniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoUniTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UniLocalApp()
                }
            }
        }
    }
}

@Composable
fun UniLocalApp() {
    var currentScreen by remember { mutableStateOf("login") }

    when (currentScreen) {
        "login" -> LoginScreen(
            onNavigate = { screen -> currentScreen = screen }
        )
        "register" -> RegisterScreen(
            onNavigate = { screen -> currentScreen = screen }
        )
        "forgot_password" -> ForgotPasswordScreen(
            onNavigateBack = { currentScreen = "login" }
        )
        "main" -> MainMapScreen(
            onNavigate = { screen -> currentScreen = screen }
        )
        "detail" -> PlaceDetailScreen(
            placeId = null,
            onNavigate = { screen -> currentScreen = screen }
        )
        "create" -> CreatePlaceScreen(
            onNavigate = { screen -> currentScreen = screen }
        )
        "moderator" -> ModeratorScreen(
            onNavigate = { screen -> currentScreen = screen }
        )
        "menu" -> {
            // TODO: Implementar MenuScreen
        }
        "profile" -> {
            // TODO: Implementar ProfileScreen
        }
    }
}