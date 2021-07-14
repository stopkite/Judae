package com.example.backbone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backbone.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    // 리사이클러뷰에 붙일 어댑터 선언
    private lateinit var homeDocListAdapter: HomeDocListAdapter

    // HomeActivity 를 유지시켜주기 위한 binding 선언
    private lateinit var binding:ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // (activity_home.xml)을 현재 보여줄 화면으로 설정!
        // activity_home.xml 에서 해당 요소들 가져오고 싶을 때
        // binding.아이디 이름 으로 가져오면 됩니다.
        // (단, 이때 아이디는 완전 똑같지 않음 : 대문자/소문자가 바뀜 -> 하다보면 알 거임!
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // xml에서 리사이클러뷰(docList)를 가져와서 변수 선언
        val docList = binding.docList

        // HomeDocListData 클래스를 담는 배열 생성
        val myDocList = ArrayList<HomeDocListData>()

        // 임시 데이터 집어 넣어봄!  (수영님 여기다 테스트 하면 됩니다.)
        myDocList.add(
            HomeDocListData(resources.getColor(R.color.purple_200,null),"글의 제목을 표시합니다.","카테고리 이름",
            "| 2021.07.21","|",resources.getDrawable(R.drawable.ic_launcher_background,null),"3")
        )

        myDocList.add(HomeDocListData(resources.getColor(R.color.purple_700),"레이아웃 쫌 빡세네요","앱 개발",
            "| 2021.07.22",null,null,null))


        // 어댑터 변수 초기화
        homeDocListAdapter = HomeDocListAdapter(this,myDocList)

        // 만든 어댑터 recyclerview에 연결
        docList.adapter = homeDocListAdapter

        // 리사이클러 뷰 타입 설정
        docList.layoutManager = LinearLayoutManager(this)







    }
}