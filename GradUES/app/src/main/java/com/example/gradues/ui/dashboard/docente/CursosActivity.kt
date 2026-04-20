package com.example.gradues.ui.dashboard.docente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gradues.databinding.ActivityCursosBinding

class CursosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCursosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCursosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}