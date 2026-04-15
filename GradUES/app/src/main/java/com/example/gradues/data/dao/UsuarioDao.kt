package com.example.gradues.data.dao

import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.entities.Usuario

class UsuarioDao(private val dbHelper: DatabaseHelper) {
    fun login(idUsuario: String, contra: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Usuario WHERE IdUsuario = ? AND Contra = ? LIMIT 1",
            arrayOf(idUsuario, contra)
        )
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3))
        }
        cursor.close()
        return usuario
    }
}