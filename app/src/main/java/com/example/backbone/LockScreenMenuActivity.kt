package com.example.backbone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.backbone.databinding.ActivityLockScreenMenuBinding

class LockScreenMenuActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLockScreenMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenMenuBinding.inflate(layoutInflater)

        // 앱 암호 설정 스위치 이동시 창 뜨게 하는 버튼 리스너
        binding.switchBtn.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if (onSwitch){
                // 스위치 활성화
                val lockScreenSetIntent = Intent(this@LockScreenMenuActivity, LockScreenSetActivity::class.java)
                startActivity(lockScreenSetIntent)
            }

            else {
                //스위치 비활성화
                val lockScreenIntent = Intent(this@LockScreenMenuActivity, LockedScreenActivity::class.java)
                startActivity(lockScreenIntent)
            }

        }

        setContentView(binding.root)
    }
}