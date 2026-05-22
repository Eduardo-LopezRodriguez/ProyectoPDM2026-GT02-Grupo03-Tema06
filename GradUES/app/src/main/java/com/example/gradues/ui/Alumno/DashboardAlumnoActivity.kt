package com.example.gradues.ui.Alumno

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.data.dao.DashboardAlumnoDao
import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.model.DashboardAlumnoModel
import com.example.gradues.databinding.ActivityDashboardAlumnoBinding
import com.example.gradues.ui.login.LoginActivity
import com.example.gradues.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class DashboardAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAlumnoBinding
    private lateinit var session: SessionManager
    private lateinit var dashboardAlumnoDao: DashboardAlumnoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        dashboardAlumnoDao = DashboardAlumnoDao(DatabaseHelper(this))

        cargarDatosDesdeBase()
        configurarClicks()
    }

    private fun cargarDatosDesdeBase() {
        val idUsuario = obtenerIdUsuarioSesion()

        if (idUsuario.isBlank()) {
            mostrarSesionInvalida()
            return
        }

        val dashboard = dashboardAlumnoDao.obtenerDashboardAlumno(idUsuario)

        if (dashboard == null) {
            mostrarDatosNoEncontrados(idUsuario)
            return
        }

        pintarDashboard(dashboard)
    }

    private fun obtenerIdUsuarioSesion(): String {
        val idDesdeSesion = session.getIdUsuario()

        if (idDesdeSesion.isNotBlank()) {
            return idDesdeSesion
        }

        return intent.getStringExtra("ID_USUARIO")
            ?: intent.getStringExtra("IdUsuario")
            ?: intent.getStringExtra("idUsuario")
            ?: ""
    }

    private fun pintarDashboard(model: DashboardAlumnoModel) {
        binding.tvSaludo.text = "¡Hola, ${obtenerPrimerNombre(model.nombreCompleto)}!"
        binding.tvCorreo.text = model.correo

        binding.tvNombreGrupo.text = model.nombreGrupo
        binding.tvTemaGrupo.text = model.temaGrupo

        binding.tvNombreProyectoEstado.text = model.nombreProyectoEstado
        binding.tvEstadoResumen.text = model.estadoResumen

        binding.tvEtapa1.text = "Etapa 1: ${formatearNota(model.notaEtapa1)}"
        binding.tvEtapa2.text = "Etapa 2: ${formatearNota(model.notaEtapa2)}"
        binding.tvEtapa3.text = "Etapa 3: ${formatearNota(model.notaEtapa3)}"
        binding.tvEtapa4.text = "Etapa 4: ${formatearNota(model.notaEtapa4)}"
    }

    private fun mostrarDatosNoEncontrados(idUsuario: String) {
        binding.tvSaludo.text = "¡Hola, Alumno!"
        binding.tvCorreo.text = "${idUsuario.lowercase()}@ues.edu.sv"

        binding.tvNombreGrupo.text = "Sin grupo asignado"
        binding.tvTemaGrupo.text = "No se encontró información académica en la base de datos"

        binding.tvNombreProyectoEstado.text = "Sin trabajo de graduación"
        binding.tvEstadoResumen.text = "Estado: sin información"

        binding.tvEtapa1.text = "Etapa 1: ---"
        binding.tvEtapa2.text = "Etapa 2: ---"
        binding.tvEtapa3.text = "Etapa 3: ---"
        binding.tvEtapa4.text = "Etapa 4: ---"
    }

    private fun configurarClicks() {
        binding.btnMenu.setOnClickListener {
            // Pendiente: abrir menú lateral
        }

        binding.btnNotificaciones.setOnClickListener {
            // Pendiente: abrir notificaciones
        }

        binding.btnPerfil.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        binding.btnVerGrupo.setOnClickListener {
            startActivity(Intent(this, DetalleTrabajoAlumnoActivity::class.java))
        }

        binding.btnVerDetallePropuestas.setOnClickListener {
            // Pendiente: abrir listado de propuestas desde DB
        }

        binding.btnAbrirNotas.setOnClickListener {
            // Pendiente: abrir detalle de notas desde DB
        }

        binding.btnRegistrarPropuestas.setOnClickListener {
            // Pendiente: abrir formulario de propuestas
        }

        binding.btnNavHome.setOnClickListener {
            // Ya está en home
        }

        binding.btnNavGrupos.setOnClickListener {
            startActivity(Intent(this, DetalleTrabajoAlumnoActivity::class.java))
        }

        binding.btnNavPerfil.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí, salir") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        session.cerrarSesion()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun mostrarSesionInvalida() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sesión no válida")
            .setMessage("No se encontró un usuario activo. Inicia sesión nuevamente.")
            .setPositiveButton("Ir al login") { _, _ ->
                cerrarSesion()
            }
            .setCancelable(false)
            .show()
    }

    private fun obtenerPrimerNombre(nombreCompleto: String): String {
        return nombreCompleto
            .trim()
            .split(" ")
            .firstOrNull()
            .orEmpty()
            .ifBlank { "Alumno" }
    }

    private fun formatearNota(nota: Double?): String {
        if (nota == null) {
            return "---"
        }

        return String.format(Locale.US, "%.2f", nota)
    }
}