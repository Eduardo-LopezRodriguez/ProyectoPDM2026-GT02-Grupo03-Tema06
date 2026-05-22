package com.example.gradues.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "gradues.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        crearTablasBase(db)
        crearTablasAcademicas(db)
        crearTablasInvestigacion(db)
        crearTablasEspecializacion(db)
        crearTablasPasantia(db)
        crearTablasDocumentosYNotas(db)

        insertarDatosIniciales(db)
    }

    private fun crearTablasBase(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Roles (
                NombreRol TEXT PRIMARY KEY,
                DescripRol TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS OpcionesMenu (
                IdOpcion TEXT PRIMARY KEY,
                DescripOpcion TEXT NOT NULL,
                Modulo TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Usuario (
                IdUsuario TEXT PRIMARY KEY,
                NombreUsuario TEXT NOT NULL,
                Contra TEXT NOT NULL,
                NombreRol TEXT NOT NULL,
                FOREIGN KEY (NombreRol) REFERENCES Roles(NombreRol)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Roles_OpcionesMenu (
                NombreRol TEXT NOT NULL,
                IdPermisoOpcion TEXT NOT NULL,
                PRIMARY KEY (NombreRol, IdPermisoOpcion),
                FOREIGN KEY (NombreRol) REFERENCES Roles(NombreRol),
                FOREIGN KEY (IdPermisoOpcion) REFERENCES OpcionesMenu(IdOpcion)
            )
            """.trimIndent()
        )
    }

    private fun crearTablasAcademicas(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Modalidad (
                IdModalidad INTEGER PRIMARY KEY,
                TipoModalidad TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS TrabajoGraduacion (
                IdTrabajoGraduacion INTEGER PRIMARY KEY AUTOINCREMENT,
                IdUsuarioDirector TEXT,
                IdModalidad INTEGER NOT NULL,
                Nombre TEXT NOT NULL,
                Estado TEXT NOT NULL DEFAULT 'Activo',
                FOREIGN KEY (IdUsuarioDirector) REFERENCES Usuario(IdUsuario),
                FOREIGN KEY (IdModalidad) REFERENCES Modalidad(IdModalidad)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Alumno (
                IdUsuario TEXT PRIMARY KEY,
                IdTrabajoGraduacion INTEGER,
                Carnet TEXT NOT NULL,
                PrimerNombre TEXT NOT NULL,
                SegundoNombre TEXT,
                PrimerApellido TEXT NOT NULL,
                SegundoApellido TEXT,
                Carrera TEXT,
                FOREIGN KEY (IdUsuario) REFERENCES Usuario(IdUsuario),
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Docente (
                IdUsuario TEXT PRIMARY KEY,
                IdTrabajoGraduacion INTEGER,
                Dui TEXT,
                PrimerNombre TEXT NOT NULL,
                SegundoNombre TEXT,
                PrimerApellido TEXT NOT NULL,
                SegundoApellido TEXT,
                FOREIGN KEY (IdUsuario) REFERENCES Usuario(IdUsuario),
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS SolicitudModalidad (
                IdSolicitudModalidad INTEGER PRIMARY KEY AUTOINCREMENT,
                IdUsuario TEXT NOT NULL,
                IdModalidad INTEGER NOT NULL,
                IdTrabajoGraduacion INTEGER,
                FechaSolicitud TEXT NOT NULL,
                EstadoSolicitud TEXT NOT NULL,
                ObservacionSolicitud TEXT,
                FOREIGN KEY (IdUsuario) REFERENCES Usuario(IdUsuario),
                FOREIGN KEY (IdModalidad) REFERENCES Modalidad(IdModalidad),
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion)
            )
            """.trimIndent()
        )
    }

    private fun crearTablasInvestigacion(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS GrupoTGI (
                IdGrupoTGI INTEGER PRIMARY KEY AUTOINCREMENT,
                IdTrabajoGraduacion INTEGER NOT NULL,
                FechaCreacion TEXT NOT NULL,
                FechaFinal TEXT,
                Estado TEXT NOT NULL DEFAULT 'Activo',
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS PropuestaPerfil (
                IdPropuestaPerfil INTEGER PRIMARY KEY AUTOINCREMENT,
                IdTrabajoGraduacion INTEGER NOT NULL,
                Titulo TEXT NOT NULL,
                Descripcion TEXT,
                Estado TEXT NOT NULL,
                FechaRegistro TEXT NOT NULL,
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion)
            )
            """.trimIndent()
        )
    }

    private fun crearTablasEspecializacion(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS GrupoTGE (
                IdGrupoTGE INTEGER PRIMARY KEY AUTOINCREMENT,
                IdTrabajoGraduacion INTEGER NOT NULL,
                FechaCreacion TEXT NOT NULL,
                FechaFinal TEXT,
                Estado TEXT NOT NULL DEFAULT 'Activo',
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS SubgrupoTGE (
                IdSubgrupoTGE INTEGER PRIMARY KEY AUTOINCREMENT,
                IdGrupoTGE INTEGER NOT NULL,
                TemaAsignado TEXT NOT NULL,
                Estado TEXT NOT NULL DEFAULT 'Activo',
                FOREIGN KEY (IdGrupoTGE) REFERENCES GrupoTGE(IdGrupoTGE)
            )
            """.trimIndent()
        )
    }

    private fun crearTablasPasantia(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Empresa (
                IdEmpresa INTEGER PRIMARY KEY AUTOINCREMENT,
                Nombre TEXT NOT NULL,
                Rubro TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Personero (
                IdPersonero INTEGER PRIMARY KEY AUTOINCREMENT,
                IdEmpresa INTEGER NOT NULL,
                Nombre TEXT NOT NULL,
                Cargo TEXT,
                FOREIGN KEY (IdEmpresa) REFERENCES Empresa(IdEmpresa)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ProyectoPasantia (
                IdProyectoPasantia INTEGER PRIMARY KEY AUTOINCREMENT,
                IdTrabajoGraduacion INTEGER NOT NULL,
                IdEmpresa INTEGER NOT NULL,
                FechaCreacion TEXT NOT NULL,
                FechaFinal TEXT,
                Estado TEXT NOT NULL DEFAULT 'Activo',
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion),
                FOREIGN KEY (IdEmpresa) REFERENCES Empresa(IdEmpresa)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS MemoriaResumen (
                IdMemoriaResumen INTEGER PRIMARY KEY AUTOINCREMENT,
                IdProyectoPasantia INTEGER NOT NULL,
                PeriodoInicio TEXT,
                PeriodoFinal TEXT,
                ContenidoResumen TEXT,
                Estado TEXT NOT NULL DEFAULT 'Pendiente',
                FOREIGN KEY (IdProyectoPasantia) REFERENCES ProyectoPasantia(IdProyectoPasantia)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Bitacora (
                IdBitacora INTEGER PRIMARY KEY AUTOINCREMENT,
                IdProyectoPasantia INTEGER NOT NULL,
                IdMemoriaResumen INTEGER,
                FechaActividad TEXT NOT NULL,
                DescripcionActividad TEXT NOT NULL,
                TotalHorasTrabajadas INTEGER NOT NULL,
                Estado TEXT NOT NULL DEFAULT 'Pendiente',
                FOREIGN KEY (IdProyectoPasantia) REFERENCES ProyectoPasantia(IdProyectoPasantia),
                FOREIGN KEY (IdMemoriaResumen) REFERENCES MemoriaResumen(IdMemoriaResumen)
            )
            """.trimIndent()
        )
    }

    private fun crearTablasDocumentosYNotas(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS EstadoDocumento (
                IdEstadoDocumento INTEGER PRIMARY KEY,
                Estado TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Documento (
                IdDocumento INTEGER PRIMARY KEY AUTOINCREMENT,
                IdTrabajoGraduacion INTEGER NOT NULL,
                IdEstadoDocumento INTEGER NOT NULL,
                Nota REAL,
                UrlDocumento TEXT,
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion),
                FOREIGN KEY (IdEstadoDocumento) REFERENCES EstadoDocumento(IdEstadoDocumento)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS NotaEtapa (
                IdNotaEtapa INTEGER PRIMARY KEY AUTOINCREMENT,
                IdTrabajoGraduacion INTEGER NOT NULL,
                NumeroEtapa INTEGER NOT NULL,
                Nota REAL,
                FOREIGN KEY (IdTrabajoGraduacion) REFERENCES TrabajoGraduacion(IdTrabajoGraduacion)
            )
            """.trimIndent()
        )
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase) {
        insertarRoles(db)
        insertarUsuarios(db)
        insertarModalidades(db)
        insertarMenu(db)
        insertarPermisos(db)
        insertarDatosAcademicos(db)
        insertarDatosInvestigacion(db)
        insertarDatosEspecializacion(db)
        insertarDatosPasantia(db)
        insertarEstadosDocumentos(db)
        insertarNotas(db)
    }

    private fun insertarRoles(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO Roles VALUES ('Administrador', 'Usuario administrador del sistema')")
        db.execSQL("INSERT OR IGNORE INTO Roles VALUES ('Docente', 'Docente asesor o encargado de revisión')")
        db.execSQL("INSERT OR IGNORE INTO Roles VALUES ('Alumno', 'Estudiante en proceso de graduación')")
        db.execSQL("INSERT OR IGNORE INTO Roles VALUES ('Jurado', 'Miembro jurado evaluador')")
    }

    private fun insertarUsuarios(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('ADMIN001', 'Administrador General', '1234', 'Administrador')")
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('CG24001', 'Ing. Cesar Augusto', '1234', 'Docente')")
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('LR21008', 'Eduardo Enrique López Rodríguez', '1234', 'Alumno')")
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('AP20025', 'Allan Augusto Anduray Portillo', '1234', 'Alumno')")
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('AC21011', 'Oscar Mauricio Ángel Córdova', '1234', 'Alumno')")
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('EM22001', 'Luisa Elizabeth Escobar Martínez', '1234', 'Alumno')")
        db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('HR23040', 'Eduardo Javier Hernández Regalado', '1234', 'Alumno')")
    }

    private fun insertarModalidades(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO Modalidad VALUES (1, 'Investigación')")
        db.execSQL("INSERT OR IGNORE INTO Modalidad VALUES (2, 'Curso de especialización')")
        db.execSQL("INSERT OR IGNORE INTO Modalidad VALUES (3, 'Pasantía profesional')")
    }

    private fun insertarMenu(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('DASH_DOC', 'Dashboard docente', 'Docente')")
        db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('GRUP_INV', 'Grupos de investigación', 'Investigación')")
        db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('CUR_ESP', 'Cursos de especialización', 'Especialización')")
        db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('PAS_DOC', 'Proyectos de pasantía', 'Pasantía')")

        db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('DASH_ALU', 'Dashboard alumno', 'Alumno')")
        db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('MI_GRUPO', 'Mi grupo', 'Alumno')")
        db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('MIS_NOTAS', 'Mis notas', 'Alumno')")
        db.execSQL("INSERT OR IGNORE INTO OpcionesMenu VALUES ('MIS_BIT', 'Mis bitácoras', 'Alumno')")
    }

    private fun insertarPermisos(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Docente', 'DASH_DOC')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Docente', 'GRUP_INV')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Docente', 'CUR_ESP')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Docente', 'PAS_DOC')")

        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Alumno', 'DASH_ALU')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Alumno', 'MI_GRUPO')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Alumno', 'MIS_NOTAS')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Alumno', 'MIS_BIT')")

        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Administrador', 'DASH_DOC')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Administrador', 'GRUP_INV')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Administrador', 'CUR_ESP')")
        db.execSQL("INSERT OR IGNORE INTO Roles_OpcionesMenu VALUES ('Administrador', 'PAS_DOC')")
    }

    private fun insertarDatosAcademicos(db: SQLiteDatabase) {
        db.execSQL(
            """
            INSERT OR IGNORE INTO TrabajoGraduacion 
            (IdTrabajoGraduacion, IdUsuarioDirector, IdModalidad, Nombre, Estado)
            VALUES (1, 'CG24001', 1, 'Sistema de seguimiento académico', 'Activo')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO TrabajoGraduacion 
            (IdTrabajoGraduacion, IdUsuarioDirector, IdModalidad, Nombre, Estado)
            VALUES (2, 'CG24001', 2, 'Curso de Desarrollo Web', 'Activo')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO TrabajoGraduacion 
            (IdTrabajoGraduacion, IdUsuarioDirector, IdModalidad, Nombre, Estado)
            VALUES (3, 'CG24001', 3, 'Pasantía profesional en empresa colaboradora', 'Activo')
            """.trimIndent()
        )

        db.execSQL("INSERT OR IGNORE INTO Docente VALUES ('CG24001', 1, NULL, 'Cesar', 'Augusto', 'González', 'Rodríguez')")

        db.execSQL("INSERT OR IGNORE INTO Alumno VALUES ('LR21008', 1, 'LR21008', 'Eduardo', 'Enrique', 'López', 'Rodríguez', 'Ingeniería de Sistemas Informáticos')")
        db.execSQL("INSERT OR IGNORE INTO Alumno VALUES ('AP20025', 1, 'AP20025', 'Allan', 'Augusto', 'Anduray', 'Portillo', 'Ingeniería de Sistemas Informáticos')")
        db.execSQL("INSERT OR IGNORE INTO Alumno VALUES ('AC21011', 2, 'AC21011', 'Oscar', 'Mauricio', 'Ángel', 'Córdova', 'Ingeniería de Sistemas Informáticos')")
        db.execSQL("INSERT OR IGNORE INTO Alumno VALUES ('EM22001', 2, 'EM22001', 'Luisa', 'Elizabeth', 'Escobar', 'Martínez', 'Ingeniería de Sistemas Informáticos')")
        db.execSQL("INSERT OR IGNORE INTO Alumno VALUES ('HR23040', 3, 'HR23040', 'Eduardo', 'Javier', 'Hernández', 'Regalado', 'Ingeniería de Sistemas Informáticos')")

        db.execSQL(
            """
            INSERT OR IGNORE INTO SolicitudModalidad 
            (IdSolicitudModalidad, IdUsuario, IdModalidad, IdTrabajoGraduacion, FechaSolicitud, EstadoSolicitud, ObservacionSolicitud)
            VALUES (1, 'LR21008', 1, 1, '2026-05-21', 'Aprobada', 'Solicitud aprobada para modalidad de investigación')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO SolicitudModalidad 
            (IdSolicitudModalidad, IdUsuario, IdModalidad, IdTrabajoGraduacion, FechaSolicitud, EstadoSolicitud, ObservacionSolicitud)
            VALUES (2, 'AC21011', 2, 2, '2026-05-21', 'Aprobada', 'Solicitud aprobada para curso de especialización')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO SolicitudModalidad 
            (IdSolicitudModalidad, IdUsuario, IdModalidad, IdTrabajoGraduacion, FechaSolicitud, EstadoSolicitud, ObservacionSolicitud)
            VALUES (3, 'HR23040', 3, 3, '2026-05-21', 'Pendiente', 'Solicitud pendiente de revisión para pasantía')
            """.trimIndent()
        )
    }

    private fun insertarDatosInvestigacion(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO GrupoTGI VALUES (1, 1, '2026-04-01', NULL, 'Activo')")

        db.execSQL(
            """
            INSERT OR IGNORE INTO PropuestaPerfil 
            (IdPropuestaPerfil, IdTrabajoGraduacion, Titulo, Descripcion, Estado, FechaRegistro)
            VALUES (1, 1, 'Sistema web para control de tutorías', 'Propuesta inicial del grupo', 'Descartada', '2026-04-10')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO PropuestaPerfil 
            (IdPropuestaPerfil, IdTrabajoGraduacion, Titulo, Descripcion, Estado, FechaRegistro)
            VALUES (2, 1, 'Sistema de seguimiento académico', 'Propuesta seleccionada por el docente', 'Seleccionada', '2026-04-11')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO PropuestaPerfil 
            (IdPropuestaPerfil, IdTrabajoGraduacion, Titulo, Descripcion, Estado, FechaRegistro)
            VALUES (3, 1, 'App móvil para evaluación de tesis', 'Propuesta con observación', 'Con observación', '2026-04-12')
            """.trimIndent()
        )
    }

    private fun insertarDatosEspecializacion(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO GrupoTGE VALUES (1, 2, '2026-04-01', NULL, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO SubgrupoTGE VALUES (1, 1, 'Sistema de gestión de contenido para estudiantes', 'Activo')")
    }

    private fun insertarDatosPasantia(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO Empresa VALUES (1, 'Crowley Shared Services S.A.', 'Servicios')")
        db.execSQL("INSERT OR IGNORE INTO Personero VALUES (1, 1, 'Eliner Villafuerte', 'Supervisor')")

        db.execSQL(
            """
            INSERT OR IGNORE INTO ProyectoPasantia 
            (IdProyectoPasantia, IdTrabajoGraduacion, IdEmpresa, FechaCreacion, FechaFinal, Estado)
            VALUES (1, 3, 1, '2026-04-01', NULL, 'Activo')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO MemoriaResumen 
            (IdMemoriaResumen, IdProyectoPasantia, PeriodoInicio, PeriodoFinal, ContenidoResumen, Estado)
            VALUES (1, 1, '2026-04-01', '2026-05-01', 'Memoria inicial de labores', 'Pendiente')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO Bitacora 
            (IdBitacora, IdProyectoPasantia, IdMemoriaResumen, FechaActividad, DescripcionActividad, TotalHorasTrabajadas, Estado)
            VALUES (1, 1, 1, '2026-04-10', 'Configuración del entorno de desarrollo', 4, 'Revisada')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO Bitacora 
            (IdBitacora, IdProyectoPasantia, IdMemoriaResumen, FechaActividad, DescripcionActividad, TotalHorasTrabajadas, Estado)
            VALUES (2, 1, 1, '2026-04-12', 'Reunión con supervisor para definir funcionalidades', 3, 'Revisada')
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO Bitacora 
            (IdBitacora, IdProyectoPasantia, IdMemoriaResumen, FechaActividad, DescripcionActividad, TotalHorasTrabajadas, Estado)
            VALUES (3, 1, 1, '2026-04-14', 'Desarrollo de módulo de usuarios', 5, 'Pendiente')
            """.trimIndent()
        )
    }

    private fun insertarEstadosDocumentos(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO EstadoDocumento VALUES (1, 'No subido')")
        db.execSQL("INSERT OR IGNORE INTO EstadoDocumento VALUES (2, 'En revisión')")
        db.execSQL("INSERT OR IGNORE INTO EstadoDocumento VALUES (3, 'Aprobado')")
        db.execSQL("INSERT OR IGNORE INTO EstadoDocumento VALUES (4, 'Con observación')")

        db.execSQL("INSERT OR IGNORE INTO Documento VALUES (1, 1, 2, NULL, NULL)")
        db.execSQL("INSERT OR IGNORE INTO Documento VALUES (2, 2, 1, NULL, NULL)")
        db.execSQL("INSERT OR IGNORE INTO Documento VALUES (3, 3, 1, NULL, NULL)")
    }

    private fun insertarNotas(db: SQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO NotaEtapa VALUES (1, 1, 1, 8.7)")
        db.execSQL("INSERT OR IGNORE INTO NotaEtapa VALUES (2, 1, 2, 8.05)")
        db.execSQL("INSERT OR IGNORE INTO NotaEtapa VALUES (3, 1, 3, NULL)")
        db.execSQL("INSERT OR IGNORE INTO NotaEtapa VALUES (4, 1, 4, NULL)")

        db.execSQL("INSERT OR IGNORE INTO NotaEtapa VALUES (5, 2, 1, 8.5)")
        db.execSQL("INSERT OR IGNORE INTO NotaEtapa VALUES (6, 2, 2, 9.0)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS NotaEtapa")
        db.execSQL("DROP TABLE IF EXISTS Documento")
        db.execSQL("DROP TABLE IF EXISTS EstadoDocumento")

        db.execSQL("DROP TABLE IF EXISTS Bitacora")
        db.execSQL("DROP TABLE IF EXISTS MemoriaResumen")
        db.execSQL("DROP TABLE IF EXISTS ProyectoPasantia")
        db.execSQL("DROP TABLE IF EXISTS Personero")
        db.execSQL("DROP TABLE IF EXISTS Empresa")

        db.execSQL("DROP TABLE IF EXISTS SubgrupoTGE")
        db.execSQL("DROP TABLE IF EXISTS GrupoTGE")

        db.execSQL("DROP TABLE IF EXISTS PropuestaPerfil")
        db.execSQL("DROP TABLE IF EXISTS GrupoTGI")

        db.execSQL("DROP TABLE IF EXISTS SolicitudModalidad")
        db.execSQL("DROP TABLE IF EXISTS Alumno")
        db.execSQL("DROP TABLE IF EXISTS Docente")
        db.execSQL("DROP TABLE IF EXISTS TrabajoGraduacion")
        db.execSQL("DROP TABLE IF EXISTS Modalidad")

        db.execSQL("DROP TABLE IF EXISTS Roles_OpcionesMenu")
        db.execSQL("DROP TABLE IF EXISTS Usuario")
        db.execSQL("DROP TABLE IF EXISTS OpcionesMenu")
        db.execSQL("DROP TABLE IF EXISTS Roles")

        onCreate(db)
    }
}