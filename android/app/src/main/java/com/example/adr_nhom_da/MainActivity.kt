package com.example.adr_nhom_da

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.adr_nhom_da.data.model.User
import com.example.adr_nhom_da.data.repository.RestaurantRepository
import com.example.adr_nhom_da.ui.screens.auth.LoginScreen
import com.example.adr_nhom_da.ui.screens.auth.RegisterScreen
import com.example.adr_nhom_da.ui.screens.home.MainContainerScreen
import com.example.adr_nhom_da.ui.theme.AdrNhomDaTheme

enum class ScreenState {
    LOGIN,
    REGISTER,
    MAIN_APP
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: RestaurantRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = RestaurantRepository(this)

        setContent {
            AdrNhomDaTheme {
                var currentScreen by remember { mutableStateOf(ScreenState.LOGIN) }
                var currentUser by remember { mutableStateOf<User?>(null) }

                when (currentScreen) {
                    ScreenState.LOGIN -> {
                        LoginScreen(
                            repository = repository,
                            onLoginSuccess = { user ->
                                currentUser = user
                                currentScreen = ScreenState.MAIN_APP
                            },
                            onNavigateToRegister = {
                                currentScreen = ScreenState.REGISTER
                            }
                        )
                    }

                    ScreenState.REGISTER -> {
                        RegisterScreen(
                            repository = repository,
                            onRegisterSuccess = {
                                currentScreen = ScreenState.LOGIN
                            },
                            onNavigateBackToLogin = {
                                currentScreen = ScreenState.LOGIN
                            }
                        )
                    }

                    ScreenState.MAIN_APP -> {
                        currentUser?.let { user ->
                            MainContainerScreen(
                                currentUser = user,
                                repository = repository,
                                onLogout = {
                                    currentUser = null
                                    currentScreen = ScreenState.LOGIN
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
