package com.example.gradues.data.dao

import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.model.DetalleTrabajoAlumnoModel

class DetalleTrabajoAlumnoDao(
    private val dbHelper: DatabaseHelper
) {

    fun obtenerDetalleAlumno(idUsuario: String): DetalleTrabajoAlumnoModel? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT
                u.IdUsuario AS IdUsuario,
                TRIM(
                    COALESCE(a.PrimerNombre, '') || ' ' ||
                    COALESCE(a.SegundoNombre || ' ', '') ||
                    COALESCE(a.PrimerApellido, '') || ' ' ||
                    COALESCE(a.SegundoApellido, '')
                ) AS NombreCompleto,
                COALESCE(a.Carnet, u.IdUsuario) AS Carnet,
                tg.IdTrabajoGraduacion AS IdTrabajoGraduacion,
                tg.IdModalidad AS IdModalidad,
                COALESCE(m.TipoModalidad, 'Sin modalidad') AS Modalidad,
                COALESCE(tg.Nombre, 'Sin trabajo de graduación asignado') AS TituloTrabajo,
                COALESCE(tg.Estado, 'Sin estado') AS EstadoTrabajo,
                TRIM(
                    COALESCE(d.PrimerNombre, '') || ' ' ||
                    COALESCE(d.SegundoNombre || ' ', '') ||
                    COALESCE(d.PrimerApellido, '') || ' ' ||
                    COALESCE(d.SegundoApellido, '')
                ) AS DocenteDirector,
                CASE
                    WHEN tg.IdModalidad = 1 AND gtgi.IdGrupoTGI IS NOT NULL
                        THEN 'Grupo de Investigación ' || printf('%02d', gtgi.IdGrupoTGI)
                    WHEN tg.IdModalidad = 2 AND gtge.IdGrupoTGE IS NOT NULL
                        THEN 'Curso de Especialización ' || printf('%02d', gtge.IdGrupoTGE)
                    WHEN tg.IdModalidad = 3 AND pp.IdProyectoPasantia IS NOT NULL
                        THEN 'Proyecto de Pasantía ' || printf('%02d', pp.IdProyectoPasantia)
                    WHEN tg.IdModalidad = 1
                        THEN 'Grupo de Investigación pendiente'
                    WHEN tg.IdModalidad = 2
                        THEN 'Curso de Especialización pendiente'
                    WHEN tg.IdModalidad = 3
                        THEN 'Proyecto de Pasantía pendiente'
                    ELSE 'Sin grupo asignado'
                END AS NombreGrupo,
                emp.Nombre AS Empresa,
                per.Nombre AS Personero,
                mr.Estado AS EstadoMemoria,
                pp.IdProyectoPasantia AS IdProyectoPasantia
            FROM Usuario u
            LEFT JOIN Alumno a
                ON a.IdUsuario = u.IdUsuario
            LEFT JOIN SolicitudModalidad sm
                ON sm.IdUsuario = u.IdUsuario
            LEFT JOIN TrabajoGraduacion tg
                ON tg.IdTrabajoGraduacion = COALESCE(sm.IdTrabajoGraduacion, a.IdTrabajoGraduacion)
            LEFT JOIN Modalidad m
                ON m.IdModalidad = tg.IdModalidad
            LEFT JOIN Docente d
                ON d.IdUsuario = tg.IdUsuarioDirector
            LEFT JOIN GrupoTGI gtgi
                ON gtgi.IdTrabajoGraduacion = tg.IdTrabajoGraduacion
            LEFT JOIN GrupoTGE gtge
                ON gtge.IdTrabajoGraduacion = tg.IdTrabajoGraduacion
            LEFT JOIN ProyectoPasantia pp
                ON pp.IdTrabajoGraduacion = tg.IdTrabajoGraduacion
            LEFT JOIN Empresa emp
                ON emp.IdEmpresa = pp.IdEmpresa
            LEFT JOIN Personero per
                ON per.IdEmpresa = emp.IdEmpresa
            LEFT JOIN MemoriaResumen mr
                ON mr.IdProyectoPasantia = pp.IdProyectoPasantia
            WHERE u.IdUsuario = ?
            ORDER BY sm.IdSolicitudModalidad DESC
            LIMIT 1
            """.trimIndent(),
            arrayOf(idUsuario)
        )

        var detalle: DetalleTrabajoAlumnoModel? = null

        if (cursor.moveToFirst()) {
            val idTrabajoGraduacion = getNullableInt(cursor, "IdTrabajoGraduacion")
            val idModalidad = getNullableInt(cursor, "IdModalidad")
            val idProyectoPasantia = getNullableInt(cursor, "IdProyectoPasantia")

            val nombreCompleto = getString(cursor, "NombreCompleto").ifBlank { "Alumno" }
            val docenteDirector = getString(cursor, "DocenteDirector").ifBlank { "Sin docente asignado" }

            detalle = DetalleTrabajoAlumnoModel(
                idUsuario = getString(cursor, "IdUsuario"),
                nombreCompleto = nombreCompleto,
                carnet = getString(cursor, "Carnet"),
                modalidad = getString(cursor, "Modalidad"),
                idModalidad = idModalidad,
                idTrabajoGraduacion = idTrabajoGraduacion,
                tituloTrabajo = getString(cursor, "TituloTrabajo"),
                estadoTrabajo = getString(cursor, "EstadoTrabajo"),
                docenteDirector = docenteDirector,
                nombreGrupo = getString(cursor, "NombreGrupo"),
                empresa = getNullableString(cursor, "Empresa"),
                personero = getNullableString(cursor, "Personero"),
                estadoMemoria = getNullableString(cursor, "EstadoMemoria"),
                bitacorasRegistradas = contarBitacoras(idProyectoPasantia),
                integrantes = obtenerIntegrantes(idTrabajoGraduacion),
                propuestas = obtenerPropuestas(idTrabajoGraduacion),
                notas = obtenerNotas(idTrabajoGraduacion)
            )
        }

        cursor.close()
        return detalle
    }

    private fun obtenerIntegrantes(idTrabajoGraduacion: Int?): List<String> {
        if (idTrabajoGraduacion == null) return emptyList()

        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()

        val cursor = db.rawQuery(
            """
            SELECT
                Carnet,
                TRIM(
                    COALESCE(PrimerNombre, '') || ' ' ||
                    COALESCE(SegundoNombre || ' ', '') ||
                    COALESCE(PrimerApellido, '') || ' ' ||
                    COALESCE(SegundoApellido, '')
                ) AS NombreCompleto
            FROM Alumno
            WHERE IdTrabajoGraduacion = ?
            ORDER BY PrimerApellido, PrimerNombre
            """.trimIndent(),
            arrayOf(idTrabajoGraduacion.toString())
        )

        while (cursor.moveToNext()) {
            val carnet = getString(cursor, "Carnet")
            val nombre = getString(cursor, "NombreCompleto").ifBlank { "Sin nombre" }
            lista.add("$nombre - $carnet")
        }

        cursor.close()
        return lista
    }

    private fun obtenerPropuestas(idTrabajoGraduacion: Int?): List<String> {
        if (idTrabajoGraduacion == null) return emptyList()

        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()

        val cursor = db.rawQuery(
            """
            SELECT Titulo, Estado
            FROM PropuestaPerfil
            WHERE IdTrabajoGraduacion = ?
            ORDER BY IdPropuestaPerfil
            """.trimIndent(),
            arrayOf(idTrabajoGraduacion.toString())
        )

        while (cursor.moveToNext()) {
            val titulo = getString(cursor, "Titulo")
            val estado = getString(cursor, "Estado")
            lista.add("$titulo — $estado")
        }

        cursor.close()
        return lista
    }

    private fun obtenerNotas(idTrabajoGraduacion: Int?): List<String> {
        if (idTrabajoGraduacion == null) return emptyList()

        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()

        val cursor = db.rawQuery(
            """
            SELECT NumeroEtapa, Nota
            FROM NotaEtapa
            WHERE IdTrabajoGraduacion = ?
            ORDER BY NumeroEtapa
            """.trimIndent(),
            arrayOf(idTrabajoGraduacion.toString())
        )

        while (cursor.moveToNext()) {
            val etapa = cursor.getInt(cursor.getColumnIndexOrThrow("NumeroEtapa"))
            val notaIndex = cursor.getColumnIndexOrThrow("Nota")
            val nota = if (cursor.isNull(notaIndex)) "---" else "%.2f".format(cursor.getDouble(notaIndex))
            lista.add("Etapa $etapa: $nota")
        }

        cursor.close()
        return lista
    }

    private fun contarBitacoras(idProyectoPasantia: Int?): Int {
        if (idProyectoPasantia == null) return 0

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT COUNT(*) AS Total
            FROM Bitacora
            WHERE IdProyectoPasantia = ?
            """.trimIndent(),
            arrayOf(idProyectoPasantia.toString())
        )

        var total = 0

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("Total"))
        }

        cursor.close()
        return total
    }

    private fun getString(cursor: android.database.Cursor, column: String): String {
        val index = cursor.getColumnIndexOrThrow(column)
        return if (cursor.isNull(index)) "" else cursor.getString(index)
    }

    private fun getNullableString(cursor: android.database.Cursor, column: String): String? {
        val index = cursor.getColumnIndexOrThrow(column)
        return if (cursor.isNull(index)) null else cursor.getString(index)
    }

    private fun getNullableInt(cursor: android.database.Cursor, column: String): Int? {
        val index = cursor.getColumnIndexOrThrow(column)
        return if (cursor.isNull(index)) null else cursor.getInt(index)
    }
}