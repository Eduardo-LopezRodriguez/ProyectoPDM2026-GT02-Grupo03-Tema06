package com.example.gradues.data.dao

import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.entities.Usuario

class UsuarioDao(private val dbHelper: DatabaseHelper) {

    fun login(idUsuario: String, contra: String): Usuario? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT 
                IdUsuario,
                NombreUsuario,
                Contra,
                NombreRol
            FROM Usuario
            WHERE IdUsuario = ? 
              AND Contra = ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(idUsuario.trim(), contra.trim())
        )

        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                IdUsuario = cursor.getString(cursor.getColumnIndexOrThrow("IdUsuario")),
                NombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("NombreUsuario")),
                Contra = cursor.getString(cursor.getColumnIndexOrThrow("Contra")),
                NombreRol = cursor.getString(cursor.getColumnIndexOrThrow("NombreRol"))
            )
        }

        cursor.close()
        return usuario
    }

    fun buscarPorId(idUsuario: String): Usuario? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT 
                IdUsuario,
                NombreUsuario,
                Contra,
                NombreRol
            FROM Usuario
            WHERE IdUsuario = ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(idUsuario.trim())
        )

        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                IdUsuario = cursor.getString(cursor.getColumnIndexOrThrow("IdUsuario")),
                NombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("NombreUsuario")),
                Contra = cursor.getString(cursor.getColumnIndexOrThrow("Contra")),
                NombreRol = cursor.getString(cursor.getColumnIndexOrThrow("NombreRol"))
            )
        }

        cursor.close()
        return usuario
    }
}