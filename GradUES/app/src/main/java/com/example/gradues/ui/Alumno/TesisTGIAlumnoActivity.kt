package com.example.gradues.ui.Alumno

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.databinding.ActivityRegistrarPropuestaTgiAlumnoBinding
import com.example.gradues.databinding.ActivityTesisTgiAlumnoBinding

class TesisTGIAlumnoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityTesisTgiAlumnoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTesisTgiAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarClicks()
    }

    private fun configurarClicks(){
        // Controla el clic del botón de regresar (ImageButton)
        binding.btnBack.setOnClickListener {
            //Intent para ir de esta pantalla de regreso al DashboardAlumno
            val intent = Intent(this, DetalleGruposTGIAlumnosActivity::class.java)

            //Iniciar actividad del DashboardAlumno
            startActivity(intent)
            //Cerrar actividad
            finish()
        }
    }
}