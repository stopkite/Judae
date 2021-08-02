package com.example.backbone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.ActivityReadingBinding
import com.example.backbone.databinding.ReadContentItemBinding
import com.example.backbone.databinding.ReadQuestionItemBinding
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URL
import java.net.URLConnection

class ReadingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReadingBinding

    private lateinit var binding2: ReadQuestionItemBinding
    private lateinit var binding3: ReadContentItemBinding

    private lateinit var readingAdapter: ReadMultiAdapter

    // 링크 삽입 관련 메소드
    var linkUri: String = ""
    var linktitle: String = ""
    var bm1: Bitmap? = null
    var url1: URL? = null
    var content:String = ""
    private var isrun:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //DBHelper와 이어주도록 클래스 선언
        var db: DBHelper = DBHelper(this)

        var binding = ActivityReadingBinding.inflate(layoutInflater)
        var binding2 = ReadQuestionItemBinding.inflate(layoutInflater)
        var binding3 = ReadContentItemBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 질문 xml 에서 가져온 요소들
        var qTitle = binding2.qTitle
        var aImg = binding2.aImg
        var q_linkLayout = binding2.clLinkArea
        var aTxt = binding2.aTxt

        // 본문 xml에서 가져온 요소들
        var contentImg = binding3.contentImg
        var c_linkLayout = binding3.clLinkArea
        var docContent = binding3.docContent

        readingAdapter = ReadMultiAdapter(this)

        //인텐트 값으로 해당 글의 WriteID를 받아오기
        var WriteID:String  = intent.getStringExtra("data").toString()

        //맨처음 본문-질문에 띄울 내용 불러오기.(multi adapter 사용X)
        var WritingArray: Array<Content> = db.getWriting("${WriteID}")
        binding.docTitle.setText("${WritingArray[0].WritingTitle}")
        binding.docContent.setText("${WritingArray[0].content}")

        if(WritingArray[0].link != "")
        {
            //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
            isrun = true
            //쓰레드 실행(한번만 실행함.)
            loadLink(WritingArray[0].link)
        }else{
            binding.clLinkArea.visibility = View.GONE
        }
        //사진 띄우기 **** - 나중에 하기.
        if(WritingArray[0].Image != null)
        {
            //binding.contentImg.setImageBitmap()
        }else{
            binding.contentImg.visibility = View.GONE
        }

        var WritingSize = WritingArray.size

        //한 글 내용에 들어가 있는 질문 객체 리스트 구하기. 1-1), 1-2)번 질문의 ID
        var QuestionIDArray: Array<Question> = db.getQuestionID(WritingArray[0].WriteID, WritingArray[0].ContentID.toString())
        var QuestionIDSize = QuestionIDArray.size
        Log.d("태그", "짠짠짠 ${QuestionIDSize}")
        for(i in 0..QuestionIDSize-1)
        {
            //Question에 해당하는 대답 객체 리스트 받아오기
            var AnswerArray: Array<Answer> = db.getAnswer(QuestionIDArray[i].QuestionID)
            var AnswerSize = AnswerArray.size
            var LastSize = AnswerSize-1
            if(AnswerSize==1)
            {
                Log.d("태그", "짠짠짠 ${QuestionIDArray[i].Content}")
                //답변이 한 개일 경우.
                readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content,AnswerArray[0].Image,q_linkLayout,null,AnswerArray[0].Link,null,
                        null,AnswerArray[0].Content, AnswerArray[0].Date, false))
            } else if(AnswerSize>1)
            {
                //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                //답변의 갯수 만큼 반복문 - 첫번째
                //
                for(j in 0..AnswerSize-2)
                {
                    if(j==0)
                    {
                        readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                        readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                    }else{
                        readingAdapter.addItems(ReadQuestionData(null,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                        readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                    }
                }
                //마지막 내용!
                readingAdapter.addItems(ReadQuestionData(null,AnswerArray[LastSize].Image,q_linkLayout,null,AnswerArray[LastSize].Link,null,
                        null,AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
            }else{
                //질문만 있고, 대답 없는 경우.
                readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                        null,null, null, false))

            }
        }


        //맨 처음 내용을 출력한 후 그다음 부터 본문 Content 덩이를 출력함.
        for(i in 1..WritingSize-1)
        {
            // 본문 추가
            readingAdapter.addItems(ReadContentData(WritingArray[i].Image,c_linkLayout,null,null,WritingArray[i].link,
                    null,null,WritingArray[i].content))

            //한 글 내용에 들어가 있는 질문 객체 리스트 구하기. 1-1), 1-2)번 질문의 ID
            var QuestionIDArray: Array<Question> = db.getQuestionID(WritingArray[i].WriteID, WritingArray[i].ContentID.toString())
            var QuestionIDSize = QuestionIDArray.size

            for(i in 0..QuestionIDSize-1)
            {
                //Question에 해당하는 대답 객체 리스트 받아오기
                var AnswerArray: Array<Answer> = db.getAnswer(QuestionIDArray[i].QuestionID)
                var AnswerSize = AnswerArray.size
                var LastSize = AnswerSize-1
                if(AnswerSize==1)
                {
                    //답변이 한 개일 경우.
                    readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content,AnswerArray[0].Image,q_linkLayout,null,AnswerArray[0].Link,null,
                            null,AnswerArray[0].Content, AnswerArray[0].Date, false))
                } else if(AnswerSize>1)
                {
                    //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                    //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                    //답변의 갯수 만큼 반복문 - 첫번째
                    //
                    for(j in 0..AnswerSize-2)
                    {
                        if(j==0)
                        {
                            readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                    null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                            readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                        }else{
                            readingAdapter.addItems(ReadQuestionData(null,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                    null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                            readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                        }
                    }
                    //마지막 내용!
                    readingAdapter.addItems(ReadQuestionData(null,AnswerArray[LastSize].Image,q_linkLayout,null,AnswerArray[LastSize].Link,null,
                            null,AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
                }else{
                    //질문만 있고, 대답 없는 경우.
                    readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                            null,null, null, false))

                }
            }

        }

        binding.docList.adapter = readingAdapter

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)
        //var ba:ByteArray = db.showImage("","")
        //init(ba)

        //그림 저장하는 코드
        //val drawable = getDrawable(R.mipmap.ic_launcher)
        //val bitmap = bitmapDrawable.bitmap
        //var byteArray: ByteArray = drawableToByteArray(drawable!!)
        //val image = Image(byteArray)
        //db.drawImage("","", image)
        //그림 읽어오는 코드
        //val memo = db.showImage("","")
        //val bitmap = init(memo.image)
        //binding.contentImg.setImageDrawable(drawable)

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun init(ba:ByteArray): Bitmap? {
        val bitmap = BitmapFactory.decodeByteArray(ba, 0,ba.size)
        return bitmap
    }

    private fun loadLink(linkUri: String) {
        //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
        linktitle = ""
        bm1 = null
        url1 = null
        content = ""
        Thread(Runnable {
            while (isrun) {//네이버의 경우에만 해당되는 것 같아.
                try {
                    if (linkUri.contains("naver")) {
                        //linkIcon에 파비콘 추출해서 삽입하기
                        val doc = Jsoup.connect("${linkUri}").get()

                        //제목 들고 오기
                        val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")
                        if (linkUri.contains("blog")) {
                            val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
                            title = doc2.title()
                            content = doc2.select("meta[property=\"og:description\"]").attr("content")
                        } else if (linkUri == "https://www.naver.com/") {
                            title = doc.title()
                            content = doc.select("meta[name=\"og:description\"]").attr("content")
                        } else {
                            title = doc.title()
                            content = doc.select("meta[property=\"og:description\"]").attr("content")
                        }

                        url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                        var conn: URLConnection = url1!!.openConnection()
                        conn.connect()
                        var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                        bm1 = BitmapFactory.decodeStream(bis)

                        bis.close()
                        isrun = false
                    } else {
                        val doc = Jsoup.connect("${linkUri}").get()
                        var favicon: String
                        var link: String
                        if (linkUri.contains("google")) {
                            favicon = doc.select("meta[itemprop=\"image\"]").attr("content")
                            link = "https://www.google.com" + favicon
                            url1 = URL("${link}")
                        } else {
                            //파비콘 이미지 들고 오기
                            favicon = doc.select("link[rel=\"icon\"]").attr("href")
                            if (favicon == "") {
                                favicon = doc.select("link[rel=\"SHORTCUT ICON\"]").attr("href")
                            }
                            if (!favicon.contains("https:")) {
                                link = "https://" + favicon
                                url1 = URL("${link}")
                            } else {
                                url1 = URL("${favicon}")
                            }
                        }

                        try {
                            var conn: URLConnection = url1!!.openConnection()
                            conn.connect()
                            var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                            bm1 = BitmapFactory.decodeStream(bis)
                            bis.close()
                        } catch (e: Exception) {

                        }
                        title = doc.title()
                        content = doc.select("meta[name=\"description\"]").attr("content")
                        if (content == "") {
                            content = doc.select("meta[property=\"og:site_name\"]").attr("content")
                        }
                        if (title == "") {
                            title = doc.select("meta[property=\"og:site_name\"]").attr("content")
                        }
                        if (bm1 == null) {
                            binding.linkIcon.visibility = View.GONE
                        }
                        isrun = false
                    }
                    this@ReadingActivity.runOnUiThread(java.lang.Runnable {
                        //어답터 연결하기
                        binding.docList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        var adapter = ReadMultiAdapter(this)
                        binding.docList.adapter = adapter
                        binding.linkUri.text = linkUri
                        binding.linkTitle.text = title
                        binding.linkContent.text = content
                        binding.linkIcon.setImageBitmap(bm1)
                    })
                } catch (e: Exception) {
                    //링크가 올바르지 않을때->안내 토스트 메시지를 띄움

                }
            }
        }).start()
    }

    //이미지를 ByteArray로 만드는 함수
    private fun drawableToByteArray(drawable: Drawable): ByteArray {
        val bitmapDrawable = drawable as BitmapDrawable?
        val bitmap = bitmapDrawable?.bitmap
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        return byteArray
    }
}