package com.example.gradues.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradues.data.entities.OpcionMenu
import com.example.gradues.data.entities.Usuario
import com.example.gradues.data.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle    : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: Usuario, val menu: List<OpcionMenu>) : LoginState()
    data class Error(val mensaje: String) : LoginState()
}

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableLiveData<LoginState>(LoginState.Idle)
    val state: LiveData<LoginState> = _state

    fun login(idUsuario: String, contra: String) {
        if (idUsuario.isBlank() || contra.isBlank()) {
            _state.value = LoginState.Error("Ingresa tu ID y contraseña.")
            return
        }

        _state.value = LoginState.Loading

        viewModelScope.launch {
            val usuario = repo.login(idUsuario, contra)
            if (usuario == null) {
                _state.value = LoginState.Error("Usuario o contraseña incorrectos.")
            } else {
                val menu = repo.getMenuPorRol(usuario.NombreRol)
                _state.value = LoginState.Success(usuario, menu)
            }
        }
    }
}