package com.example.gradues.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.gradues.data.dao.OpcionMenuDao
import com.example.gradues.data.dao.UsuarioDao
import com.example.gradues.data.db.DatabaseHelper
import com.example.gradues.data.entities.OpcionMenu
import com.example.gradues.data.repository.AuthRepository
import com.example.gradues.databinding.ActivityLoginBinding
import com.example.gradues.ui.dashboard.DashboardDocenteActivity
import com.example.gradues.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    private val viewModel: LoginViewModel by viewModels {
        val dbHelper = DatabaseHelper(this)
        val uDao = UsuarioDao(dbHelper)
        val oDao = OpcionMenuDao(dbHelper)
        val repo = AuthRepository(uDao, oDao)
        LoginViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDarkMode.bringToFront()
        binding.btnHelp.bringToFront()

        session = SessionManager(this)
        session.cerrarSesion() // <- agrega esta línea temporalmente

        if (session.estaLogueado()) {
            mostrarDialogoYaLogueado()
            return
        }

        binding.btnDarkMode.setOnClickListener { toggleDarkMode() }
        binding.btnHelp.setOnClickListener { mostrarDialogoAyuda() }

        binding.btnLogin.setOnClickListener {
            val id = binding.etIdUsuario.text.toString().trim()
            val pass = binding.etContra.text.toString().trim()

            if (id.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor, llena los campos", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("LOGIN_DEBUG", "Intentando login con: $id")
                viewModel.login(id, pass)
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            Log.d("LOGIN_DEBUG", "Nuevo estado recibido: ${state::class.simpleName}")

            when (state) {
                is LoginState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is LoginState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error de acceso")
                        .setMessage(state.mensaje)
                        .setPositiveButton("Intentar de nuevo", null)
                        .show()
                }
                is LoginState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    val nombre = state.usuario.NombreUsuario
                    val id = state.usuario.IdUsuario
                    val rol = state.usuario.NombreRol
                    val menu = state.menu

                    MaterialAlertDialogBuilder(this)
                        .setTitle("¡Bienvenido!")
                        .setMessage("Inicio de sesión exitoso.\nHola, $nombre")
                        .setCancelable(false)
                        .setPositiveButton("Continuar") { _, _ ->
                            session.guardarSesion(id, nombre, rol)
                            if (rol == "Docente") {
                                val intent = Intent(this, DashboardDocenteActivity::class.java)
                                intent.putExtra("NOMBRE", nombre)
                                intent.putExtra("ROL", rol)
                                startActivity(intent)
                                finish()
                            }
                        }
                        .show()
                }
                is LoginState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun mostrarDialogoYaLogueado() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sesión activa")
            .setMessage("Ya tienes una sesión iniciada.")
            .setCancelable(false)
            .setPositiveButton("Continuar") { _, _ ->
                Log.d("LOGIN_DEBUG", "Usuario ya logueado, redirigiendo...")
            }
            .show()
    }

    private fun toggleDarkMode() {
        val modoActual = AppCompatDelegate.getDefaultNightMode()
        if (modoActual == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        recreate()
    }

    private fun mostrarDialogoAyuda() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Ayuda")
            .setMessage("Usa tu carnet y contraseña institucional.")
            .setPositiveButton("OK", null)
            .show()
    }
}