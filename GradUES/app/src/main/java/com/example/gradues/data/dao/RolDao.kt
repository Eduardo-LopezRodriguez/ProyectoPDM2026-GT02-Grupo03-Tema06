package com.example.gradues.data.dao

import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.entities.Rol

class RolDao(private val dbHelper: DatabaseHelper) {

    /**
     * Inserta un nuevo rol en la tabla Roles.
     */
    fun insertar(rol: Rol) {
        val db = dbHelper.writableDatabase
        val sql = "INSERT OR IGNORE INTO Roles (NombreRol, DescripRol) VALUES (?, ?)"
        db.execSQL(sql, arrayOf(rol.NombreRol, rol.DescripRol))
    }

    /**
     * Busca un rol por su nombre.
     * Retorna el objeto Rol si lo encuentra, o null si no existe.
     */
    fun buscarPorNombre(nombreRol: String): Rol? {
        val db = dbHelper.readableDatabase
        val sql = "SELECT * FROM Roles WHERE NombreRol = ? LIMIT 1"
        val cursor = db.rawQuery(sql, arrayOf(nombreRol))

        var rol: Rol? = null
        if (cursor.moveToFirst()) {
            rol = Rol(
                NombreRol = cursor.getString(0),
                DescripRol = cursor.getString(1)
            )
        }
        cursor.close()
        return rol
    }
}