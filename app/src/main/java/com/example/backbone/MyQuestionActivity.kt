package com.example.backbone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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

        // 화면 설정 코드
        binding = ActivityMyQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 질문 리사이클러뷰 변수 선언
        val myQList = binding.myQQlist

        // 질문 리스트 데이터 넣기
        qList.add(MyQListData(resources.getDrawable(R.drawable.ic_launcher_background),"1.질문 내용이 나타납니다?","질문에 적힌 글의 제목을 표시합니다."))
        qList.add(MyQListData(resources.getDrawable(R.drawable.ic_launcher_background),"2.질문 내용이 나타납니다?","질문에 적힌 글의 제목을 표시합니다."))
        qList.add(MyQListData(resources.getDrawable(R.drawable.ic_launcher_background),"3.질문 내용이 나타납니다?","질문에 적힌 글의 제목을 표시합니다."))
        qList.add(MyQListData(resources.getDrawable(R.drawable.ic_launcher_background),"4.질문 내용이 나타납니다?","질문에 적힌 글의 제목을 표시합니다."))

        // 어댑터 변수 초기화
        qAdapter = MyQListAdapter(qList)

        // 만든 어댑터 리사이클러뷰에 연결
        myQList.adapter = qAdapter

        // 아이템 구분선 색상 설정
        val dividerItemDecoration = DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recycler_divider_qlist))

        // 아이템 구분선 삽입
        myQList.addItemDecoration(dividerItemDecoration)



    }
}