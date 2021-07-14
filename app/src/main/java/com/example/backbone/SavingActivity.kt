package com.example.backbone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.backbone.databinding.ActivitySavingBinding



class SavingActivity : AppCompatActivity() {
    // 리스트뷰에 붙일 adapter 변수 생성
    private lateinit var saveCateAdapter: SaveCateAdapter

    // 현재 액티비티에서 보여줄 xml 화면 생성
    private lateinit var binding: ActivitySavingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // (activity_saving.xml)을 현재 보여줄 화면으로 설정
        binding = ActivitySavingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // xml에서 리스트뷰(cateList)를 가져와서 변수로 선언
        val cateList = binding.cateList

        // CategoryList 클래스를 담는 배열 생성
        val categoryList = ArrayList<SaveCateListData>()

        // 카테고리에 들어갈 목록들 삽입(임의로 넣은 데이터) : 기능 연결할 때 이 부분 다뤄줘야함...
        categoryList.add(SaveCateListData("우정"))
        categoryList.add(SaveCateListData("사랑"))
        categoryList.add(SaveCateListData("진로"))
        categoryList.add(SaveCateListData("가족"))
        categoryList.add(SaveCateListData("감정"))

        // adapter 초기화
        saveCateAdapter = SaveCateAdapter(this,categoryList)

        // 리스트뷰에 방금 생성한 adapter를 붙여서 화면에 연결해준다.
        cateList.adapter = saveCateAdapter

    }
}