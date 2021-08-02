package com.example.backbone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backbone.databinding.ActivityHomeBinding
import com.example.backbone.databinding.ActivityMyQuestionBinding

class MyQuestionActivity : AppCompatActivity() {

    // 화면 설정 코드
    private lateinit var binding:ActivityMyQuestionBinding

    // 질문 리스트를 담기 위한 배열 생성
    var qList = ArrayList<MyQListData>()

    // 질문 리스트에 붙여줄 adpater 생성
    private lateinit var qAdapter:MyQListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //DBHelper와 이어주도록 클래스 선언
        var db: DBHelper = DBHelper(this)

        // 화면 설정 코드
        binding = ActivityMyQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 질문 리사이클러뷰 변수 선언
        val myQList = binding.myQQlist

        // 질문 객체 생성
        // 질문에 엮인 글의 제목 연결
        var Array: ArrayList<Question> = db.getQuestion()

        // 질문 리스트 데이터 넣기
        for (i in 0..(Array.size - 1)) {
            qList.add(MyQListData(resources.getDrawable(R.drawable.ic_q_list_question), "${Array[i].Content}", "${Array[i].WritingTitle}"," ${Array[i].WritingID}"))
        }

        // 어댑터 변수 초기화
        qAdapter = MyQListAdapter(qList)

        // 만든 어댑터 리사이클러뷰에 연결
        myQList.adapter = qAdapter

        // 아이템 구분선 색상 설정
        val dividerItemDecoration = DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recycler_divider_qlist))

        // 아이템 구분선 삽입
        myQList.addItemDecoration(dividerItemDecoration)

        //뒤로가기 버튼 클릭 리스너
        binding.myQBackBtn.setOnClickListener {
            // 홈 화면으로 이동
            val HomeIntent = Intent(this@MyQuestionActivity, HomeActivity::class.java)
            HomeIntent.putExtra("home", "q")
            startActivity(HomeIntent)
            finish()
        }

    }
}