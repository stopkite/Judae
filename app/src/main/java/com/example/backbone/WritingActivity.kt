package com.example.backbone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.backbone.databinding.ActivityWritingBinding
import com.example.backbone.databinding.CancelWritingBinding
import com.example.backbone.databinding.WriteContentItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.io.IOException
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.lang.Exception
import java.net.URL
import java.net.URLConnection
import java.nio.Buffer
import kotlin.concurrent.thread


class WritingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWritingBinding

    private lateinit var binding2:WriteQuestionItemBinding
    private lateinit var binding3:WriteContentItemBinding

    private lateinit var binding4:CancelWritingBinding

    // 리사이클러뷰에 붙일 어댑터 선언
   // private lateinit var writingAdapter: WritingAdapter
    private lateinit var writingAdapter:WriteMultiAdapter

    // 링크 삽입 관련 메소드
    var linkUri: String = ""
    var title: String = ""
    var bm1: Bitmap? = null
    var url1: URL? = null
    var content:String = ""
    private var isrun:Boolean = false



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
        val qlinkLayout = binding2.clLinkArea

        //write_content_item.xml에서 view들 가져오기
        val docContent = binding3.docContent
        val contentImg = binding3.contentImg
        val clinkLayout = binding3.clLinkArea
        val clinkInsertTxt = binding3.linkInsertTxt
        val clinkInsertBtn = binding3.linkInsertBtn


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
        binding.linkImg.visibility = View.GONE
        binding.linkIcon.visibility = View.GONE
        binding.linkTitle.visibility = View.GONE

        binding.clLinkArea.visibility = View.GONE


        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

        binding.linkInsertTxt.setText("https://blog.naver.com/rapael860429")


        //링크 입력 후 확인을 누르면 실행되는 리스너
        binding.linkInsertBtn.setOnClickListener{
            //입력 받은 링크를 String으로 넣어 준 후
            linkUri = binding.linkInsertTxt.getText().toString()
            //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
            isrun = true
            //쓰레드 실행(한번만 실행함.)
            loadLink(linkUri)
        }


        //하단의 '본문' 버튼 클릭 리스너
        binding.addContentBTN.setOnClickListener {
            //본문 객체 생성
            writingAdapter.addItems(WriteContentData(null,null,null,null,null,null,
                null,null,null, docContent))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '링크' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            // 본문에 링크 생성
            writingAdapter.addItems(WriteContentData(null,clinkInsertTxt, clinkInsertBtn, null,null,null,
                null,null,null, null))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '사진' 버튼 클릭 리스너
        binding.addImgBtn.setOnClickListener {
            // 본문에 이미지 생성

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivity(intent)

            writingAdapter.addItems(WriteContentData(contentImg,null,null,null,null,null,
                null,null,null, null))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '질문' 버튼 클릭 리스너
        binding.addQBtn.setOnClickListener {
            // 질문 추가
            writingAdapter.addItems(WriteQuestionData(qTitle,null,null,null,null,null,
                null,null,null,aTxt,null))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()

        }
    }

    private fun loadLink(linkUri: String) {
        //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
        title = ""
        bm1 = null
        url1 = null
        content = ""
        Thread(Runnable {
            while(isrun)
            {//네이버의 경우에만 해당되는 것 같아.
                try{
                    if (linkUri.contains("naver")) {
                        //linkIcon에 파비콘 추출해서 삽입하기
                        Log.d("태그그", "${linkUri}")
                        val doc = Jsoup.connect("${linkUri}").get()

                        //제목 들고 오기
                        val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")
                        if(linkUri.contains("blog"))
                        {
                            val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
                            title = doc2.title()
                            content = doc2.select("meta[property=\"og:description\"]").attr("content")
                        }else if(linkUri == "https://www.naver.com/"){
                            title = doc.title()
                            content = doc.select("meta[name=\"og:description\"]").attr("content")
                        }else{
                            title = doc.title()
                            content = doc.select("meta[property=\"og:description\"]").attr("content")
                        }

                        url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                        var conn: URLConnection = url1!!.openConnection()
                        conn.connect()
                        var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                        bm1 = BitmapFactory.decodeStream(bis)

                        bis.close()
                        isrun=false
                    } else {
                        val doc = Jsoup.connect("${linkUri}").get()
                        var favicon:String
                        var link:String
                        if(linkUri.contains("google"))
                        {
                            favicon = doc.select("meta[itemprop=\"image\"]").attr("content")
                            link = "https://www.google.com"+favicon
                            url1 = URL("${link}")
                        }else{
                            //파비콘 이미지 들고 오기
                            favicon = doc.select("link[rel=\"icon\"]").attr("href")
                            if(favicon=="")
                            {
                                favicon = doc.select("link[rel=\"SHORTCUT ICON\"]").attr("href")
                            }
                            if (!favicon.contains("https:")) {
                                link = "https://"+favicon
                                url1 = URL("${link}")
                            }else{
                                url1 = URL("${favicon}")
                            }
                        }

                        try{
                            var conn: URLConnection = url1!!.openConnection()
                            conn.connect()
                            var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                            bm1 = BitmapFactory.decodeStream(bis)
                            bis.close()
                        }catch (e:Exception)
                        {

                        }
                        title = doc.title()


                        content = doc.select("meta[name=\"description\"]").attr("content")
                        if(content == "")
                        {
                            content = doc.select("meta[property=\"og:site_name\"]").attr("content")
                        }
                        if(title == "")
                        {
                            title = doc.select("meta[property=\"og:site_name\"]").attr("content")
                        }
                        Log.d("태그", "${bm1}")
                        if(bm1==null)
                        {
                            binding.linkIcon.visibility= View.GONE
                        }
                        isrun=false
                    }
                    this@WritingActivity.runOnUiThread(java.lang.Runnable {
                        //어답터 연결하기
                        binding.docList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        var adapter = WriteMultiAdapter()
                        binding.docList.adapter = adapter
                        binding.linkUri.text = linkUri
                        binding.linkTitle.text = title
                        binding.linkContent.text = content
                        binding.linkIcon.setImageBitmap(bm1)
                    })
                }catch(e:Exception){
                    //링크가 올바르지 않을때->안내 토스트 메시지를 띄움

                }
            }
        }).start()
    }
}
