package com.example.backbone

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.ActivityReadingBinding
import com.example.backbone.databinding.PopupWritingDelBinding
import com.example.backbone.databinding.ReadContentItemBinding
import com.example.backbone.databinding.ReadQuestionItemBinding
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URL
import java.net.URLConnection

class ReadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadingBinding

    private lateinit var binding2: ReadQuestionItemBinding
    private lateinit var binding3: ReadContentItemBinding
    private lateinit var binindg4: PopupWritingDelBinding

    private lateinit var readingAdapter: ReadMultiAdapter

    // 링크 삽입 관련 메소드
    var linkUri: String = ""
    var linktitle: String = ""
    var bm1: Bitmap? = null
    var url1: URL? = null
    var content: String = ""
    private var isrun: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //DBHelper와 이어주도록 클래스 선언
        var db: DBHelper = DBHelper(this)

        binding = ActivityReadingBinding.inflate(layoutInflater)
        binding2 = ReadQuestionItemBinding.inflate(layoutInflater)
        binding3 = ReadContentItemBinding.inflate(layoutInflater)
        var binding4 = PopupWritingDelBinding.inflate(layoutInflater)

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
        var WriteID: String = intent.getStringExtra("data").toString()

        //맨처음 본문-질문에 띄울 내용 불러오기.(multi adapter 사용X)
        var WritingArray: ArrayList<Content> = db.getWriting("${WriteID}")
        binding.docTitle.setText("${WritingArray[0].WritingTitle}")
        binding.docContent.setText("${WritingArray[0].content}")
        if (WritingArray[0].link != "" && WritingArray[0].link != "http://wwww.youtube.com/watch?v=MDeuDrlBKqwhttp://wwww.youtube.com/watch?v=MDeuDrlBKqw") {
            //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
            isrun = true
            //쓰레드 실행(한번만 실행함.)
            loadLink(WritingArray[0].link)
        } else {
            binding.clLinkArea.visibility = View.GONE
        }

        var WritingSize = WritingArray.size

        //한 글 내용에 들어가 있는 질문 객체 리스트 구하기. 1-1), 1-2)번 질문의 ID
        var QuestionIDArray: ArrayList<Question> = db.getQuestionID(WritingArray[0].WriteID, WritingArray[0].ContentID.toString())
        var QuestionIDSize = QuestionIDArray.size
        for (i in 0..QuestionIDSize - 1) {
            //Question에 해당하는 대답 객체 리스트 받아오기
            var AnswerArray: ArrayList<Answer> = db.getAnswer(QuestionIDArray[i].QuestionID)
            var AnswerSize = AnswerArray.size
            var LastSize = AnswerSize - 1
            if (AnswerSize == 1) {
                var Image: Bitmap? = null
                if (AnswerArray[0].Image != null) {
                    Image = init(AnswerArray[0].Image)
                }

                //답변이 한 개일 경우.
                readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, Image, q_linkLayout, null, AnswerArray[0].Link, null,
                        null, AnswerArray[0].Content, AnswerArray[0].Date, false))
            } else if (AnswerSize > 1) {
                //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                //답변의 갯수 만큼 반복문 - 첫번째
                //
                for (j in 0..AnswerSize - 2) {
                    var Image: Bitmap? = null
                    if (AnswerArray[j].Image != null) {
                        Image = init(AnswerArray[j].Image)
                    }

                    if (j == 0) {
                        readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, Image, q_linkLayout, null, AnswerArray[j].Link, null,
                                null, AnswerArray[j].Content, AnswerArray[j].Date, true))
                        readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                    } else {
                        readingAdapter.addItems(ReadQuestionData(null, Image, q_linkLayout, null, AnswerArray[j].Link, null,
                                null, AnswerArray[j].Content, AnswerArray[j].Date, true))
                        readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                    }
                }
                var Image: Bitmap? = null
                if (AnswerArray[LastSize].Image != null) {
                    Image = init(AnswerArray[LastSize].Image)
                }
                //마지막 내용!
                readingAdapter.addItems(ReadQuestionData(null, Image, q_linkLayout, null, AnswerArray[LastSize].Link, null,
                        null, AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
            } else {
                //질문만 있고, 대답 없는 경우.
                readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, null, q_linkLayout, null, null, null,
                        null, null, null, false))

            }
        }


        //맨 처음 내용을 출력한 후 그다음 부터 본문 Content 덩이를 출력함.
        for (i in 1..WritingSize - 1) {
            var Image: Bitmap? = null
            if (WritingArray[i].Image != null) {
                Image = init(WritingArray[i].Image)
            }

            // 본문 추가
            readingAdapter.addItems(ReadContentData(Image, c_linkLayout, null, null, WritingArray[i].link,
                    null, null, WritingArray[i].content))

            //한 글 내용에 들어가 있는 질문 객체 리스트 구하기. 1-1), 1-2)번 질문의 ID
            var QuestionIDArray: ArrayList<Question> = db.getQuestionID(WritingArray[i].WriteID, WritingArray[i].ContentID.toString())
            var QuestionIDSize = QuestionIDArray.size

            for (i in 0..QuestionIDSize - 1) {

                //Question에 해당하는 대답 객체 리스트 받아오기
                var AnswerArray: ArrayList<Answer> = db.getAnswer(QuestionIDArray[i].QuestionID)
                var AnswerSize = AnswerArray.size
                var LastSize = AnswerSize - 1

                if (AnswerSize == 1) {
                    var Image: Bitmap? = null
                    if (AnswerArray[0].Image != null) {
                        Image = init(AnswerArray[0].Image)
                    }

                    //답변이 한 개일 경우.
                    readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, Image, q_linkLayout, null, AnswerArray[0].Link, null,
                            null, AnswerArray[0].Content, AnswerArray[0].Date, false))
                } else if (AnswerSize > 1) {
                    //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                    //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                    //답변의 갯수 만큼 반복문 - 첫번째
                    //
                    for (j in 0..AnswerSize - 2) {
                        var Image: Bitmap? = null
                        if (AnswerArray[j].Image != null) {
                            Image = init(AnswerArray[j].Image)
                        }

                        if (j == 0) {
                            readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, Image, q_linkLayout, null, AnswerArray[j].Link, null,
                                    null, AnswerArray[j].Content, AnswerArray[j].Date, true))
                            readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                        } else {
                            readingAdapter.addItems(ReadQuestionData(null, Image, q_linkLayout, null, AnswerArray[j].Link, null,
                                    null, AnswerArray[j].Content, AnswerArray[j].Date, true))
                            readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                        }
                    }
                    var Image: Bitmap? = null
                    if (AnswerArray[LastSize].Image != null) {
                        Image = init(AnswerArray[LastSize].Image)
                    }
                    //마지막 내용!
                    readingAdapter.addItems(ReadQuestionData(null, Image, q_linkLayout, null, AnswerArray[LastSize].Link, null,
                            null, AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
                } else {
                    //질문만 있고, 대답 없는 경우.
                    readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, null, q_linkLayout, null, null, null,
                            null, null, null, false))

                }
            }

        }

        binding.docList.adapter = readingAdapter

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.editBtn.setOnClickListener {
            // 글쓰기 화면으로 이동
            val writeIntent = Intent(this, EditingActivity::class.java)
            writeIntent.putExtra("data", "${WriteID}")
            startActivity(writeIntent)
        }

        binding.delBtn.setOnClickListener {
            // 글 삭제 팝업
            val mBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme).setView(binding4.root)

            // view의 중복 사용을 방지하기 위한 코드
            if (binding4.root.parent != null)
                (binding4.root.parent as ViewGroup).removeView(binding4.root)

            val mAlertDialog = mBuilder.show()

            // 확인 버튼 다이얼로그
            binding4.delBtn.setOnClickListener {

                //삭제 클릭 리스너
                // 해당 글에 속하는 ContentID를 받아옴.
                var WritingArray: ArrayList<Content> = db.getWriting("${WriteID}")


                for (i in 0..WritingArray.size - 1) {
                    db.deleteContent(WritingArray[i].ContentID.toString())

                    //Content에 해당하는 QuestionID를 받아옴.
                    var QuestionIDArray: ArrayList<Question> = db.getQuestionID(WritingArray[i].WriteID, WritingArray[i].ContentID.toString())

                    for (i in 0..QuestionIDArray.size - 1) {
                        // 해당하는 Question&Answer를 데베에서 삭제
                        db.deleteQuestion(QuestionIDArray[i].QuestionID)
                    }
                }

                 // 모든 내용들이 다 삭제되었으면, 글 삭제.

                 db.deleteWriting(WriteID.toString())

                finish()
            }

            //취소 버튼 다이얼로그
            binding4.cancelBtn.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
        //DBHelper와 이어주도록 클래스 선언
        var db: DBHelper = DBHelper(this)


        // 질문 xml 에서 가져온 요소들
        var q_linkLayout = binding2.clLinkArea
        var c_linkLayout = binding3.clLinkArea

        readingAdapter = ReadMultiAdapter(this)

        //인텐트 값으로 해당 글의 WriteID를 받아오기
        var WriteID: String = intent.getStringExtra("data").toString()

        //맨처음 본문-질문에 띄울 내용 불러오기.(multi adapter 사용X)
        var WritingArray: ArrayList<Content> = db.getWriting("${WriteID}")
        binding.docTitle.setText("${WritingArray[0].WritingTitle}")
        binding.docContent.setText("${WritingArray[0].content}")
        if (WritingArray[0].link != "" && WritingArray[0].link != "http://wwww.youtube.com/watch?v=MDeuDrlBKqwhttp://wwww.youtube.com/watch?v=MDeuDrlBKqw") {
            //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
            isrun = true
            //쓰레드 실행(한번만 실행함.)
            loadLink(WritingArray[0].link)
        } else {
            binding.clLinkArea.visibility = View.GONE
        }
        //사진 띄우기 **** - 나중에 하기.
        if (WritingArray[0].Image != null) {
            //binding.contentImg.setImageBitmap()
        } else {
            binding.contentImg.visibility = View.GONE
        }

        var WritingSize = WritingArray.size

        //한 글 내용에 들어가 있는 질문 객체 리스트 구하기. 1-1), 1-2)번 질문의 ID
        var QuestionIDArray: ArrayList<Question> = db.getQuestionID(WritingArray[0].WriteID, WritingArray[0].ContentID.toString())
        var QuestionIDSize = QuestionIDArray.size
        for (i in 0..QuestionIDSize - 1) {
            //Question에 해당하는 대답 객체 리스트 받아오기
            var AnswerArray: ArrayList<Answer> = db.getAnswer(QuestionIDArray[i].QuestionID)
            var AnswerSize = AnswerArray.size
            var LastSize = AnswerSize - 1
            if (AnswerSize == 1) {
                var Image: Bitmap? = null
                if (AnswerArray[0].Image != null) {
                    Image = init(AnswerArray[0].Image)
                }

                //답변이 한 개일 경우.
                readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, Image, q_linkLayout, null, AnswerArray[0].Link, null,
                    null, AnswerArray[0].Content, AnswerArray[0].Date, false))
            } else if (AnswerSize > 1) {
                //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                //답변의 갯수 만큼 반복문 - 첫번째
                //
                for (j in 0..AnswerSize - 2) {
                    var Image: Bitmap? = null
                    if (AnswerArray[j].Image != null) {
                        Image = init(AnswerArray[j].Image)
                    }

                    if (j == 0) {
                        readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, Image, q_linkLayout, null, AnswerArray[j].Link, null,
                            null, AnswerArray[j].Content, AnswerArray[j].Date, true))
                        readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                    } else {
                        readingAdapter.addItems(ReadQuestionData(null, Image, q_linkLayout, null, AnswerArray[j].Link, null,
                            null, AnswerArray[j].Content, AnswerArray[j].Date, true))
                        readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                    }
                }
                var Image: Bitmap? = null
                if (AnswerArray[LastSize].Image != null) {
                    Image = init(AnswerArray[LastSize].Image)
                }
                //마지막 내용!
                readingAdapter.addItems(ReadQuestionData(null, Image, q_linkLayout, null, AnswerArray[LastSize].Link, null,
                    null, AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
            } else {
                //질문만 있고, 대답 없는 경우.
                readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, null, q_linkLayout, null, null, null,
                    null, null, null, false))

            }
        }


        //맨 처음 내용을 출력한 후 그다음 부터 본문 Content 덩이를 출력함.
        for (i in 1..WritingSize - 1) {
            var Image: Bitmap? = null
            if (WritingArray[i].Image != null) {
                Image = init(WritingArray[i].Image)
            }

            // 본문 추가
            readingAdapter.addItems(ReadContentData(Image, c_linkLayout, null, null, WritingArray[i].link,
                null, null, WritingArray[i].content))

            //한 글 내용에 들어가 있는 질문 객체 리스트 구하기. 1-1), 1-2)번 질문의 ID
            var QuestionIDArray: ArrayList<Question> = db.getQuestionID(WritingArray[i].WriteID, WritingArray[i].ContentID.toString())
            var QuestionIDSize = QuestionIDArray.size

            for (i in 0..QuestionIDSize - 1) {

                //Question에 해당하는 대답 객체 리스트 받아오기
                var AnswerArray: ArrayList<Answer> = db.getAnswer(QuestionIDArray[i].QuestionID)
                var AnswerSize = AnswerArray.size
                var LastSize = AnswerSize - 1

                if (AnswerSize == 1) {
                    var Image: Bitmap? = null
                    if (AnswerArray[0].Image != null) {
                        Image = init(AnswerArray[0].Image)
                    }

                    //답변이 한 개일 경우.
                    readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, Image, q_linkLayout, null, AnswerArray[0].Link, null,
                        null, AnswerArray[0].Content, AnswerArray[0].Date, false))
                } else if (AnswerSize > 1) {
                    //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                    //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                    //답변의 갯수 만큼 반복문 - 첫번째
                    //
                    for (j in 0..AnswerSize - 2) {
                        var Image: Bitmap? = null
                        if (AnswerArray[j].Image != null) {
                            Image = init(AnswerArray[j].Image)
                        }

                        if (j == 0) {
                            readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, Image, q_linkLayout, null, AnswerArray[j].Link, null,
                                null, AnswerArray[j].Content, AnswerArray[j].Date, true))
                            readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                        } else {
                            readingAdapter.addItems(ReadQuestionData(null, Image, q_linkLayout, null, AnswerArray[j].Link, null,
                                null, AnswerArray[j].Content, AnswerArray[j].Date, true))
                            readingAdapter.notifyItemChanged(readingAdapter.itemCount, "color")
                        }
                    }
                    var Image: Bitmap? = null
                    if (AnswerArray[LastSize].Image != null) {
                        Image = init(AnswerArray[LastSize].Image)
                    }
                    //마지막 내용!
                    readingAdapter.addItems(ReadQuestionData(null, Image, q_linkLayout, null, AnswerArray[LastSize].Link, null,
                        null, AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
                } else {
                    //질문만 있고, 대답 없는 경우.
                    readingAdapter.addItems(ReadQuestionData(QuestionIDArray[i].Content, null, q_linkLayout, null, null, null,
                        null, null, null, false))

                }
            }
        }

        binding.docList.adapter = readingAdapter
    }

    private fun init(ba: ByteArray?): Bitmap? {
        val bitmap = BitmapFactory.decodeByteArray(ba, 0, ba!!.size)
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
                        isrun = false
                    }
                    this@ReadingActivity.runOnUiThread(java.lang.Runnable {
                        //어답터 연결하기
                        binding.docList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        if (title == null) {
                            binding.clLinkArea.visibility = View.GONE
                        }
                        var adapter = ReadMultiAdapter(this)
                        if (bm1 == null) {
                            binding.linkIcon.visibility = View.GONE
                        }
                        binding.docList.adapter = adapter
                        binding.linkUri.text = linkUri
                        binding.linkTitle.text = title
                        binding.linkContent.text = content
                        binding.linkIcon.setImageBitmap(bm1)
                        adapter.notifyDataSetChanged()
                        isrun = false
                    })
                } catch (e: Exception) {
                    //링크가 올바르지 않을때->안내 토스트 메시지를 띄움
                }
            }
        }).start()
    }
}