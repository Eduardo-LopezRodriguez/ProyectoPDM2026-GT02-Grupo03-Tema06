package com.example.gradues.data.dao

import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.entities.OpcionMenu

class OpcionMenuDao(private val dbHelper: DatabaseHelper) {

    fun getOpcionesPorRol(nombreRol: String): List<OpcionMenu> {
        val lista = mutableListOf<OpcionMenu4>()
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT om.* FROM OpcionesMenu om
            INNER JOIN Roles_OpcionesMenu rom ON om.IdOpcion = rom.IdPermisoOpcion
            WHERE rom.NombreRol = ?
        """
        val cursor = db.rawQuery(sql, arrayOf(nombreRol))
        if (cursor.moveToFirst()) {
            do {
                lista.add(OpcionMenu(
                    cursor.getString(0),
                    cursor.getString(1)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}