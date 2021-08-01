package com.example.backbone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Gallery
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.backbone.databinding.ActivityWritingBinding
import com.example.backbone.databinding.CancelWritingBinding
import com.example.backbone.databinding.WriteContentItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding
import org.jsoup.Jsoup
import org.w3c.dom.Text
import java.io.BufferedInputStream
import java.io.IOException
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.lang.Exception
import java.net.URL
import java.net.URLConnection
import java.sql.Blob


class WritingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWritingBinding

    private lateinit var binding2:WriteQuestionItemBinding
    private lateinit var binding3:WriteContentItemBinding

    private lateinit var binding4:CancelWritingBinding

    // 리사이클러뷰에 붙일 어댑터 선언
   // private lateinit var writingAdapter: WritingAdapter
    private lateinit var writingAdapter:WriteMultiAdapter

    var Gallery = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding2 = WriteQuestionItemBinding.inflate(layoutInflater)
        binding3 = WriteContentItemBinding.inflate(layoutInflater)

        binding4 = CancelWritingBinding.inflate(layoutInflater)

        //저장하기 위한 객체 생성
        //answer 객체
        class sAnswer (questionID: Text, content: Text, data: Text, image: Blob, link: Text){
            var questionID = questionID
            var content = content
            var data = data
            var image = image
            var link = link
        }
        //content 객체
        class sContent (writeID: Text, contentID: Int, content: Text, image: Blob, link: Text) {
            var writeID = writeID
            var contentID = contentID
            var content = content
            var image = image
            var link = link
        }

        //question 객체
        class sQuestion (writeID: Text, contentID: Text, questionID: Int, content: Text) {
            var writeID = writeID
            var contentID = contentID
            var questionID = questionID
            var content = content
        }

        //writing 객체
        class s



        // write_qustion_item.xml에서 view들 가져오기
        val qIcon = binding2.qIcon
        val aIcon = binding2.aIcon
        val qTitle = binding2.qTitle
        val aTxt = binding2.aTxt
        val addBtn = binding2.addAnswer
        val qlinkLayout = binding2.clLinkArea

        //write_content_item.xml에서 view들 가져오기
        val docContent = binding3.docContent
        val contentImg = binding3.contentImg
        val clinkLayout = binding3.clLinkArea
        val clinkIcon = binding3.linkIcon
        val clinkTitle = binding3.linkTitle
        val clinkContent = binding3.linkContent
        val clinkUri = binding3.linkUri
        val clinkInsertTxt = binding3.linkInsertTxt
        val clinkInsertBtn = binding3.linkInsertBtn


        // 글쓰기 취소 버튼 눌렀을 때 뜨는 팝업
        binding.cancelButton.setOnClickListener {

            val mBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme).setView(binding4.root)

            // view의 중복 사용을 방지하기 위한 코드
            if (binding4.root.parent != null)
                (binding4.root.parent as ViewGroup).removeView(binding4.root)

            val mAlertDialog = mBuilder.show()

            // 확인 버튼 다이얼로그
            binding4.confirmBtn.setOnClickListener {
                // 홈 화면으로 이동
                //val backIntent = Intent(this@WritingActivity, HomeActivity::class.java)
                //startActivity(backIntent)
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


        //시작할 때 title과 content만 뜨도록 하기
        binding.contentImg.visibility = View.GONE
        binding.linkContent.visibility = View.GONE
        binding.linkUri.visibility = View.GONE
        binding.linkInsertTxt.visibility = View.GONE
        binding.linkInsertBtn.visibility = View.GONE
        binding.linkIcon.visibility = View.GONE
        binding.linkTitle.visibility = View.GONE
        binding.clLinkArea.visibility = View.GONE


        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

        //하단의 '본문' 버튼 클릭 리스너
        binding.addContentBTN.setOnClickListener {
            //본문 객체 생성
            writingAdapter.addItems(
                WriteContentData(
                    null, null, null, null, null, null,
                    null, null, docContent
                )
            )

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '링크' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            // 본문에 링크 생성
            writingAdapter.addItems(
                WriteContentData(
                    null, clinkInsertTxt, clinkInsertBtn, clinkLayout, clinkTitle.text.toString(), clinkContent.text.toString(),
                    clinkUri.text.toString(), clinkIcon.drawable, null
                )
            )

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '사진' 버튼 클릭 리스너
        binding.addImgBtn.setOnClickListener {
            writingAdapter.addItems(
                WriteContentData(contentImg, null, null, null, null, null,
                    null, null, null
                )
            )

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }


        //하단의 '질문' 버튼 클릭 리스너
        binding.addQBtn.setOnClickListener {
            // 질문 추가
            writingAdapter.addItems(
                WriteQuestionData(
                    qTitle, null, null, null, null, null,
                    null, null, aTxt, null
                )
            )

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()

        }

        // 저장 버튼 클릭 리스너
        binding.editBtn.setOnClickListener {
            // 제목, 본문, 사진, 링크, 질문, 답변 객체에 따로 저장

        }
    }
}
