package com.example.backbone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.backbone.databinding.ActivityEditingBinding

class EditingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditingBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}