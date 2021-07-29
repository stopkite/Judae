package com.example.backbone

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backbone.databinding.ActivityWritingBinding
import com.example.backbone.databinding.CancelWritingBinding
import com.example.backbone.databinding.WriteContentItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class WritingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWritingBinding

    private lateinit var binding2:WriteQuestionItemBinding
    private lateinit var binding3:WriteContentItemBinding

    private lateinit var binding4:CancelWritingBinding

    // 리사이클러뷰에 붙일 어댑터 선언
   // private lateinit var writingAdapter: WritingAdapter
    private lateinit var writingAdapter:WriteMultiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding2 = WriteQuestionItemBinding.inflate(layoutInflater)
        binding3 = WriteContentItemBinding.inflate(layoutInflater)

        binding4 = CancelWritingBinding.inflate(layoutInflater)

        // write_qustion_item.xml에서 view들 가져오기
        val qIcon = binding2.qIcon
        val aIcon = binding2.aIcon
        val qTitle = binding2.qTitle
        val aTxt = binding2.aTxt
        val addBtn= binding2.addAnswer
        val linkLayout = binding2.clLinkArea

        //write_content_item.xml에서 view들 가져오기
        val docContent = binding3.docContent
        val contentImg = binding3.contentImg


        // 글쓰기 취소 버튼 눌렀을 때 뜨는 팝업
        binding.cancelButton.setOnClickListener {

            val mBuilder = AlertDialog.Builder(this,R.style.MyDialogTheme).setView(binding4.root)

            // view의 중복 사용을 방지하기 위한 코드
            if(binding4.root.parent != null)
                (binding4.root.parent as ViewGroup).removeView(binding4.root)

            val  mAlertDialog = mBuilder.show()

            // 확인 버튼 다이얼로그
            binding4.confirmBtn.setOnClickListener {
                // 홈 화면으로 이동
                val backIntent = Intent(this@WritingActivity, HomeActivity::class.java)
                startActivity(backIntent)
                finish()
            }

            //취소 버튼 다이얼로그
            binding4.cancelBtn.setOnClickListener {
                mAlertDialog.dismiss()
            }

        }


        //어댑터 연결
        writingAdapter = WriteMultiAdapter()
        binding.docList.adapter = writingAdapter

        //질문 추가
        writingAdapter.addItems(WriteQuestionData(qTitle,null,null,null,null,null,
        null,null,null,aTxt,addBtn))

        //본문추가
        writingAdapter.addItems(WriteContentData(contentImg,null,null,null,null,null,
            null,null,null,docContent))

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)



        //하단의 '본문' 버튼 클릭 리스너
        binding.addContentBTN.setOnClickListener {
            //본문 객체 생성
            var count = 0;
            //writingList.add(Writing(count, docContent.toString(), docTitle.toString(), "2021-02-18", "기본"))
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
//            writeList.add(WriteListData(qTitle,null,null,null,
//                    null,null,null, aTxt, addBtn))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()

        }
        
        //답변 추가 버튼 클릭 리스너
        binding2.addAnswer.setOnClickListener {
            // 답변 생성
//            writeList.add(WriteListData(qTitle, null,null,null,
//                null,null,null, aTxt, addBtn))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }




    }
}