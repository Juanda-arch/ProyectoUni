package com.example.proyectouni.ui.view


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class UserData(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val city: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registerUser(
        name: String,
        username: String,
        email: String,
        password: String,
        city: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = RegisterUiState(isLoading = true)

                // Validar campos
                if (!validateFields(name, username, email, password, city)) {
                    _uiState.value = RegisterUiState(
                        isLoading = false,
                        errorMessage = "Por favor completa todos los campos correctamente"
                    )
                    return@launch
                }

                // Verificar si el username ya existe
                val usernameExists = checkUsernameExists(username)
                if (usernameExists) {
                    _uiState.value = RegisterUiState(
                        isLoading = false,
                        errorMessage = "El nombre de usuario ya está en uso"
                    )
                    return@launch
                }

                // Crear usuario en Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid

                if (userId != null) {
                    // Guardar datos adicionales en Firestore
                    val userData = UserData(
                        name = name.trim(),
                        username = username.trim().lowercase(),
                        email = email.trim().lowercase(),
                        city = city
                    )

                    firestore.collection("users")
                        .document(userId)
                        .set(userData)
                        .await()

                    // Registro exitoso
                    _uiState.value = RegisterUiState(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    throw Exception("Error al crear el usuario")
                }

            } catch (e: Exception) {
                _uiState.value = RegisterUiState(
                    isLoading = false,
                    errorMessage = handleFirebaseError(e)
                )
            }
        }
    }

    private suspend fun checkUsernameExists(username: String): Boolean {
        return try {
            val result = firestore.collection("users")
                .whereEqualTo("username", username.trim().lowercase())
                .get()
                .await()
            !result.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    private fun validateFields(
        name: String,
        username: String,
        email: String,
        password: String,
        city: String
    ): Boolean {
        if (name.isBlank() || username.isBlank() || email.isBlank() ||
            password.isBlank() || city.isBlank()) {
            return false
        }

        // Validar email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }

        // Validar contraseña
        if (password.length < 8 ||
            !password.any { it.isUpperCase() } ||
            !password.any { it.isDigit() }) {
            return false
        }

        // Validar username (solo letras, números y guión bajo)
        if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            return false
        }

        return true
    }

    private fun handleFirebaseError(exception: Exception): String {
        return when {
            exception.message?.contains("email address is already in use") == true ->
                "Este correo electrónico ya está registrado"
            exception.message?.contains("network error") == true ->
                "Error de conexión. Verifica tu internet"
            exception.message?.contains("weak password") == true ->
                "La contraseña es muy débil"
            exception.message?.contains("invalid email") == true ->
                "El correo electrónico no es válido"
            else -> "Error al registrar: ${exception.message ?: "Desconocido"}"
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
    }
}