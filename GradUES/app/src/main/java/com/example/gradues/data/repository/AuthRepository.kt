package com.example.gradues.data.repository

import com.example.gradues.data.dao.OpcionMenuDao
import com.example.gradues.data.dao.UsuarioDao
import com.example.gradues.data.entities.OpcionMenu
import com.example.gradues.data.entities.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val usuarioDao: UsuarioDao,
    private val opcionMenuDao: OpcionMenuDao
) {
    suspend fun login(idUsuario: String, contra: String): Usuario? = withContext(Dispatchers.IO) {
        usuarioDao.login(idUsuario.trim(), contra.trim())
    }

    suspend fun getMenuPorRol(nombreRol: String): List<OpcionMenu> = withContext(Dispatchers.IO) {
        opcionMenuDao.getOpcionesPorRol(nombreRol)
    }
}