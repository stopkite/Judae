package com.example.backbone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.backbone.databinding.LockScreenSetBinding

class LockScreenSetActivity : AppCompatActivity() {
    lateinit var binding: LockScreenSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LockScreenSetBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}