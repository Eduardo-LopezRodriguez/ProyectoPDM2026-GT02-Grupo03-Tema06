package com.example.gradues.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.data.dao.UsuarioDao
import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.databinding.ActivityLoginBinding
import com.example.gradues.ui.Alumno.DashboardAlumnoActivity
import com.example.gradues.ui.dashboard.docente.DashboardDocenteActivity
import com.example.gradues.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)
        usuarioDao = UsuarioDao(dbHelper)
        sessionManager = SessionManager(this)

        if (sessionManager.estaLogueado()) {
            redirigirSegunRol(
                rol = sessionManager.getRol(),
                nombre = sessionManager.getNombre()
            )
            return
        }

        configurarEventos()
    }

    private fun configurarEventos() {
        binding.btnLogin.setOnClickListener {
            iniciarSesion()
        }

        binding.btnHelp.setOnClickListener {
            mostrarError("Usuarios de prueba: LR21008 / 1234 o CG24001 / 1234")
        }

        binding.btnDarkMode.setOnClickListener {
            mostrarError("Cambio de tema pendiente de implementación.")
        }
    }

    private fun iniciarSesion() {
        val idUsuario = binding.etIdUsuario.text?.toString()?.trim().orEmpty()
        val contra = binding.etContra.text?.toString()?.trim().orEmpty()

        limpiarError()

        if (idUsuario.isEmpty()) {
            mostrarError("Ingrese su usuario.")
            return
        }

        if (contra.isEmpty()) {
            mostrarError("Ingrese su contraseña.")
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        val usuario = usuarioDao.login(idUsuario, contra)

        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true

        if (usuario == null) {
            mostrarError("Usuario o contraseña incorrectos.")
            return
        }

        sessionManager.guardarSesion(
            idUsuario = usuario.IdUsuario,
            nombre = usuario.NombreUsuario,
            rol = usuario.NombreRol
        )

        redirigirSegunRol(
            rol = usuario.NombreRol,
            nombre = usuario.NombreUsuario
        )
    }

    private fun redirigirSegunRol(rol: String, nombre: String) {
        val intent = when (rol.lowercase()) {
            "alumno" -> Intent(this, DashboardAlumnoActivity::class.java)
            "docente" -> Intent(this, DashboardDocenteActivity::class.java)
            "administrador" -> Intent(this, DashboardDocenteActivity::class.java)
            "jurado" -> Intent(this, DashboardDocenteActivity::class.java)
            else -> Intent(this, DashboardDocenteActivity::class.java)
        }

        intent.putExtra("NOMBRE", nombre)
        intent.putExtra("ROL", rol)

        startActivity(intent)
        finish()
    }

    private fun mostrarError(mensaje: String) {
        binding.tvError.text = mensaje
        binding.tvError.visibility = View.VISIBLE
    }

    private fun limpiarError() {
        binding.tvError.text = ""
        binding.tvError.visibility = View.GONE
    }
}