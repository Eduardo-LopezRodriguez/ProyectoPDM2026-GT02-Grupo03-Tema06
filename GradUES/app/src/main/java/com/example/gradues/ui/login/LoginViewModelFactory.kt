package com.example.gradues.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gradues.data.repository.AuthRepository

class LoginViewModelFactory(private val repo: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repo) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}