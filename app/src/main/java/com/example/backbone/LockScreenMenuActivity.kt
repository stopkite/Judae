package com.example.backbone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.backbone.databinding.ActivityLockScreenMenuBinding

class LockScreenMenuActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLockScreenMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}