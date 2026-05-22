package com.example.gradues.data.model

data class DetalleTrabajoAlumnoModel(
    val idUsuario: String,
    val nombreCompleto: String,
    val carnet: String,
    val modalidad: String,
    val idModalidad: Int?,
    val idTrabajoGraduacion: Int?,
    val tituloTrabajo: String,
    val estadoTrabajo: String,
    val docenteDirector: String,
    val nombreGrupo: String,
    val empresa: String?,
    val personero: String?,
    val estadoMemoria: String?,
    val bitacorasRegistradas: Int,
    val integrantes: List<String>,
    val propuestas: List<String>,
    val notas: List<String>
)