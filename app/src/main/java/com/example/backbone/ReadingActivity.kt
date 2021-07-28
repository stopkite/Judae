package com.example.backbone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backbone.databinding.ActivityReadingBinding
import com.example.backbone.databinding.ReadQuestionItemBinding

class ReadingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReadingBinding
    private lateinit var binding2: ReadQuestionItemBinding

    private lateinit var readingAdapter: ReadingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReadingBinding.inflate(layoutInflater)
        binding2 = ReadQuestionItemBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val qIcon = binding2.qIcon
        val qTitle = binding2.qTitle
        val aIcon = binding2.aIcon
        val linkLayout = binding2.clLinkArea
        val aTxt = binding2.aTxt

        //글(읽기) 기능을 담을 배열 선언
        val readList = ArrayList<ReadListData>()

        readList.add(ReadListData(qIcon,qTitle,resources.getDrawable(R.drawable.ic_launcher_background),linkLayout,
            "공부 안하고 a+ 받는 법","www.youtube.com",resources.getDrawable(R.drawable.ic_launcher_background),resources.getDrawable(R.drawable.ic_launcher_background)
            ,aIcon,aTxt))

        readList.add(ReadListData(null,null,null,
            linkLayout, null,null,null,null
            ,aIcon,aTxt))

        readingAdapter = ReadingAdapter(readList)

        binding.docList.adapter = readingAdapter

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

    }
}