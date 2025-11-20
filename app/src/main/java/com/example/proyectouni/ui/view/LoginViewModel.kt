package com.example.proyectouni.ui.screens

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectouni.ui.view.UserData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isModerator: Boolean = false,
    val errorMessage: String? = null,
    val userId: String? = null,
    val userData: UserData? = null
)

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Credenciales del moderador
    private val moderatorEmail = "moderador@unilocal.com"
    private val moderatorPassword = "Moderador123"

    // Web Client ID de tu proyecto Firebase
    // IMPORTANTE: Reemplazar con tu propio Web Client ID
    private val webClientId = "TU_WEB_CLIENT_ID.apps.googleusercontent.com"

    init {
        // Verificar si ya hay una sesión activa
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    val userData = getUserData(currentUser.uid)
                    _uiState.value = LoginUiState(
                        isSuccess = true,
                        userId = currentUser.uid,
                        userData = userData,
                        isModerator = currentUser.email == moderatorEmail
                    )
                } catch (e: Exception) {
                    // Si hay error al obtener datos, cerrar sesión
                    auth.signOut()
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState(isLoading = true)

                // Validar campos
                if (email.isBlank() || password.isBlank()) {
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        errorMessage = "Por favor completa todos los campos"
                    )
                    return@launch
                }

                // Verificar si es el moderador
                val isModerator = email.trim().equals(moderatorEmail, ignoreCase = true) &&
                        password == moderatorPassword

                // Intentar iniciar sesión con Firebase Auth
                val authResult = auth.signInWithEmailAndPassword(
                    email.trim(),
                    password
                ).await()

                val userId = authResult.user?.uid
                if (userId != null) {
                    // Obtener datos del usuario desde Firestore
                    val userData = getUserData(userId)

                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = true,
                        isModerator = isModerator,
                        userId = userId,
                        userData = userData
                    )
                } else {
                    throw Exception("Error al obtener el usuario")
                }

            } catch (e: Exception) {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    errorMessage = handleFirebaseError(e)
                )
            }
        }
    }

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState(isLoading = true)

                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                // Autenticar con Firebase usando el token de Google
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                val userId = authResult.user?.uid
                if (userId != null) {
                    // Verificar si el usuario ya existe en Firestore
                    var userData = getUserData(userId)

                    // Si no existe, crear perfil básico
                    if (userData == null) {
                        val user = authResult.user
                        userData = UserData(
                            name = user?.displayName ?: "",
                            username = user?.email?.substringBefore("@") ?: "",
                            email = user?.email ?: "",
                            city = "",
                            createdAt = System.currentTimeMillis()
                        )

                        // Guardar en Firestore
                        firestore.collection("users")
                            .document(userId)
                            .set(userData)
                            .await()
                    }

                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = true,
                        isModerator = false,
                        userId = userId,
                        userData = userData
                    )
                } else {
                    throw Exception("Error al obtener el usuario")
                }

            } catch (e: GetCredentialException) {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    errorMessage = "Error al iniciar sesión con Google. Intenta nuevamente"
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    errorMessage = handleFirebaseError(e)
                )
            }
        }
    }

    private suspend fun getUserData(userId: String): UserData? {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                UserData(
                    name = document.getString("name") ?: "",
                    username = document.getString("username") ?: "",
                    email = document.getString("email") ?: "",
                    city = document.getString("city") ?: "",
                    createdAt = document.getLong("createdAt") ?: 0L
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = LoginUiState()
    }

    private fun handleFirebaseError(exception: Exception): String {
        return when {
            exception.message?.contains("no user record") == true ||
                    exception.message?.contains("wrong password") == true ||
                    exception.message?.contains("invalid-credential") == true ->
                "Credenciales inválidas. Verifica tu email y contraseña"
            exception.message?.contains("network error") == true ->
                "Error de conexión. Verifica tu internet"
            exception.message?.contains("too many requests") == true ->
                "Demasiados intentos. Intenta más tarde"
            exception.message?.contains("user-disabled") == true ->
                "Esta cuenta ha sido deshabilitada"
            exception.message?.contains("invalid-email") == true ->
                "El formato del email no es válido"
            else -> "Error al iniciar sesión: ${exception.message ?: "Desconocido"}"
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }

    fun fillModeratorCredentials(): Pair<String, String> {
        return Pair(moderatorEmail, moderatorPassword)
    }
}