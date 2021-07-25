package com.example.backbone

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.backbone.databinding.ActivityLockScreenMenuBinding


class LockScreenMenuActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLockScreenMenuBinding

    //DBHelper와 이어주도록 클래스 선언
    var db:DBHelper= DBHelper(this)

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockScreenMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try{
            //User 테이블이 있는지 SQL문으로 돌려보기
            db.PWisExist()
            binding.switchBtn.isChecked = true
            binding.changePWTxt.isEnabled = false
        }catch (e: Exception){
            //존재 하지 않으면 익셉션 발생하므로 그냥 flag 변수 false로 설정해주기.
            //만약 사용자 User 테이블이 존재하지 않는다면 바로 홈 화면으로 넘어가기
            binding.switchBtn.isChecked = false
            binding.changePWTxt.isEnabled = true
            binding.changePWTxt.setTextColor(Color.parseColor("#808080"))
        }

        //뒤로가기 버튼 클릭 리스너
        binding.backBtn.setOnClickListener {
            // 홈 화면으로 이동
            finish()
        }

        // 앱 암호 설정 스위치 이동시 창 뜨게 하는 버튼 리스너
        binding.switchBtn.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if (onSwitch){
                // 비밀번호 생성
                // 스위치 활성화시 암호 설정 화면으로 이동
                val lockScreenSetIntent = Intent(this@LockScreenMenuActivity, LockedScreenSetActivity::class.java)
                startActivity(lockScreenSetIntent)
            }

            else {
                // 비밀번호 삭제
                //스위치 비활성화시 암호 확인 화면으로 이동
                val lockScreenIntent = Intent(this@LockScreenMenuActivity, LockedScreenOnceActivity::class.java)
                lockScreenIntent.putExtra("state", "Delete")
                startActivity(lockScreenIntent)
            }

        }

        // 암호 변경 창 뜨게 하는 버튼 리스너
        binding.changePWTxt.setOnClickListener {
            // 앱 암호 설정 화면으로 이동
            val lockScreenSetIntent = Intent(this@LockScreenMenuActivity, LockedScreenOnceActivity::class.java)
            lockScreenSetIntent.putExtra("state", "Update")
            startActivity(lockScreenSetIntent)
        }

        setContentView(binding.root)
    }



}