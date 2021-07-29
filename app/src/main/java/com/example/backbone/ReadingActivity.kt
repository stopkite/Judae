package com.example.backbone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backbone.databinding.ActivityReadingBinding
import com.example.backbone.databinding.ReadContentItemBinding
import com.example.backbone.databinding.ReadQuestionItemBinding

class ReadingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReadingBinding

    private lateinit var binding2: ReadQuestionItemBinding
    private lateinit var binding3: ReadContentItemBinding

    private lateinit var readingAdapter: ReadMultiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReadingBinding.inflate(layoutInflater)
        binding2 = ReadQuestionItemBinding.inflate(layoutInflater)
        binding3 = ReadContentItemBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 질문 xml 에서 가져온 요소들
        val qIcon = binding2.qIcon
        val qTitle = binding2.qTitle
        val aImg = binding2.aImg
        val q_linkLayout = binding2.clLinkArea
        val aIcon = binding2.aIcon
        val aTxt = binding2.aTxt

        // 본문 xml에서 가져온 요소들
        val contentImg = binding3.contentImg
        val c_linkLayout = binding3.clLinkArea
        val docContent = binding3.docContent

        readingAdapter = ReadMultiAdapter()

        //질문 추가
        readingAdapter.addItems(ReadQuestionData(qIcon.drawable,qTitle,aImg.drawable,q_linkLayout,"유튜브","www.youtube.com",resources.getDrawable(R.drawable.ic_launcher_background),
        resources.getDrawable(R.drawable.ic_launcher_background),aIcon.drawable,aTxt))

        // 본문 추가
        readingAdapter.addItems(ReadContentData(contentImg,c_linkLayout,"서울여대","소중대 사이트","www.swu.ac.kr",
        resources.getDrawable(R.drawable.ic_launcher_background),resources.getDrawable(R.drawable.ic_launcher_background),docContent))

        binding.docList.adapter = readingAdapter

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

    }
}