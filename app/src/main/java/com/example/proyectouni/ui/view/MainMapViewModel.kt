package com.example.proyectouni.ui.view

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class MainMapUiState(
    val isLoading: Boolean = false,
    val currentUser: UserProfile? = null,
    val isLoggedOut: Boolean = false
)

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val city: String = "",
    val initials: String = ""
)

class MainMapViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _uiState = MutableStateFlow(MainMapUiState())
    val uiState: StateFlow<MainMapUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                _uiState.value = MainMapUiState(isLoading = true)

                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Obtener datos del usuario desde Firestore
                    val document = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    if (document.exists()) {
                        val name = document.getString("name") ?: currentUser.displayName ?: "Usuario"
                        val username = document.getString("username") ?: ""
                        val email = document.getString("email") ?: currentUser.email ?: ""
                        val city = document.getString("city") ?: ""

                        // Generar iniciales (primeras letras del nombre)
                        val initials = name.split(" ")
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.uppercase() }
                            .joinToString("")
                            .ifEmpty { "U" }

                        val userProfile = UserProfile(
                            userId = currentUser.uid,
                            name = name,
                            username = username,
                            email = email,
                            city = city,
                            initials = initials
                        )

                        _uiState.value = MainMapUiState(
                            isLoading = false,
                            currentUser = userProfile
                        )
                    } else {
                        // Si no hay datos en Firestore, usar datos de Auth
                        val initials = (currentUser.displayName ?: "U")
                            .split(" ")
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.uppercase() }
                            .joinToString("")

                        val userProfile = UserProfile(
                            userId = currentUser.uid,
                            name = currentUser.displayName ?: "Usuario",
                            username = "",
                            email = currentUser.email ?: "",
                            city = "",
                            initials = initials
                        )

                        _uiState.value = MainMapUiState(
                            isLoading = false,
                            currentUser = userProfile
                        )
                    }
                } else {
                    // No hay usuario logueado
                    _uiState.value = MainMapUiState(
                        isLoading = false,
                        isLoggedOut = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = MainMapUiState(
                    isLoading = false,
                    isLoggedOut = true
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _uiState.value = MainMapUiState(
                    isLoading = false,
                    isLoggedOut = true
                )
            } catch (e: Exception) {
                // Manejar error si es necesario
            }
        }
    }

    fun refreshUserData() {
        loadCurrentUser()
    }
}