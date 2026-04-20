package com.example.gradues.ui.Alumno

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.databinding.ActivityDashboardAlumnoBinding

class DashboardAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAlumnoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarPreview()
        configurarClicksPreview()
    }

    private fun cargarPreview() {
        binding.tvSaludo.text = "¡Hola, Eduardo!"
        binding.tvCorreo.text = "lr21008@ues.edu.sv"

        binding.tvNombreGrupo.text = "Grupo de Investigación 01"
        binding.tvTemaGrupo.text = "Sistema de seguimiento académico"

        binding.tvNombreProyectoEstado.text = "Sistema de seguimiento académico"
        binding.tvEstadoResumen.text = "Estado: 1 aprobada, 2 denegadas"

        binding.tvEtapa1.text = "Etapa 1: 8.7"
        binding.tvEtapa2.text = "Etapa 2: 8.05"
        binding.tvEtapa3.text = "Etapa 3: ---"
        binding.tvEtapa4.text = "Etapa 4: ---"
    }

    private fun configurarClicksPreview() {
        binding.btnMenu.setOnClickListener {
            // TODO Preview: abrir menú
        }

        binding.btnNotificaciones.setOnClickListener {
            // TODO Preview: abrir notificaciones
        }

        binding.btnPerfil.setOnClickListener {
            // TODO Preview: abrir perfil
        }

        binding.btnVerGrupo.setOnClickListener {
            // TODO Preview: ver grupo
        }

        binding.btnVerDetallePropuestas.setOnClickListener {
            // TODO Preview: ver detalle de propuestas
        }

        binding.btnAbrirNotas.setOnClickListener {
            // TODO Preview: abrir notas
        }

        binding.btnRegistrarPropuestas.setOnClickListener {
            // TODO Preview: registrar propuestas
        }

        binding.btnNavHome.setOnClickListener {
            // TODO Preview: ir a inicio
        }

        binding.btnNavGrupos.setOnClickListener {
            // TODO Preview: ir a grupos
        }

        binding.btnNavPerfil.setOnClickListener {
            // TODO Preview: ir a perfil
        }
    }
}