 package com.example.backbone

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        //DB에서 받아올 Writing 객체 리스트를 담는 배열 생성
        var Array: Array<Writing>
        //지정된 카테고리 별로 내용 바꾸기
        var categoryName: String = "전체"
        //DB에 글 객체를 배열로 받아오기
        if(intent.hasExtra("category"))
        {
            categoryName = intent.getStringExtra("category").toString()
            binding.cateName.setText(categoryName)
        }

        Array = db.getCateWriting(categoryName)

        //배열로 받아온 글 객체를 순서대로 출력하기.
        for (i in 0..(Array.size - 1)) {
            myDocList.add(
                    HomeDocListData("${Array[i].WriteID}","${Array[i].Title}", "${Array[i].Category}",
                            "| ${Array[i].Date}", "|", resources.getDrawable(R.drawable.ic_qcount, null), "${Array[i].Question}")
            )
        }

        // 어댑터 변수 초기화
        homeDocListAdapter = HomeDocListAdapter(this, myDocList)

        // 만든 어댑터 recyclerview에 연결
        docList.adapter = homeDocListAdapter

        // 아이템 구분선 색상 설정
        val dividerItemDecoration = DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recycler_divider_qlist))

        // 아이템 구분선 삽입
        binding.root.findViewById<RecyclerView>(R.id.docList).addItemDecoration(dividerItemDecoration)

        // 리사이클러 뷰 타입 설정
        docList.layoutManager = LinearLayoutManager(this)


        // 카테고리 설정 창 뜨게 하는 버튼 리스너
        binding.clDropCate.setOnClickListener {
            //changeFragment(BottomFragmentList())
            loadCategory(db)
        }

        if (intent.hasExtra("home")) {
            // 뒤로가기 버튼을 통해 넘어온 홈화면이라면
            binding.root.openDrawer(Gravity.LEFT)
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

        // 아이템 구분선 색상 설정
        val dividerItemDecoration2 = DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        dividerItemDecoration2.setDrawable(resources.getDrawable(R.drawable.recycler_divider_qlist))

        // 아이템 구분선 삽입
        binding.root.findViewById<RecyclerView>(R.id.navigation_rv).addItemDecoration(dividerItemDecoration)

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

        //글쓰기 화면으로 이동하는 버튼 리스너
        binding.clHomeWriteBtn.setOnClickListener {
            // 글쓰기 화면으로 이동
            val writeIntent = Intent(this@HomeActivity, WritingActivity::class.java)
            startActivity(writeIntent)
        }

    }

     //선언: BottomFragmentList.kt
     //카테고리 수정 함수
     //인자로 받은 값을 수정하는 에디트텍스트 힌트로 설정하기 위해 전달
     fun fragmentChange_for_adapter(cate: String) {
         var db: DBHelper = DBHelper(this)
         val bottomSheet = BottomFragmentEdit(db, cate)
         bottomSheet.show(supportFragmentManager, bottomSheet.tag)
     }

     //선언: BottomFragmentList.kt
     //선택한 카테고리를 홈 화면에 다시 갱신시켜주기 위한 함수
     fun refresh(cate:String, db: DBHelper)
     {
         //홈 화면 상단에 있는 카테고리 정보를 인자에서 받은 BottomFragmentList에서 선택되어진 카테고리 정보로 변경해주기
         binding.cateName.setText(cate)
         
         // 글 리스트를 띄우기 위해 필요한
         // HomeDocListData 클래스를 담는 배열 생성
         val myDocList = ArrayList<HomeDocListData>()

         //DB에 글 객체를 배열로 받아오기
         var Array: Array<Writing>
         //인자로 받아온 선택 되어진 카테고리 이름을 db클래스 함수의 인자로 넘겨줌
         //해당 인자 카테고리와 동일한 카테고리를 가진 글 배열을 받아옴
         Array = db.getCateWriting(cate)

         //배열로 받아온 글 객체를 순서대로 출력하기.
         for (i in 0..(Array.size - 1)) {
             myDocList.add(
                     HomeDocListData("${Array[i].WriteID}","${Array[i].Title}", "${Array[i].Category}",
                             "| ${Array[i].Date}", "|", resources.getDrawable(R.drawable.ic_qcount, null), "${Array[i].Question}")
             )
         }

         //어댑터에 내용이 변경 되었다는 의미의 setData
         //어댑터 setData함수 내에서 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
         homeDocListAdapter.setData(myDocList)
     }

     override fun onStop() {
         super.onStop()
         //이전 화면으로 돌아올 때 메뉴바가 닫힌 상태로 돌아올 수 있게 해주는 코드
         binding.root.closeDrawers()
     }

     //BottomFragmentList.kt에서 카테고리 추가 버튼을 눌렀을 때 발생하는 클릭 리스너
     fun onButtonClicked() {
         var db: DBHelper = DBHelper(this)
         // 추가해주는 프래그먼트와 연결해줌
         val bottomSheet = BottomFragmentAdd(db)
         bottomSheet.show(supportFragmentManager, "BottomFragmentAdd")
     }

     //BottomFragmentEdit.kt에서 선언 되어짐
     //수정 후 다시 사용하는 함수
     //수정되어지면서 변경된 카테고리 이전 카테고리 내용을 가진 글 객체를 모두 찾아 변경된 카테고리 내용으로 변경시켜줌.
     //현재 홈화면에 띄워진 리스트 내용만 갱신해주는 것임! refresh함수와 다름.
     //업데이트된 내용 리사이클러뷰에 갱신한 후 카테고리 리스트 다시 띄워줌
     public fun loadCategory(db: DBHelper){
         // HomeDocListData 클래스를 담는 배열 생성
         val myDocList = ArrayList<HomeDocListData>()

         //DB에 글 객체를 배열로 받아오기
         var Array: Array<Writing>
         Array = db.getCateWriting(binding.cateName.getText().toString())

         //배열로 받아온 글 객체를 순서대로 출력하기.
         for (i in 0..(Array.size - 1)) {
             myDocList.add(
                     HomeDocListData("${Array[i].WriteID}","${Array[i].Title}", "${Array[i].Category}",
                             "| ${Array[i].Date}", "|", resources.getDrawable(R.drawable.ic_qcount, null), "${Array[i].Question}")
             )
         }

         homeDocListAdapter.notifyDataSetChanged()

         val bottomSheet = BottomFragmentList(db)
         bottomSheet.show(supportFragmentManager, bottomSheet.tag)
     }

     override fun onRestart() {
         super.onRestart()
     }

     override fun onBackPressed() {

        // 메뉴가 열린 채로 뒤로가기 버튼을 눌렀을 때
         // 메뉴창을 닫히게 하는 코드
        drawerLayout = binding.drawerLayout

         if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
             drawerLayout.closeDrawer(GravityCompat.START);
         } else {
             super.onBackPressed();
         }
     }
}