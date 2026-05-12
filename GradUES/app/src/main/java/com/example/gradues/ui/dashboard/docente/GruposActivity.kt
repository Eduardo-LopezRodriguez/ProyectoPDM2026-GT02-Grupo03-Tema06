package com.example.gradues.ui.dashboard.docente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.databinding.ActivityGruposBinding

class GruposActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGruposBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGruposBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}