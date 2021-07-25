 package com.example.backbone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backbone.databinding.ActivityHomeBinding

 //홈 화면 액티비티
class HomeActivity : AppCompatActivity() {

    // 리사이클러뷰에 붙일 어댑터 선언
    lateinit var homeDocListAdapter: HomeDocListAdapter

    // HomeActivity 를 유지시켜주기 위한 binding 선언
    private lateinit var binding:ActivityHomeBinding

    lateinit var docList: androidx.recyclerview.widget.RecyclerView

     // navigation을 붙이기 위한 코드
     lateinit var drawerLayout: DrawerLayout
     private lateinit var naviAdapter: NavigationAdapter

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
        docList = binding.docList

        // HomeDocListData 클래스를 담는 배열 생성
        val myDocList = ArrayList<HomeDocListData>()

        //DB에 글 객체를 배열로 받아오기
        var Array: Array<Writing>
        Array = db.getWriting()


        //배열로 받아온 글 객체를 순서대로 출력하기.
        for (i in 0..(Array.size - 1)) {
            myDocList.add(
                    HomeDocListData(resources.getColor(R.color.purple_200, null), "${Array[i].Title}", "${Array[i].Category}",
                            "| ${Array[i].Date}", "|", resources.getDrawable(R.drawable.ic_launcher_background, null), "${Array[i].Question}")
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
            loadCategory(db)
        }


        // 환경 설정 탭
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
                        val myQListIntent = Intent(this@HomeActivity, MyQuestionActivity::class.java)
                        startActivity(myQListIntent)
                    }

                    1 -> {
                        // 앱 암호 설정 화면으로 이동
                        val lockScreenMenuIntent = Intent(this@HomeActivity, LockScreenMenuActivity::class.java)
                        startActivity(lockScreenMenuIntent)
                    }
                }
            }
        }))

        //검색 버튼 클릭 리스너
        binding.searchBtn.setOnClickListener {
            // 홈 화면으로 이동
            val searchIntent = Intent(this@HomeActivity, SearchActivity::class.java)
            startActivity(searchIntent)
        }

    }

     //화면 띄우기 함수
     fun Load(){

     }

     //카테고리 수정 함수
     fun fragmentChange_for_adapter(cate:String ) {
         var db: DBHelper = DBHelper(this)
         val bottomSheet = BottomFragmentEdit(db, cate)

         bottomSheet.show(supportFragmentManager, bottomSheet.tag)
     }

     override fun onStop() {
         super.onStop()
         //이전 화면으로 돌아올 때 메뉴바가 닫힌 상태로 돌아올 수 있게 해주는 코드
         binding.root.closeDrawers()
     }

     //BottomFragmentList.kt에서 카테고리 추가 버튼을 눌렀을 때 발생하는 클릭 리스너
     fun onButtonClicked() {
         var db: DBHelper = DBHelper(this)
         val bottomSheet = BottomFragmentAdd(db)
         bottomSheet.show(supportFragmentManager, "BottomFragmentAdd")
     }

     //업데이트된 내용 리사이클러뷰에 갱신
     fun loadAdapter(db:DBHelper){
         // HomeDocListData 클래스를 담는 배열 생성
         val myDocList = ArrayList<HomeDocListData>()

         //DB에 글 객체를 배열로 받아오기
         var Array: Array<Writing>
         Array = db.getWriting()

         //배열로 받아온 글 객체를 순서대로 출력하기.
         for (i in 0..(Array.size - 1)) {
             myDocList.add(
                     HomeDocListData(resources.getColor(R.color.purple_200, null), "${Array[i].Title}", "${Array[i].Category}",
                             "| ${Array[i].Date}", "|", resources.getDrawable(R.drawable.ic_launcher_background, null), "${Array[i].Question}")
             )
         }
         homeDocListAdapter.setData(myDocList)
     }

     //수정 후 다시 사용하는 함수
     public fun loadCategory(db: DBHelper){
         //homeDocListAdapter.setHasStableIds(true)
         // HomeDocListData 클래스를 담는 배열 생성
         val myDocList = ArrayList<HomeDocListData>()

         //DB에 글 객체를 배열로 받아오기
         var Array: Array<Writing>
         Array = db.getWriting()

         //배열로 받아온 글 객체를 순서대로 출력하기.
         for (i in 0..(Array.size - 1)) {
             myDocList.add(
                     HomeDocListData(resources.getColor(R.color.purple_200, null), "${Array[i].Title}", "${Array[i].Category}",
                             "| ${Array[i].Date}", "|", resources.getDrawable(R.drawable.ic_launcher_background, null), "${Array[i].Question}")
             )
         }

         homeDocListAdapter.notifyDataSetChanged()

         val bottomSheet = BottomFragmentList(db)
         bottomSheet.show(supportFragmentManager, bottomSheet.tag)
     }

     override fun onRestart() {
         super.onRestart()
     }
}