package com.example.gradues.ui.dashboard.docente

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.databinding.ActivityDashboardDocenteBinding
import com.example.gradues.ui.login.LoginActivity
import com.example.gradues.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.gradues.ui.dashboard.docente.GruposActivity
import com.example.gradues.ui.dashboard.docente.CursosActivity

class DashboardDocenteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardDocenteBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardDocenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val nombre = intent.getStringExtra("NOMBRE") ?: "Docente"
        val rol    = intent.getStringExtra("ROL")    ?: "Docente"

        binding.tvNombreDocente.text = nombre
        binding.tvRolDocente.text    = rol

        // Cerrar sesión desde ícono de perfil
        binding.btnPerfil.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí, salir") { _, _ ->
                    session.cerrarSesion()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()


        }


        binding.cardGrupos.setOnClickListener {
            startActivity(Intent(this, GruposActivity::class.java))
        }

        binding.cardCursos.setOnClickListener {
            startActivity(Intent(this, CursosActivity::class.java))
        }

        binding.btnCentroTickets.setOnClickListener {
            // Navegar a tickets
        }

        binding.btnNavHome.setOnClickListener {
            // Ya estás en home
        }

        binding.btnNavGrupos.setOnClickListener {
            // Navegar a grupos
        }

        binding.btnNavPerfil.setOnClickListener {
            // Navegar a perfil
        }
    }
}