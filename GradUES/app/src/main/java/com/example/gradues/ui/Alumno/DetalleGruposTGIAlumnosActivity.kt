package com.example.gradues.ui.Alumno

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.databinding.ActivityDetalleGruposTgiAlumnoBinding

class DetalleGruposTGIAlumnosActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDetalleGruposTgiAlumnoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalleGruposTgiAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarClicks()
    }

    private fun configurarClicks(){
        // Controla el clic del botón de regresar
        binding.btnBack.setOnClickListener {
            //Intent para ir de esta pantalla de regreso al DashboardAlumno
            val intent = Intent(this, DashboardAlumnoActivity::class.java)

            //Iniciar actividad del DashboardAlumno
            startActivity(intent)
            //Cerrar actividad
            finish()
        }

        // Controla el clic del botón de Subir Tesis
        binding.btnSubirTesis.setOnClickListener {
            //Intent para ir de esta pantalla de regreso al DashboardAlumno
            val intent = Intent(this, TesisTGIAlumnoActivity::class.java)

            //Iniciar actividad del DashboardAlumno
            startActivity(intent)
            //Cerrar actividad
            finish()
        }
    }

}
