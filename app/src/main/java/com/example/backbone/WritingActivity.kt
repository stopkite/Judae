package com.example.backbone

import android.opengl.Visibility
import android.os.Bundle
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

        // 글쓰기 기능을 담을 배열 선언
        val writeList = ArrayList<WriteListData>()

        writeList.add(WriteListData(qTitle,resources.getDrawable(R.drawable.ic_launcher_background),linkLayout,
                "공부 안하고 a+ 받는 법","www.youtube.com",resources.getDrawable(R.drawable.ic_launcher_background),resources.getDrawable(R.drawable.ic_launcher_background)
                ,aTxt,null))

        writeList.add(WriteListData(qTitle,resources.getDrawable(R.drawable.ic_launcher_background),linkLayout,
                "공부 안하고 a+ 받는 법","www.youtube.com",resources.getDrawable(R.drawable.ic_launcher_background),resources.getDrawable(R.drawable.ic_launcher_background)
                ,aTxt,null))

        writeList.add(WriteListData(qTitle,resources.getDrawable(R.drawable.ic_launcher_background),linkLayout,
                "공부 안하고 a+ 받는 법","www.youtube.com",resources.getDrawable(R.drawable.ic_launcher_background),resources.getDrawable(R.drawable.ic_launcher_background)
                ,aTxt,null))

        writeList.add(WriteListData(null,null,null,null,
                null,null,null,null
                ,addBtn))

        writingAdapter = WritingAdapter(writeList)

        binding.docList.adapter = writingAdapter

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)


    }
}