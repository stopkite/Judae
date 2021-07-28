package com.example.backbone

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backbone.databinding.ActivityWritingBinding
import com.example.backbone.databinding.WriteQuestionItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class WritingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWritingBinding
    private lateinit var binding2:WriteQuestionItemBinding

    // 리사이클러뷰에 붙일 어댑터 선언
    private lateinit var writingAdapter: WritingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding2 = WriteQuestionItemBinding.inflate(layoutInflater)

        // write_qustion_item.xml에서 view들 가져오기
        val qIcon = binding2.qIcon
        val aIcon = binding2.aIcon
        val qTitle = binding2.qTitle
        val aTxt = binding2.aTxt
        val addBtn= binding2.addAnswer
        val linkLayout = binding2.clLinkArea

        // 질문 - 대답 기능을 담을 배열 선언
        val writeList = ArrayList<WriteListData>()


        // activity_writing.xml에서 view들 가져오기
        val docContent = binding.docContent
        val docTitle = binding.docTitle

        /*this.WriteID = WriteID
        this.content = content
        this.Title = Title
        this.Date = Date
        this.Category = Category*/

        // 본문 담을 배열 선언
        val writingList = ArrayList<Writing>()


        writingAdapter = WritingAdapter(writeList)

        binding.docList.adapter = writingAdapter


        //뒤로가기 버튼 클릭 리스너
        binding.cancelButton.setOnClickListener {
            // 홈 화면으로 이동
            val backIntent = Intent(this@WritingActivity, HomeActivity::class.java)
            startActivity(backIntent)
            finish()
        }


        //하단의 '본문' 버튼 클릭 리스너
        binding.addContentBTN.setOnClickListener {
            //본문 객체 생성
            var count = 0;
            writingList.add(Writing(count, docContent.toString(), docTitle.toString(), "2021-02-18", "기본"))
            count++

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            //adap.notifyDataSetChanged()
        }

        //하단의 '링크' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            // 링크 생성

        }

        //하단의 '사진' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            // 사진 생성
        }

        //하단의 '질문' 버튼 클릭 리스너
        binding.addQBtn.setOnClickListener {
            // 질문 생성
            writeList.add(WriteListData(qTitle,null,null,null,
                    null,null,null, aTxt, addBtn))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()

        }
        
        //답변 추가 버튼 클릭 리스너
        binding2.addAnswer.setOnClickListener {
            // 답변 생성
            writeList.add(WriteListData(qTitle, null,null,null,
                null,null,null, aTxt, addBtn))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }


        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

    }
}