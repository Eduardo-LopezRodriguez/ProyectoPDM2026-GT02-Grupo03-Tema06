package com.example.gradues.data.model

data class DashboardAlumnoModel(
    val idUsuario: String,
    val idTrabajoGraduacion: Int?,
    val nombreCompleto: String,
    val correo: String,
    val modalidad: String,
    val nombreGrupo: String,
    val temaGrupo: String,
    val nombreProyectoEstado: String,
    val estadoResumen: String,
    val notaEtapa1: Double?,
    val notaEtapa2: Double?,
    val notaEtapa3: Double?,
    val notaEtapa4: Double?
)