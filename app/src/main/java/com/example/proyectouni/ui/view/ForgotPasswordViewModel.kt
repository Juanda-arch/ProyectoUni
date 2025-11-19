package com.example.proyectouni.ui.view



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class ForgotPasswordViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ForgotPasswordUiState(isLoading = true)

                // Validar email
                if (email.isBlank()) {
                    _uiState.value = ForgotPasswordUiState(
                        isLoading = false,
                        errorMessage = "Por favor ingresa tu correo electrónico"
                    )
                    return@launch
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _uiState.value = ForgotPasswordUiState(
                        isLoading = false,
                        errorMessage = "Por favor ingresa un correo electrónico válido"
                    )
                    return@launch
                }

                // Enviar email de recuperación
                auth.sendPasswordResetEmail(email.trim()).await()

                _uiState.value = ForgotPasswordUiState(
                    isLoading = false,
                    isSuccess = true
                )

            } catch (e: Exception) {
                _uiState.value = ForgotPasswordUiState(
                    isLoading = false,
                    errorMessage = handleFirebaseError(e)
                )
            }
        }
    }

    private fun handleFirebaseError(exception: Exception): String {
        return when {
            exception.message?.contains("no user record") == true ->
                "No existe una cuenta con este correo electrónico"
            exception.message?.contains("invalid-email") == true ->
                "El formato del correo electrónico no es válido"
            exception.message?.contains("network error") == true ->
                "Error de conexión. Verifica tu internet"
            exception.message?.contains("too many requests") == true ->
                "Demasiados intentos. Por favor intenta más tarde"
            else -> "Error al enviar el correo: ${exception.message ?: "Desconocido"}"
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}