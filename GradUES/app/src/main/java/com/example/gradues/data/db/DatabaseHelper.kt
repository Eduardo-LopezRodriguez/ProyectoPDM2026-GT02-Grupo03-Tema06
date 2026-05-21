package com.example.gradues.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "gradues.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Habilitacion de soporte para Llaves Foráneas en SQLite
        db.execSQL("PRAGMA foreign_keys = ON;")

        //Para una mejor comprension y menor confusion se dividieron las tablas de la BD en bloques (lo mismo pasa con las inserciones iniciales de mas abajo)
        //Si se quiere cambiar algo verificar las tablassegun los bloques :)

        // =============================================================================================
        // 1. Tablas independientes de la BD (nombre de la BD: proyecto_pdm)
        // =============================================================================================

        db.execSQL("CREATE TABLE empresa (idEmpresa INTEGER PRIMARY KEY, nombreEmpresa TEXT, rubroEmpresa TEXT)")

        db.execSQL("CREATE TABLE grupo_tge (idGrupoTGE INTEGER PRIMARY KEY, nombreCurso TEXT, cicloAcademico TEXT, cupoMaximo INTEGER, fechaCreacion TEXT, fechaFinal TEXT)")

        db.execSQL("CREATE TABLE modalidad (idModalidad INTEGER PRIMARY KEY, tipoModalidad TEXT)")

        db.execSQL("CREATE TABLE modulo_menu (idModuloMenu INTEGER PRIMARY KEY, nombreModuloMenu TEXT, descripcionModuloMenu TEXT, estadoModuloMenu TEXT)")

        db.execSQL("CREATE TABLE rol (idRol INTEGER PRIMARY KEY, nombreRol TEXT, descripcionRol TEXT)")

        // ============================================================================================
        // 2. Tablas con dependencia directas de la BD
        // ============================================================================================

        db.execSQL("""
            CREATE TABLE personero (
                idPersonero INTEGER PRIMARY KEY,
                idEmpresa INTEGER,
                nombrePersonero TEXT,
                cargoPersonero TEXT,
                FOREIGN KEY(idEmpresa) REFERENCES empresa(idEmpresa)
            )
        """)

        db.execSQL("""
            CREATE TABLE opcion_menu (
                idOpcionMenu INTEGER PRIMARY KEY,
                idModuloMenu INTEGER,
                codigoOpcionMenu TEXT,
                nombreOpcionMenu TEXT,
                rutaPantalla TEXT,
                tipoOpcion TEXT,
                estadoOpcionMenu TEXT,
                FOREIGN KEY(idModuloMenu) REFERENCES modulo_menu(idModuloMenu)
            )
        """)

        db.execSQL("""
            CREATE TABLE usuario (
                idUsuario INTEGER PRIMARY KEY,
                idRol INTEGER,
                nombreUsuario TEXT,
                contraseña TEXT,
                primerNombreUsuario TEXT,
                segunNombreUsuario TEXT,
                primerApellidoUsuario TEXT,
                segundoApellidoUsuario TEXT,
                correoUsuario TEXT,
                carnetUsuario TEXT,
                duiUsuario TEXT,
                carreraUsuario TEXT,
                estadoUsuario TEXT,
                FOREIGN KEY(idRol) REFERENCES rol(idRol)
            )
        """)

        // ==============================================================================================
        // 3. Tablas complejas de la BD
        // ===============================================================================================

        db.execSQL("""
            CREATE TABLE rol_opcion_menu (
                idRolOpcionMenu INTEGER PRIMARY KEY,
                idRol INTEGER,
                idOpcionMenu INTEGER,
                puedeVer INTEGER,
                puedeEditar INTEGER,
                puedeCrear INTEGER,
                puedeEliminar INTEGER,
                FOREIGN KEY(idRol) REFERENCES rol(idRol),
                FOREIGN KEY(idOpcionMenu) REFERENCES opcion_menu(idOpcionMenu)
            )
        """)

        db.execSQL("""
            CREATE TABLE trabajo_graduacion (
                idTrabajoGraduacion INTEGER PRIMARY KEY,
                idModalidad INTEGER,
                idDocenteResponsable INTEGER,
                nombreTrabajo TEXT,
                cicloAcademico TEXT,
                estadoTrabajo TEXT,
                fechaInicioTrabajo TEXT,
                fechaFinalTrabajo TEXT,
                FOREIGN KEY(idModalidad) REFERENCES modalidad(idModalidad),
                FOREIGN KEY(idDocenteResponsable) REFERENCES usuario(idUsuario)
            )
        """)

        // ===================================================================================================
        // 4. Tablas vinculadas a trabajos de graduacion de la BD
        // ===================================================================================================

        db.execSQL("""
            CREATE TABLE alumno_trabajo (
                idAlumnoTrabajo INTEGER PRIMARY KEY,
                idUsuario INTEGER,
                idTrabajoGraduacion INTEGER,
                estadoAlumnoTrabajo TEXT,
                FOREIGN KEY(idUsuario) REFERENCES usuario(idUsuario),
                FOREIGN KEY(idTrabajoGraduacion) REFERENCES trabajo_graduacion(idTrabajoGraduacion)
            )
        """)

        db.execSQL("""
            CREATE TABLE asignacion_jurado (
                idAsignacionJurado INTEGER PRIMARY KEY,
                idUsuario INTEGER,
                idTrabajoGraduacion INTEGER,
                fechaAsignacion TEXT,
                FOREIGN KEY(idUsuario) REFERENCES usuario(idUsuario),
                FOREIGN KEY(idTrabajoGraduacion) REFERENCES trabajo_graduacion(idTrabajoGraduacion)
            )
        """)

        db.execSQL("""
            CREATE TABLE documento (
                idDocumento INTEGER PRIMARY KEY,
                idTrabajoGraduacion INTEGER,
                tipoDocumento TEXT,
                tituloDocumento TEXT,
                urlDocumento TEXT,
                estadoDocumento TEXT,
                observacionDocumento TEXT,
                versionDocumento INTEGER,
                fechaSubida TEXT,
                FOREIGN KEY(idTrabajoGraduacion) REFERENCES trabajo_graduacion(idTrabajoGraduacion)
            )
        """)

        db.execSQL("""
            CREATE TABLE grupo_tgi (
                idGrupoTGI INTEGER PRIMARY KEY,
                idTrabajoGraduacion INTEGER,
                fechaCreacion TEXT,
                fechaFinal TEXT,
                FOREIGN KEY(idTrabajoGraduacion) REFERENCES trabajo_graduacion(idTrabajoGraduacion)
            )
        """)

        db.execSQL("""
            CREATE TABLE propuesta_perfil (
                idPropuesta INTEGER PRIMARY KEY,
                idTrabajoGraduacion INTEGER,
                tituloPropuesta TEXT,
                descripcionPropuesta TEXT,
                estadoPropuesta TEXT,
                observacionPropuesta TEXT,
                urlArchivo TEXT,
                fechaRegistro TEXT,
                FOREIGN KEY(idTrabajoGraduacion) REFERENCES trabajo_graduacion(idTrabajoGraduacion)
            )
        """)

        db.execSQL("""
            CREATE TABLE subgrupo_tge (
                idSubgrupoTGE INTEGER PRIMARY KEY,
                idGrupoTGE INTEGER,
                idTrabajoGraduacion INTEGER,
                nombreSubgrupo TEXT,
                temaAsignado TEXT,
                FOREIGN KEY(idGrupoTGE) REFERENCES grupo_tge(idGrupoTGE),
                FOREIGN KEY(idTrabajoGraduacion) REFERENCES trabajo_graduacion(idTrabajoGraduacion)
            )
        """)

        db.execSQL("""
            CREATE TABLE proyecto_pasantia (
                idProyectoPasantia INTEGER PRIMARY KEY,
                idTrabajoGraduacion INTEGER,
                idEmpresa INTEGER,
                fechaInicioPasantia TEXT,
                fechaFinalPasantia TEXT,
                estadoPasantia TEXT,
                FOREIGN KEY(idTrabajoGraduacion) REFERENCES trabajo_graduacion(idTrabajoGraduacion),
                FOREIGN KEY(idEmpresa) REFERENCES empresa(idEmpresa)
            )
        """)

        // ==========================================
        // 5. Tablas de pasantias y seguimiento de la BD
        // ==========================================

        db.execSQL("""
            CREATE TABLE bitacora (
                idBitacora INTEGER PRIMARY KEY,
                idProyectoPasantia INTEGER,
                fechaActividad TEXT,
                tituloActividad TEXT,
                descripcionActividad TEXT,
                totalHorasTrabajadas INTEGER,
                estadoBitacora TEXT,
                observacionBitacora TEXT,
                FOREIGN KEY(idProyectoPasantia) REFERENCES proyecto_pasantia(idProyectoPasantia)
            )
        """)

        db.execSQL("""
            CREATE TABLE memoria_resumen (
                idMemoriaResumen INTEGER PRIMARY KEY,
                idProyectoPasantia INTEGER,
                contenidoResumen TEXT,
                urlDocumento TEXT,
                estadoMemoria TEXT,
                observacionMemoria TEXT,
                FOREIGN KEY(idProyectoPasantia) REFERENCES proyecto_pasantia(idProyectoPasantia)
            )
        """)

        db.execSQL("""
            CREATE TABLE nota_etapa (
                idNotaEtapa INTEGER PRIMARY KEY,
                idAlumnoTrabajo INTEGER,
                numeroEtapa INTEGER,
                nota REAL,
                observacionNota TEXT,
                fechaRegistro TEXT,
                FOREIGN KEY(idAlumnoTrabajo) REFERENCES alumno_trabajo(idAlumnoTrabajo)
            )
        """)

        seedDatabase(db)
    }

    private fun seedDatabase(db: SQLiteDatabase) {

        // ===============================================================================================================
        // 1. Inserciones de las tablas independientes
        // ===============================================================================================================

        // Empresas
        db.execSQL("INSERT OR IGNORE INTO empresa VALUES (1, 'Super Selectos', 'Cadena de supermercados')")
        db.execSQL("INSERT OR IGNORE INTO empresa VALUES (2, 'Tigo El Salvador', 'Telecomunicaciones y telefonía')")
        db.execSQL("INSERT OR IGNORE INTO empresa VALUES (3, 'Banco Agricola', 'Servicios financieros y banca')")
        db.execSQL("INSERT OR IGNORE INTO empresa VALUES (4, 'Almacenes Siman', 'Tienda por departamentos')")
        db.execSQL("INSERT OR IGNORE INTO empresa VALUES (5, 'Pollo Campero', 'Restaurantes y comida rápida')")

        // Grupos TGE
        db.execSQL("INSERT OR IGNORE INTO grupo_tge VALUES (50, 'Desarrollo de Software', 'Ciclo I', 2, '2026-03-12', '2026-08-15')")
        db.execSQL("INSERT OR IGNORE INTO grupo_tge VALUES (51, 'Gestión de Proyectos Tecnológicos', 'Ciclo I', 2, '2026-02-22', '2026-07-04')")

        // Modalidades
        db.execSQL("INSERT OR IGNORE INTO modalidad VALUES (1, 'Presencial')")
        db.execSQL("INSERT OR IGNORE INTO modalidad VALUES (2, 'Virtual')")

        // Módulos Menú
        db.execSQL("INSERT OR IGNORE INTO modulo_menu VALUES (1, 'Docente', 'Modulo del menú para los Docentes', 'En uso')")
        db.execSQL("INSERT OR IGNORE INTO modulo_menu VALUES (2, 'Alumno', 'Modul del menú para los Alumnos', 'En uso')")

        // Roles
        db.execSQL("INSERT OR IGNORE INTO rol VALUES (1, 'Alumno', 'Alumno de la Universidad')")
        db.execSQL("INSERT OR IGNORE INTO rol VALUES (2, 'Docente', 'Docente de la Universidad')")
        // Roles
        db.execSQL("INSERT OR IGNORE INTO Roles VALUES ('Docente', 'Docente asesor / administrador')")
        db.execSQL("INSERT OR IGNORE INTO Roles VALUES ('Alumno', 'Estudiante de la UES')")

        // ===============================================================================================================
        // 2. Inserciones de las que dependen directamente de las independientesa
        // ===============================================================================================================

        // Personeros
        db.execSQL("INSERT OR IGNORE INTO personero VALUES (1, 2, 'Manuel Navarro', 'Técnico en Redes')")
        db.execSQL("INSERT OR IGNORE INTO personero VALUES (2, 1, 'Miguel Quintero', 'Gerente')")
        db.execSQL("INSERT OR IGNORE INTO personero VALUES (3, 4, 'José Palillo', 'Gerente')")
        db.execSQL("INSERT OR IGNORE INTO personero VALUES (4, 5, 'Fernando Rodriguez', 'Técnico en Sistemas informáticos')")
        db.execSQL("INSERT OR IGNORE INTO personero VALUES (5, 3, 'Juana Paredes', 'Técnico en Sistemas informáticos')")

        // Usuarios (Alumnos y Docentes de la UES)
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (1, 1, 'Luis Fernandez', 'LF89004', 'Luis', 'Antonio', 'Fernandez', 'García', 'LF89004@ues.edu.sv', 'LF89004', '088127582', 'Arquitectura', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (2, 1, 'Oscar Noyola', 'ON78903', 'Oscar', 'José', 'Noyola', 'Martínez', 'ON78903@ues.edu.sv', 'ON78903', '056108208', 'Ingeniería Civil', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (3, 1, 'Karla Menjivar', 'KM89001', 'Karla', 'Alexandra', 'Menjivar', 'López', 'KM89001@ues.edu.sv', 'KM89001', '072907741', 'Arquitectura', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (4, 1, 'José Melgar', 'JM78905', 'José', 'David', 'Melgar', 'Ramírez', 'JM78905@ues.edu.sv', 'JM78905', '080273246', 'Ingeniería Mecánica', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (5, 1, 'Luisa Hernández', 'LH34509', 'Luisa', 'Gabriela', 'Hernández', 'Castro', 'LH34509ues.edu.sv', 'LH34509', '069022028', 'Ingeniería Mecánica', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (6, 1, 'Gabriel Muñoz', 'GM34510', 'Gabriel', 'Enrique', 'Muñoz', 'Flores', 'GM34510ues.edu.sv', 'GM34510', '088959362', 'Ingeniería Química', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (7, 1, 'Icaro Juarez', 'IJ34520', 'Icaro', 'Ricardo', 'Juarez', 'Vásquez', 'IJ34520ues.edu.sv', 'IJ34520', '069029648', 'Ingeniería Eléctrica', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (8, 1, 'Fernando Arevalo', 'FA89011', 'Fernando', 'Eduardo', 'Arevalo', 'Morales', 'FA89011ues.edu.sv', 'FA89011', '010929082', 'Ingeniería Industrial', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (9, 1, 'Patricio Estrella', 'PE78907', 'Patricio', 'Manuel', 'Estrella', 'Rivera', 'PE78907@ues.edu.sv', 'PE78907', '086385757', 'Ingeniería Mecánica', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (10, 1, 'Daniella Cevallos', 'DC78971', 'Daniella', 'Cristina', 'Cevallos', 'Cruz', 'DC78971@ues.edu.sv', 'DC78971', '042967679', 'Ingeniería Civil', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (11, 2, 'Ursula Carabantes', 'UC89015', 'Ursula', 'Paola', 'Carabantes', 'Henández', 'UC89015@ues.edu.sv', 'UC89015', '038914115', 'Ingeniería Agronómica', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (12, 2, 'Pedro Lumbar', 'PL78923', 'Pedro', 'Ángel', 'Lumbar', 'Reyes', 'PL78923@ues.edu.sv', 'PL78923', '052659107', 'Arquitectura', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (13, 2, 'Humberto Portillo', 'HP12309', 'Humberto', 'Mario', 'Portillo', 'Rojas', 'HP12309@ues.edu.sv', 'HP12309', '050296026', 'Ingeniería en Sistemas Informáticos', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (14, 2, 'Jorge Arevalo', 'JA12310', 'Jorge', 'Cristian', 'Arevalo', 'Navarro', 'JA12310@ues.edu.sv', 'JA12310', '081692568', 'Ingeniería Agronómica', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (15, 2, 'Tatiana Paredes', 'TP78901', 'Tatiana', 'Rosa', 'Paredes', 'Portillo', 'TP78901@ues.edu.sv', 'TP78901', '099494582', 'Ingeniería Civil', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (16, 2, 'Javier Pozuelo', 'JP89034', 'Javier', 'Carlos', 'Pozuelo', 'Pineda', 'JP89034@ues.edu.sv', 'JP89034', '055163975', 'Arquitectura', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (17, 2, 'Humberto Martinez', 'HM78944', 'Humberto', 'Miguel', 'Martinez', 'Castillo', 'HM78944@ues.edu.sv', 'HM78944', '052664319', 'Ingeniería en Sistemas Informáticos', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (18, 2, 'Luis Rodriguez', 'LR78955', 'Luis', 'Fernando', 'Rodriguez', 'Escobar', 'LR78955@ues.edu.sv', 'LR78955', '004146312', 'Arquitectura', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (19, 2, 'María Hernández', 'MH78990', 'María', 'Fransisca', 'Hernández', 'Guerrero', 'MH78990@ues.edu.sv', 'MH78990', '004801101', 'Ingeniería Química', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (20, 2, 'Juan Goméz', 'JG78975', 'Juan', 'Andrés', 'Goméz', 'Mejía', 'JG78975@ues.edu.sv', 'JG78975', '053904136', 'Ingeniería Eléctrica', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (21, 2, 'Noel Jimenéz', 'NJ78234', 'Noel', 'Nicolas', 'Jimenéz', 'Mejia', 'NJ78234@ues.edu.sv', 'NJ78234', '053904674', 'Ingeniería en Sistemas Informáticos', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (22, 1, 'Miguel Hernández', 'MH89202', 'Miguel', 'Oscar', 'Hernández', 'Bolaños', 'MH89202@ues.edu.sv', 'MH89202', '080196026', 'Ingeniería en Sistemas Informáticos', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (23, 1, 'Rodrigo Melano', 'RM89203', 'Rodrigo', 'Antonio', 'Melano', 'Quiinteros', 'RM89203@ues.edu.sv', 'RM89203', '004146356', 'Ingeniería en Sistemas Informáticos', 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO usuario VALUES (24, 1, 'Alfonso Torres', 'AT87493', 'Alfonso', 'David', 'Torres', 'Lazos', 'AT87493@ues.edu.sv', 'AT87493', '094534582', 'Ingeniería en Sistemas Informáticos', 'Activo')")

        // ==============================================================================================================
        // 3. Inserciones de trabajos de graduacion
        // ==============================================================================================================

        // Trabajos de Graduación
        db.execSQL("INSERT OR IGNORE INTO trabajo_graduacion VALUES (1, 1, 15, 'Diseño e implementación de un sistema web integral para el control de inventarios, gestión de activos fijos y flujos de auditoría interna bajo arquitectura MVC', 'Ciclo I', 'En curso', '2026-03-20', '2026-10-15')")
        db.execSQL("INSERT OR IGNORE INTO trabajo_graduacion VALUES (2, 1, 18, 'Desarrollo de un entorno virtual educativo para la simulación de laboratorios técnicos o recorridos interactivos de la Ciudad Universitaria (UES)', 'Ciclo I', 'En curso', '2026-03-11', '2026-10-10')")
        db.execSQL("INSERT OR IGNORE INTO trabajo_graduacion VALUES (3, 2, 12, 'Diseño de una base de datos relacional robusta con lógica de negocio integrada mediante Triggers y Procedimientos Almacenados para la analítica de ventas en microempresas.', 'Ciclo I', 'En curso', '2026-05-14', '2027-02-15')")
        db.execSQL("INSERT OR IGNORE INTO trabajo_graduacion VALUES (4, 2, 13, 'Plataforma Web para la Gestión de Clínicas Odontológicas y Control de Expedientes Clínicos de Pacientes', 'Ciclo I', 'En curso', '2026-02-23', '2026-08-15')")
        db.execSQL("INSERT OR IGNORE INTO trabajo_graduacion VALUES (5, 2, 21, 'Simulador Virtual en 3D para la Inducción y Capacitación en Prevención de Riesgos Laborales en el Sector Industrial', 'Ciclo I', 'En curso', '2026-02-01', '2026-07-04')")
        db.execSQL("INSERT OR IGNORE INTO trabajo_graduacion VALUES (6, 1, 11, 'Diseño e implementación de un sistema web para el control de inventarios y activos fijos utilizando arquitectura MVC en el departamento de TI de Empresas Pollo Campero', 'Ciclo I', 'En curso', '2026-04-03', '2026-07-10')")
        db.execSQL("INSERT OR IGNORE INTO trabajo_graduacion VALUES (7, 1, 14, 'Desarrollo de una plataforma digital para la gestión de expedientes de empleados y control de asistencia en el área de desarrollo de software de Grupo Simán', 'Ciclo I', 'En cuso', '2026-04-19', '2026-07-24')")

        // ==============================================================================================================
        // 4. Inserciones vinculadas al trabajo de graduacion
        // ==============================================================================================================

        // Alumno Trabajo
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (1, 1, 1, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (2, 2, 1, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (3, 3, 1, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (4, 4, 2, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (5, 5, 2, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (6, 6, 2, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (7, 7, 3, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (8, 8, 3, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (9, 9, 3, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (10, 10, 3, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (11, 22, 4, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (12, 23, 4, 'Activo')")
        db.execSQL("INSERT OR IGNORE INTO alumno_trabajo VALUES (13, 24, 5, 'Activo')")

        // Asignación Jurado
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (100, 11, 1, '2026-05-23')")
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (101, 12, 1, '2026-05-23')")
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (102, 13, 1, '2026-05-23')")
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (103, 14, 2, '2026-06-04')")
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (104, 15, 2, '2026-06-04')")
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (105, 16, 2, '2026-06-04')")
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (106, 17, 3, '2026-08-14')")
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (107, 18, 3, '2026-08-14')")
        db.execSQL("INSERT OR IGNORE INTO asignacion_jurado VALUES (108, 19, 3, '2026-08-14')")

        // Documentos
        db.execSQL("INSERT OR IGNORE INTO documento VALUES (1, 1, 'Capitulo I', ' Capitulo I - Diseño e implementación de un sistema web integral para el control de inventarios, gestión de activos fijos y flujos de auditoría interna bajo arquitectura MVC', 'example.com', 'Observado', 'Sin observaciones.', 1, '2026-07-12')")
        db.execSQL("INSERT OR IGNORE INTO documento VALUES (2, 2, 'Capitulo I', 'Capitulo I - Desarrollo de un entorno virtual educativo para la simulación de laboratorios técnicos o recorridos interactivos de la Ciudad Universitaria (UES)', 'example2.com', 'Observado', 'Contenido corto.', 1, '2026-07-08')")

        // Grupos TGI
        db.execSQL("INSERT OR IGNORE INTO grupo_tgi VALUES (200, 1, '2026-02-17', '2026-10-20')")
        db.execSQL("INSERT OR IGNORE INTO grupo_tgi VALUES (201, 2, '2026-02-10', '2026-10-15')")
        db.execSQL("INSERT OR IGNORE INTO grupo_tgi VALUES (202, 3, '2026-04-12', '2027-02-20')")

        // Propuesta Perfil
        db.execSQL("""
        INSERT OR IGNORE INTO propuesta_perfil VALUES (
            1, 4, 
            'Diseño e implementación de un sistema web basado en arquitectura MVC para la automatización de expedientes clínicos y control de accesos por roles en clínicas odontológicas', 
            'Aplicación interactiva en 3D desarrollada con Unity 6 y URP que simula entornos industriales reales para capacitar al personal en seguridad ocupacional. La plataforma mitiga los riesgos físicos del entrenamiento tradicional en planta mediante mecánicas de interacción en C#, optimización de archivos pesados con Git LFS y el registro del rendimiento del usuario en una base de datos local SQLite.', 
            'Aprobada', 'Sin observaciones', 'propuestaexample.com', '2026-03-25'
        )
    """)
        db.execSQL("""
        INSERT OR IGNORE INTO propuesta_perfil VALUES (
            2, 5, 
            'Desarrollo de un entorno virtual interactivo mediante Unity 6 y el pipeline de renderizado URP para la capacitación en prevención de riesgos en el sector industrial', 
            'Sistema de información web desarrollado bajo la arquitectura MVC para automatizar el control de expedientes clínicos y la gestión administrativa en clínicas odontológicas. La plataforma digitaliza el historial médico de los pacientes y optimiza la seguridad mediante un control estricto de accesos basado en roles, integrando una base de datos relacional (MySQL) protegida con triggers y registros automáticos de auditoría.', 
            'Aprobada', 'Sin observaciones', 'propuesta2example.com', '2026-03-29'
        )
    """)

        // Subgrupos TGE
        db.execSQL("INSERT OR IGNORE INTO subgrupo_tge VALUES (1, 50, 4, 'SubGrupo - 01', 'Plataforma Web para la Gestión de Clínicas Odontológicas y Control de Expedientes Clínicos de Pacientes')")
        db.execSQL("INSERT OR IGNORE INTO subgrupo_tge VALUES (2, 51, 5, 'SubGrupo - 02', 'Simulador Virtual en 3D para la Inducción y Capacitación en Prevención de Riesgos Laborales en el Sector Industrial')")

        // Proyectos Pasantía
        db.execSQL("INSERT OR IGNORE INTO proyecto_pasantia VALUES (1, 6, 5, '2026-04-22', '2026-07-15', 'En curso')")
        db.execSQL("INSERT OR IGNORE INTO proyecto_pasantia VALUES (2, 7, 4, '2026-05-01', '2026-08-03', 'En cuso')")

        // ==========================================
        // 5. Inserciones de las ultimas tablas dependientes
        // ==========================================

        // Bitácoras (Actividades de pasantías en Pollo Campero / Simán)
        db.execSQL("""
        INSERT OR IGNORE INTO bitacora VALUES (
            100, 1, '2026-04-22', 'Soporte técnico a dispositvios', 
            'Consiste en brindar mantenimiento preventivo y correctivo a equipos de cómputo y redes, diagnosticando fallas de hardware, instalando sistemas operativos y resolviendo incidencias técnicas para asegurar la continuidad operativa y el correcto funcionamiento de los dispositivos de la empresa.', 
            6, 'Aprobada', 'Sin observaciones'
        )
    """)
        db.execSQL("""
        INSERT OR IGNORE INTO bitacora VALUES (
            101, 2, '2026-05-01', 'Mantenimiento de computadoras', 
            'Consiste en realizar limpiezas internas y externas del hardware, optimizar el sistema operativo, depurar archivos innecesarios y actualizar software para prevenir fallas físicas, mejorar el rendimiento del equipo y prolongar la vida útil de las computadoras de la empresa.', 
            6, 'Aprobada', 'Sin observaciones'
        )
    """)
        db.execSQL("""
        INSERT OR IGNORE INTO bitacora VALUES (
            102, 2, '2026-05-02', 'Orientación profesional', 
            'Consiste en brindar asesoría y acompañamiento a estudiantes o profesionales para identificar sus habilidades, intereses y competencias técnicas. A través de este proceso se les guía en la elección de su ruta académica, la definición de su perfil laboral and la preparación para el mercado laboral, facilitando su inserción o crecimiento en el ámbito profesional.', 
            12, 'Aprobada', 'Sin observaciones'
        )
    """)
        db.execSQL("""
        INSERT OR IGNORE INTO bitacora VALUES (
            103, 1, '2026-05-08', 'Orientación profesional', 
            'Consiste en brindar asesoría y acompañamiento a estudiantes o profesionales para identificar sus habilidades, intereses y competencias técnicas. A través de este proceso se les guía en la elección de su ruta académica, la definición de su perfil laboral and la preparación para el mercado laboral, facilitando su inserción o crecimiento en el ámbito profesional.', 
            12, 'Aprobada', 'Sin observaciones'
        )
    """)

        // Memorias Resumen
        db.execSQL("""
        INSERT OR IGNORE INTO memoria_resumen VALUES (
            1, 1, 
            'La presente memoria detalla el desarrollo de la pasantía profesional enfocada en el diseño e implementación de un sistema web para el control de inventarios y activos fijos, desarrollado dentro del departamento de TI de Empresas Pollo Campero. El proyecto surgió ante la necesidad de optimizar y centralizar el registro físico y contable de los recursos de la organización. Para dar solución a esta problemática, se desarrolló una plataforma robusta utilizando el patrón arquitectónico MVC, garantizando una clara separación entre la interfaz de usuario, la lógica de negocio y el acceso a los datos. El sistema integra un modelo relacional de base de datos protegido con controles de seguridad basados en roles, permitiendo una gestión precisa de existencias, auditorías automatizadas y la generación de reportes en tiempo real. Con la implementación de este software, se logró mitigar los errores humanos del control manual anterior, agilizar los tiempos de consulta y dotar a la empresa de una herramienta escalable y eficiente para la toma de decisiones logísticas.', 
            'memoria1example.com', 'Aprobada', 'Sin observaciones'
        )
    """)
        db.execSQL("""
        INSERT OR IGNORE INTO memoria_resumen VALUES (
            2, 2, 
            'Esta memoria detalla el desarrollo de una plataforma digital para la gestión de expedientes de empleados y el control de asistencia dentro del área de desarrollo de software de Grupo Simán. El proyecto resuelve la necesidad de centralizar y automatizar los registros del personal mediante un sistema web estructurado bajo la arquitectura MVC. La solución implementa un control de acceso basado en roles para resguardar la confidencialidad de la información y herramientas para el seguimiento de asistencias en tiempo real con persistencia de datos segura. Como resultado, se optimizaron los tiempos administrativos del departamento, se eliminaron los flujos de trabajo basados en papel y se garantizó un registro íntegro, auditable y eficiente del capital humano de la organización.', 
            'memoria2example.com', 'En observacion', 'Mejorar la estructuracion'
        )
    """)

        // Notas Etapas
        //db.execSQL("INSERT OR IGNORE INTO nota_etapa VALUES (1, 1, 1, 9.0, 'Mejorar la redacción', '2026-05-08')")
       // db.execSQL("INSERT OR IGNORE INTO nota_etapa VALUES (2, 2, 1, 9.0, 'Mejorar la redacción', '2026-05-08')")
        //db.execSQL("INSERT OR IGNORE INTO nota_etapa VALUES (3, 3, 1, 9.0, 'Mejorar la redacción', '2026-05-08')")


        // Usuarios
        //db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('CG24001', 'Carlos García', '1234', 'Docente')")
        //db.execSQL("INSERT OR IGNORE INTO Usuario VALUES ('RR24001', 'Rosa Ramos', '4567', 'Alumno')")

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
        //Se eliminan las tablas en caso de actualizacion
        //Se eliminan respetando el orden de las decendencias inversamente
        db.execSQL("DROP TABLE IF EXISTS nota_etapa")
        db.execSQL("DROP TABLE IF EXISTS memoria_resumen")
        db.execSQL("DROP TABLE IF EXISTS bitacora")
        db.execSQL("DROP TABLE IF EXISTS proyecto_pasantia")
        db.execSQL("DROP TABLE IF EXISTS subgrupo_tge")
        db.execSQL("DROP TABLE IF EXISTS propuesta_perfil")
        db.execSQL("DROP TABLE IF EXISTS grupo_tgi")
        db.execSQL("DROP TABLE IF EXISTS documento")
        db.execSQL("DROP TABLE IF EXISTS asignacion_jurado")
        db.execSQL("DROP TABLE IF EXISTS alumno_trabajo")
        db.execSQL("DROP TABLE IF EXISTS trabajo_graduacion")
        db.execSQL("DROP TABLE IF EXISTS rol_opcion_menu")
        db.execSQL("DROP TABLE IF EXISTS usuario")
        db.execSQL("DROP TABLE IF EXISTS opcion_menu")
        db.execSQL("DROP TABLE IF EXISTS personero")
        db.execSQL("DROP TABLE IF EXISTS rol")
        db.execSQL("DROP TABLE IF EXISTS modulo_menu")
        db.execSQL("DROP TABLE IF EXISTS modalidad")
        db.execSQL("DROP TABLE IF EXISTS grupo_tge")
        db.execSQL("DROP TABLE IF EXISTS empresa")
        onCreate(db)
    }
}