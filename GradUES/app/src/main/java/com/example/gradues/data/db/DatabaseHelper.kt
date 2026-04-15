package com.example.gradues.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "gradues.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear Tablas
        db.execSQL("CREATE TABLE Roles (NombreRol TEXT PRIMARY KEY, DescripRol TEXT)")

        db.execSQL("CREATE TABLE OpcionesMenu (IdOpcion TEXT PRIMARY KEY, DescripOpcion TEXT)")

        db.execSQL("""
            CREATE TABLE Usuario (
                IdUsuario TEXT PRIMARY KEY, 
                NombreUsuario TEXT, 
                Contra TEXT, 
                NombreRol TEXT,
                FOREIGN KEY(NombreRol) REFERENCES Roles(NombreRol)
            )
        """)

        db.execSQL("""
            CREATE TABLE Roles_OpcionesMenu (
                NombreRol TEXT, 
                IdPermisoOpcion TEXT,
                PRIMARY KEY (NombreRol, IdPermisoOpcion),
                FOREIGN KEY(NombreRol) REFERENCES Roles(NombreRol),
                FOREIGN KEY(IdPermisoOpcion) REFERENCES OpcionesMenu(IdOpcion)
            )
        """)

        seedDatabase(db)
    }

    private fun seedDatabase(db: SQLiteDatabase) {
        // Roles
        db.execSQL("INSERT OR IGNORE INTO Roles VALUES ('Docente', 'Docente asesor / administrador')")
        db.execSQL("INSERT OR IGNORE INTO Roles VALUES ('Alumno', 'Estudiante de la UES')")

        // Usuarios
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('CG24001', 'Carlos García', '1234', 'Docente')")
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('RR24001', 'Rosa Ramos', '4567', 'Alumno')")

        // Opciones Menú
        //val opciones = listOf(
          //  "D01" to "Gestionar Trabajos", "D02" to "Revisar Avances", "D03" to "Aprobar Trabajo",
            //"D04" to "Gestionar Alumnos", "D05" to "Reportes", "A01" to "Mi Trabajo",
            //"A02" to "Subir Avance", "A03" to "Ver Observaciones", "A04" to "Mi Perfil"
        //)
        //opciones.forEach { db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('${it.first}', '${it.second}')") }

        // Permisos
        //val permisos = listOf(
           // "Docente" to "D01", "Docente" to "D02", "Docente" to "D03", "Docente" to "D04", "Docente" to "D05",
           // "Alumno" to "A01", "Alumno" to "A02", "Alumno" to "A03", "Alumno" to "A04"
       // )
        //permisos.forEach { db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('${it.first}', '${it.second}')") }
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS Roles_OpcionesMenu")
        db.execSQL("DROP TABLE IF EXISTS Usuario")
        db.execSQL("DROP TABLE IF EXISTS OpcionesMenu")
        db.execSQL("DROP TABLE IF EXISTS Roles")
        onCreate(db)
    }
}