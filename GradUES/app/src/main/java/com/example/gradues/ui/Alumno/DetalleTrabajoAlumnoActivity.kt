package com.example.gradues.ui.Alumno

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.data.dao.DetalleTrabajoAlumnoDao
import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.model.DetalleTrabajoAlumnoModel
import com.example.gradues.databinding.ActivityDetalleTrabajoAlumnoBinding
import com.example.gradues.ui.login.LoginActivity
import com.example.gradues.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DetalleTrabajoAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleTrabajoAlumnoBinding
    private lateinit var session: SessionManager
    private lateinit var detalleDao: DetalleTrabajoAlumnoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalleTrabajoAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        detalleDao = DetalleTrabajoAlumnoDao(DatabaseHelper(this))

        configurarClicks()
        cargarDetalle()
    }

    private fun configurarClicks() {
        binding.btnVolver.setOnClickListener {
            finish()
        }

        binding.btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    private fun cargarDetalle() {
        val idUsuario = obtenerIdUsuarioSesion()

        if (idUsuario.isBlank()) {
            mostrarSesionInvalida()
            return
        }

        val detalle = detalleDao.obtenerDetalleAlumno(idUsuario)

        if (detalle == null) {
            mostrarSinDatos()
            return
        }

        pintarDetalle(detalle)
    }

    private fun obtenerIdUsuarioSesion(): String {
        val idSesion = session.getIdUsuario()

        if (idSesion.isNotBlank()) {
            return idSesion
        }

        return intent.getStringExtra("ID_USUARIO")
            ?: intent.getStringExtra("idUsuario")
            ?: ""
    }

    private fun pintarDetalle(detalle: DetalleTrabajoAlumnoModel) {
        binding.tvTituloPantalla.text = detalle.nombreGrupo
        binding.tvSubtituloPantalla.text = detalle.modalidad

        binding.tvNombreAlumno.text = detalle.nombreCompleto
        binding.tvCarnet.text = "Carnet: ${detalle.carnet}"
        binding.tvModalidad.text = "Modalidad: ${detalle.modalidad}"

        binding.tvNombreGrupo.text = detalle.nombreGrupo
        binding.tvTituloTrabajo.text = detalle.tituloTrabajo
        binding.tvEstadoTrabajo.text = "Estado: ${detalle.estadoTrabajo}"
        binding.tvDocenteDirector.text = "Docente director: ${detalle.docenteDirector}"

        binding.tvIntegrantes.text = if (detalle.integrantes.isEmpty()) {
            "Sin integrantes registrados"
        } else {
            detalle.integrantes.joinToString(separator = "\n")
        }

        binding.tvPropuestas.text = if (detalle.propuestas.isEmpty()) {
            "Sin propuestas registradas"
        } else {
            detalle.propuestas.joinToString(separator = "\n")
        }

        binding.tvNotas.text = if (detalle.notas.isEmpty()) {
            "Sin notas registradas"
        } else {
            detalle.notas.joinToString(separator = "\n")
        }

        if (detalle.idModalidad == 3) {
            binding.cardPasantia.visibility = View.VISIBLE
            binding.tvEmpresa.text = "Empresa: ${detalle.empresa ?: "Sin empresa"}"
            binding.tvPersonero.text = "Personero: ${detalle.personero ?: "Sin personero"}"
            binding.tvMemoria.text = "Memoria: ${detalle.estadoMemoria ?: "Sin memoria registrada"}"
            binding.tvBitacoras.text = "Bitácoras registradas: ${detalle.bitacorasRegistradas}"
        } else {
            binding.cardPasantia.visibility = View.GONE
        }
    }

    private fun mostrarSinDatos() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sin información")
            .setMessage("No se encontró información académica para este usuario.")
            .setPositiveButton("Volver") { _, _ ->
                finish()
            }
            .show()
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
}