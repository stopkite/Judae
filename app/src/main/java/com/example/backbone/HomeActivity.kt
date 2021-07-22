 package com.example.backbone

import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backbone.databinding.ActivityHomeBinding

 //홈 화면 액티비티
class HomeActivity : AppCompatActivity() {

     // navigation을 붙이기 위한 코드
     lateinit var drawerLayout: DrawerLayout
     private lateinit var naviAdapter: NavigationAdapter

     // 리사이클러뷰에 붙일 어댑터 선언
     private lateinit var homeDocListAdapter: HomeDocListAdapter

     // HomeActivity 를 유지시켜주기 위한 binding 선언
     private lateinit var binding: ActivityHomeBinding

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

         //DBHelper와 이어주도록 클래스 선언
         var db: DBHelper = DBHelper(this)

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

         //DB에 글 객체를 배열로 받아오기
         var Array: Array<Writing>
         Array = db.getWriting()

         //배열로 받아온 글 객체를 순서대로 출력하기.
         for (i in 0..(Array.size - 1)) {
             myDocList.add(
                 HomeDocListData(
                     resources.getColor(R.color.purple_200, null),
                     "${Array[i].Title}",
                     "${Array[i].Category}",
                     "| ${Array[i].Date}",
                     "|",
                     resources.getDrawable(R.drawable.ic_launcher_background, null),
                     "${Array[i].Question}"
                 )
             )
         }

         // 어댑터 변수 초기화
         homeDocListAdapter = HomeDocListAdapter(this, myDocList)

         // 만든 어댑터 recyclerview에 연결
         docList.adapter = homeDocListAdapter

         // 리사이클러 뷰 타입 설정
         docList.layoutManager = LinearLayoutManager(this)


         // 카테고리 설정 창 뜨게 하는 버튼 리스너
         binding.cateName.setOnClickListener {
             //changeFragment(BottomFragmentList())
             val bottomSheet = BottomFragmentList()
             bottomSheet.show(supportFragmentManager, bottomSheet.tag)
         }




        // 환경 설정 탭(navi)
        // 버튼을 누르면 환경설정 창이 뜨게 만들기
         binding.settingBtn.setOnClickListener {

             binding.root.openDrawer(Gravity.LEFT)
         }

         // 메뉴 목록에 들어갈 데이터 설정
        var naviList = arrayListOf(
            NavigationItemModel("나의 질문 리스트"),
            NavigationItemModel("앱 암호 설정"),
        )

       // 어댑터 설정
       naviAdapter = NavigationAdapter(naviList)

       // 어댑터 연결
       binding.navigationRv.adapter = naviAdapter
         binding.navigationRv.layoutManager = LinearLayoutManager(this)
        binding.navigationRv.setHasFixedSize(true)

        binding.navigationRv.addOnItemTouchListener(NaviTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        // 나의 질문 리스트 화면으로 이동
                        val myQListIntent = Intent(this@HomeActivity,MyQuestionActivity::class.java)
                        startActivity(myQListIntent)
                    }

                    1 -> {
                        // 앱 암호 설정 화면으로 이동
                    }
                }
            }
        }))


    }




}