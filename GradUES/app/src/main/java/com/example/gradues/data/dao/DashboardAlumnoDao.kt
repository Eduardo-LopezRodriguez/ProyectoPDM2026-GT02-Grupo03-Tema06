package com.example.gradues.data.dao

import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.model.DashboardAlumnoModel

class DashboardAlumnoDao(
    private val dbHelper: DatabaseHelper
) {

    fun obtenerDashboardAlumno(idUsuario: String): DashboardAlumnoModel? {
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
                COALESCE(tg.Nombre, 'Sin trabajo de graduación asignado') AS NombreTrabajo,
                COALESCE(m.TipoModalidad, 'Sin modalidad asignada') AS Modalidad,
                CASE
                    WHEN COALESCE(sm.IdModalidad, tg.IdModalidad) = 1 AND gtgi.IdGrupoTGI IS NOT NULL
                        THEN 'Grupo de Investigación ' || printf('%02d', gtgi.IdGrupoTGI)
                    WHEN COALESCE(sm.IdModalidad, tg.IdModalidad) = 2 AND gtge.IdGrupoTGE IS NOT NULL
                        THEN 'Curso de Especialización ' || printf('%02d', gtge.IdGrupoTGE)
                    WHEN COALESCE(sm.IdModalidad, tg.IdModalidad) = 3 AND pp.IdProyectoPasantia IS NOT NULL
                        THEN 'Proyecto de Pasantía ' || printf('%02d', pp.IdProyectoPasantia)
                    WHEN COALESCE(sm.IdModalidad, tg.IdModalidad) = 1
                        THEN 'Grupo de Investigación pendiente'
                    WHEN COALESCE(sm.IdModalidad, tg.IdModalidad) = 2
                        THEN 'Curso de Especialización pendiente'
                    WHEN COALESCE(sm.IdModalidad, tg.IdModalidad) = 3
                        THEN 'Proyecto de Pasantía pendiente'
                    ELSE 'Sin grupo asignado'
                END AS NombreGrupo
            FROM Usuario u
            LEFT JOIN Alumno a
                ON a.IdUsuario = u.IdUsuario
            LEFT JOIN SolicitudModalidad sm
                ON sm.IdUsuario = u.IdUsuario
            LEFT JOIN TrabajoGraduacion tg
                ON tg.IdTrabajoGraduacion = COALESCE(sm.IdTrabajoGraduacion, a.IdTrabajoGraduacion)
            LEFT JOIN Modalidad m
                ON m.IdModalidad = COALESCE(sm.IdModalidad, tg.IdModalidad)
            LEFT JOIN GrupoTGI gtgi
                ON gtgi.IdTrabajoGraduacion = tg.IdTrabajoGraduacion
            LEFT JOIN GrupoTGE gtge
                ON gtge.IdTrabajoGraduacion = tg.IdTrabajoGraduacion
            LEFT JOIN ProyectoPasantia pp
                ON pp.IdTrabajoGraduacion = tg.IdTrabajoGraduacion
            WHERE u.IdUsuario = ?
            ORDER BY sm.IdSolicitudModalidad DESC
            LIMIT 1
            """.trimIndent(),
            arrayOf(idUsuario)
        )

        var model: DashboardAlumnoModel? = null

        if (cursor.moveToFirst()) {
            val idTrabajoGraduacionIndex = cursor.getColumnIndexOrThrow("IdTrabajoGraduacion")
            val idTrabajoGraduacion = if (cursor.isNull(idTrabajoGraduacionIndex)) {
                null
            } else {
                cursor.getInt(idTrabajoGraduacionIndex)
            }

            val nombreCompleto = cursor
                .getString(cursor.getColumnIndexOrThrow("NombreCompleto"))
                .ifBlank { "Alumno" }

            val nombreTrabajo = cursor.getString(cursor.getColumnIndexOrThrow("NombreTrabajo"))
            val nombreGrupo = cursor.getString(cursor.getColumnIndexOrThrow("NombreGrupo"))
            val modalidad = cursor.getString(cursor.getColumnIndexOrThrow("Modalidad"))
            val carnet = cursor.getString(cursor.getColumnIndexOrThrow("Carnet"))

            model = DashboardAlumnoModel(
                idUsuario = cursor.getString(cursor.getColumnIndexOrThrow("IdUsuario")),
                idTrabajoGraduacion = idTrabajoGraduacion,
                nombreCompleto = nombreCompleto,
                correo = "${carnet.lowercase()}@ues.edu.sv",
                modalidad = modalidad,
                nombreGrupo = nombreGrupo,
                temaGrupo = nombreTrabajo,
                nombreProyectoEstado = nombreTrabajo,
                estadoResumen = obtenerResumenPropuestas(idTrabajoGraduacion),
                notaEtapa1 = obtenerNotaEtapa(idTrabajoGraduacion, 1),
                notaEtapa2 = obtenerNotaEtapa(idTrabajoGraduacion, 2),
                notaEtapa3 = obtenerNotaEtapa(idTrabajoGraduacion, 3),
                notaEtapa4 = obtenerNotaEtapa(idTrabajoGraduacion, 4)
            )
        }

        cursor.close()
        return model
    }

    private fun obtenerResumenPropuestas(idTrabajoGraduacion: Int?): String {
        if (idTrabajoGraduacion == null) {
            return "Estado: sin trabajo asignado"
        }

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT
                COUNT(*) AS Total,
                SUM(CASE 
                    WHEN Estado IN ('Seleccionada', 'Aprobada') THEN 1 
                    ELSE 0 
                END) AS Aprobadas,
                SUM(CASE 
                    WHEN Estado IN ('Descartada', 'Denegada', 'Rechazada') THEN 1 
                    ELSE 0 
                END) AS Denegadas,
                SUM(CASE 
                    WHEN Estado IN ('Con observación', 'Con observacion') THEN 1 
                    ELSE 0 
                END) AS Observaciones
            FROM PropuestaPerfil
            WHERE IdTrabajoGraduacion = ?
            """.trimIndent(),
            arrayOf(idTrabajoGraduacion.toString())
        )

        var resumen = "Estado: sin propuestas registradas"

        if (cursor.moveToFirst()) {
            val total = cursor.getInt(cursor.getColumnIndexOrThrow("Total"))
            val aprobadas = cursor.getInt(cursor.getColumnIndexOrThrow("Aprobadas"))
            val denegadas = cursor.getInt(cursor.getColumnIndexOrThrow("Denegadas"))
            val observaciones = cursor.getInt(cursor.getColumnIndexOrThrow("Observaciones"))

            resumen = if (total > 0) {
                "Estado: $aprobadas aprobada(s), $denegadas denegada(s), $observaciones con observación"
            } else {
                "Estado: sin propuestas registradas"
            }
        }

        cursor.close()
        return resumen
    }

    private fun obtenerNotaEtapa(idTrabajoGraduacion: Int?, numeroEtapa: Int): Double? {
        if (idTrabajoGraduacion == null) {
            return null
        }

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT Nota
            FROM NotaEtapa
            WHERE IdTrabajoGraduacion = ?
              AND NumeroEtapa = ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(idTrabajoGraduacion.toString(), numeroEtapa.toString())
        )

        var nota: Double? = null

        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow("Nota")
            nota = if (cursor.isNull(index)) null else cursor.getDouble(index)
        }

        cursor.close()
        return nota
    }
}