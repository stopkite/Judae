package com.example.backbone

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
//import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
//import com.example.backbone.databinding.ActivityWritingBinding
//import com.example.backbone.databinding.CancelWritingBinding
//import com.example.backbone.databinding.WriteContentItemBinding
//import com.example.backbone.databinding.WriteQuestionItemBinding
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.backbone.databinding.*
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


class WritingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWritingBinding

    private lateinit var binding2:WriteQuestionItemBinding
    private lateinit var binding3:WriteContentItemBinding


    private lateinit var binding4:CancelWritingBinding


    // 카테고리 저장 화면
    private lateinit var binding5:ActivitySavingBinding
    // 리스트뷰에 붙일 adapter 변수 생성
    private lateinit var saveCateAdapter: SaveCateAdapter
    private lateinit var binding6:SaveCategoryItemBinding

    // 링크 삽입 관련 메소드
    var linkUri: String = ""
    var linktitle: String = ""
    var bm1: Bitmap? = null
    var url1: URL? = null
    var content:String = ""
    private var isrun:Boolean = false

    //DBHelper와 이어주도록 클래스 선언
    var db: DBHelper = DBHelper(this)

    // 리사이클러뷰에 붙일 어댑터 선언
   // private lateinit var writingAdapter: WritingAdapter
    private lateinit var writingAdapter:WriteMultiAdapter

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private val OPEN_GALLERY = 1
    lateinit var viewPager : ViewPager

    @RequiresApi(Build.VERSION_CODES.O)
    var now = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    var localDateTime: LocalDate = LocalDate.parse(now.toString())
    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    @RequiresApi(Build.VERSION_CODES.O)
    var today = formatter.format(localDateTime)

    // 뒤로 가기 버튼 눌렀을 때
    override fun onBackPressed() {
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

    var writeID: String = ""
    var count = 1

    val writeQuestionList = ArrayList<WriteQuestionData>()
    val writeContentList = ArrayList<WriteContentData>()
    val loadQuestionList = ArrayList<loadQuestionData>()
    val loadContentList = ArrayList<loadContentData>()


    //저장하기 위한 객체 생성
    //answer 객체
    class sAnswer(content: String, date: String, image: Bitmap?, link: String?){
        var content = content
        var date = date
        var image = image
        var link = link
    }
    var AnswerArray = ArrayList<sAnswer>()

    //content 객체
    class sContent(content: String?, image: Bitmap?, link: String?) {
        var content = content
        var image = image
        var link = link
    }
    var ContentArray = ArrayList<sContent>()

    //question 객체
    class sQuestion (content: String) {
        var content = content
    }
    var QuestionArray = ArrayList<sQuestion>()

    //writing 객체
    class sWriting (title: String, date: String, category: String) {
        var title = title
        var date = date
        var category = category
    }



    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {

        //DBHelper와 이어주도록 클래스 선언
        var db: DBHelper = DBHelper(this)

        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 본문과 질문(대답) reyclerview 요소가 담긴 layout
        binding2 = WriteQuestionItemBinding.inflate(layoutInflater)
        binding3 = WriteContentItemBinding.inflate(layoutInflater)

        // 글쓰기 취소 팝업창 레이아웃
        binding4 = CancelWritingBinding.inflate(layoutInflater)


        // 카테고리 저장 요소가 담긴 레이아웃
        binding5 = ActivitySavingBinding.inflate(layoutInflater)
        binding6 = SaveCategoryItemBinding.inflate(layoutInflater)

        // 카테고리 안에 있는 라디오 버튼 변수 가져오기
        var radioBtn = binding6.popupCategoryRbtn
        // xml에서 리스트뷰(cateList)를 가져와서 변수로 선언
        val cateList = binding5.cateList
        // CategoryList 클래스를 담는 배열 생성
        var categoryList = ArrayList<String>()
        //카테고리 정보를 DB에서 받아와 배열에 담기.
        categoryList = db.getCategory()

        //배열의 크기만큼 카테고리 아이템 설정하기.
        //배열로 받아온 글 객체를 순서대로 출력하기.
        /*
                for(i in 0..(categoryList.size-1))
        {
            categoryList.add(
                    SaveCateListData("${categoryList[i]}")
            )
        }
         */

        // adapter 초기화
        saveCateAdapter = SaveCateAdapter(this, categoryList, binding5.cateSaveBtn)
        // 리스트뷰에 방금 생성한 adapter를 붙여서 화면에 연결해준다.
        cateList.adapter = saveCateAdapter

        val docTitle = binding.docTitle

        // write_qustion_item.xml에서 view들 가져오기
        val qIcon = binding2.qIcon
        //val aIcon = binding2.aIcon
        var qTitle = binding2.qTitle
        var aTxt = binding2.aTxt
        //setContentView(R.layout.write_question_item);
        //qTitle = findViewById(R.id.qTitle);
        //val qTitleText: String = qTitle.getText().toString()
        //aTxt = findViewById(R.id.aTxt);
        //val aTxtText: String = aTxt.getText().toString()
        val addBtn = binding2.addAnswer
        val qlinkLayout = binding2.clLinkArea
        val qlinkIcon = binding2.linkIcon
        val qlinkTitle = binding2.linkTitle
        val qlinkContent = binding2.linkContent
        val qlinkUri = binding2.linkUri
        val qAddImgBtn = binding2.qImgAddBtn
        val qAddLinkBtn = binding2.qLinkAddBtn

        //write_content_item.xml에서 view들 가져오기
        var docContent = binding3.docContent
        //setContentView(R.layout.write_content_item);
        //docContent = findViewById(R.id.docContent);
        //val docContentText: String = docContent.getText().toString()

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
        writingAdapter = WriteMultiAdapter(this)
        binding.docList.adapter = writingAdapter

        //시작할 때 title과 content만 뜨도록 하기
        binding.docContent.visibility = View.GONE
        binding.contentImg.visibility = View.GONE
        binding.linkContent.visibility = View.GONE
        binding.linkUri.visibility = View.GONE
        binding.linkInsertTxt.visibility = View.GONE
        binding.linkInsertBtn.visibility = View.GONE
        binding.linkIcon.visibility = View.GONE
        binding.linkTitle.visibility = View.GONE
        binding.clLinkArea.visibility = View.GONE

        //기본 본문 박스 생성
        writeContentList.add(WriteContentData(0, null, null, null, null, null,
            null, null, null, docContent, null, null
        ))
        writingAdapter.updateList(writeContentList)
        ContentArray.add(0, sContent(docContent.text.toString(), null, null))


        var WriteID: String = ""
        if(intent.hasExtra("data"))
        {
            Log.d("태그", "${WriteID}")
            WriteID = intent.getStringExtra("data").toString()
            loadWriting(WriteID, writingAdapter)
        }

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

        var id = 1
        //하단의 '본문' 버튼 클릭 리스너
        binding.addContentBTN.setOnClickListener {



            //writeContentList에 해당 내용 넣기
            writeContentList.add(WriteContentData(count, null, null, null, null, null,
                        null, null, null, docContent, null, null
            ))

            //내용 저장 후 본문 박스 생성
            writingAdapter.updateList(writeContentList)

            //객체에 저장
            ContentArray.add(id, sContent(docContent.text.toString(), null, null))
            Log.d("과연", "${docContent.text.toString()}")

            id++
            count++

        }

        var id2 = 0
        //하단의 '링크' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            // 본문에 링크 생성

            //writeContentList에 해당 내용 넣기
            writeContentList.add(WriteContentData(count, null, clinkInsertTxt, clinkInsertBtn, null, null,
                null, null, null, null, null, null
            ))

            //내용 저장 후 본문 박스 생성
            writingAdapter.updateList(writeContentList)

            //객체에 저장
            ContentArray.add(id2, sContent(docContent.text.toString(), null, null))

            id2++
            count++
        }

        //하단의 '사진' 버튼 클릭 리스너
        binding.addImgBtn.setOnClickListener {
            //setContentView(R.layout.activity_writing)

            //viewPager = findViewById(R.id.viewPager)

            //권한이 허용되어있는지 self로 체크(확인)
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED) {
                //허용되지 않았을 때 - 권한이 필요한 알림창을 올림
                //이전에 거부한 적이 있는지 확인
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    var dlg = AlertDialog.Builder(this)
                    dlg.setTitle("권한이 필요한 이유")
                    dlg.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다")
                    //OK버튼
                    dlg.setPositiveButton("확인") { dialog, which ->
                        ActivityCompat.requestPermissions(this@WritingActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    dlg.setNegativeButton("취소", null)
                    dlg.show()
                } else {
                    //권한 요청
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
                }
            }else{
                openGalleryForImage()
            }
        }


        var id3 = 0
        //하단의 '질문' 버튼 클릭 리스너
        binding.addQBtn.setOnClickListener {



            //writeContentList에 해당 내용 넣기
            writeQuestionList.add(WriteQuestionData(id3, qTitle, null, null, null, null, null, null, null,aTxt,
                null, null, null
            ))

            //내용 저장 후 본문 박스 생성
            writingAdapter.updateList(writeQuestionList)

            //객체에 저장
            QuestionArray.add(id3, sQuestion(qTitle.text.toString()))
            AnswerArray.add(id3, sAnswer(aTxt.text.toString(), today, null, null))

            id3++

        }

        //저장 활성화
       binding.saveBtn.setEnabled(true)

        // 저장 버튼 클릭 리스너
        binding.saveBtn.setOnClickListener {


            // 제목, 본문, 사진, 링크, 질문, 답변 객체에 따로 저장
            // 제목 객체 저장
            writingAdapter.updateList(writeQuestionList)
            writingAdapter.updateList(writeContentList)

            sWriting(docTitle.text.toString(), today, "기본")

            ContentArray.add(writeContentList.size-1, sContent(writeContentList[(writeContentList.size-1)].docContent?.text.toString(), writeContentList[(writeContentList.size-1)].contentImg, writeContentList[(writeContentList.size-1)].linkUri))
            QuestionArray.add(writeQuestionList.size-1, sQuestion(writeQuestionList[(writeQuestionList.size-1)].qTitle?.text.toString()))
            AnswerArray.add(writeQuestionList.size-1, sAnswer(writeQuestionList[(writeContentList.size-1)].aTxt?.text.toString(), today, writeQuestionList[(writeContentList.size-1)].aImg, writeQuestionList[(writeContentList.size-1)].linkUri))

            // 카테고리 저장 팝업업
            val mBuilder = AlertDialog.Builder(this, R.style.CateSaveDialogTheme).setView(binding5.root)

            // view의 중복 사용을 방지하기 위한 코드
            if (binding5.root.parent != null)
                (binding5.root.parent as ViewGroup).removeView(binding5.root)

            val mAlertDialog = mBuilder.show()

            // 확인 버튼 다이얼로그
            binding5.cateSaveBtn.setOnClickListener {
                var t1 = Toast.makeText(this, "여기다가 카테고리 저장 받아오삼유", Toast.LENGTH_SHORT)
                t1.show()
            }


//           // Long 클릭 팝업 띄울 때 이 코드 가져다가 활용해서 쓰면 됨
//            // 선택 목록
//            val selectList = arrayOf("변경", "삭제")
//            var selectDialog =
//                AlertDialog.Builder(this, R.style.LongClickPopUp)
//
//            selectDialog
//                .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->
//
//                    // 변경 버튼을 클릭했을 때
//                    if(which == 0){
//                        var t1 = Toast.makeText(this, "변경 버튼 클릭", Toast.LENGTH_SHORT)
//                        t1.show()
//                    }
//                    // 삭제 버튼을 클릭했을 때
//                    else if(which == 1){
//                        var t1 = Toast.makeText(this, "삭제 버튼 클릭", Toast.LENGTH_SHORT)
//                        t1.show()
//                    }
//                }
//                ).show()
        }

    }

    private fun loadWriting(WriteID: String, writingAdapter: WriteMultiAdapter)
    {
        binding2 = WriteQuestionItemBinding.inflate(layoutInflater)
        val q_linkLayout = binding2.clLinkArea
        val c_linkLayout = binding3.clLinkArea
        var isrun:Boolean = false
//맨처음 본문-질문에 띄울 내용 불러오기.(multi adapter 사용X)
        var WritingArray: ArrayList<Content> = db.getWriting("${WriteID}")
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
        var QuestionIDArray: ArrayList<Question> = db.getQuestionID(WritingArray[0].WriteID, WritingArray[0].ContentID.toString())
        var QuestionIDSize = QuestionIDArray.size
        for(i in 0..QuestionIDSize-1)
        {
            //Question에 해당하는 대답 객체 리스트 받아오기
            var AnswerArray: ArrayList<Answer> = db.getAnswer(QuestionIDArray[i].QuestionID)
            var AnswerSize = AnswerArray.size
            var LastSize = AnswerSize-1
            if(AnswerSize==1)
            {
                //답변이 한 개일 경우.
                loadQuestionList.add(loadQuestionData(0, QuestionIDArray[i].Content,AnswerArray[0].Image,q_linkLayout,null,AnswerArray[0].Link,null,
                    null,AnswerArray[0].Content, AnswerArray[0].Date, false))

                writingAdapter.updateList(loadQuestionList)

            } else if(AnswerSize>1)
            {
                //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                //답변의 갯수 만큼 반복문 - 첫번째
                for(j in 0..AnswerSize-2)
                {
                    if(j==0)
                    {
                        loadQuestionList.add(loadQuestionData(0, QuestionIDArray[i].Content,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                            null,AnswerArray[j].Content, AnswerArray[j].Date, true))

                        writingAdapter.updateList(loadQuestionList)

                        writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                    }else{

                        loadQuestionList.add(loadQuestionData(0, null,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                            null,AnswerArray[j].Content, AnswerArray[j].Date, true))

                        writingAdapter.updateList(loadQuestionList)

                        writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                    }
                }
                //마지막 내용!

                loadQuestionList.add(loadQuestionData(0, null,AnswerArray[LastSize].Image,q_linkLayout,null,AnswerArray[LastSize].Link,null,
                    null,AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))

                writingAdapter.updateList(loadQuestionList)

            }else{
                Log.d("태그", "${QuestionIDArray[i].Content}")
                //질문만 있고, 대답 없는 경우.
                loadQuestionList.add(loadQuestionData(0, QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                    null,null, null, false))

                writingAdapter.updateList(loadQuestionList)

            }
        }



        //맨 처음 내용을 출력한 후 그다음 부터 본문 Content 덩이를 출력함.
        for(i in 1..WritingSize-1)
        {
            // 본문 추가
            loadContentList.add(loadContentData(0, WritingArray[i].Image,c_linkLayout,null,null,WritingArray[i].link,
                null,null,WritingArray[i].content))
            writingAdapter.updateList(loadContentList)

            //한 글 내용에 들어가 있는 질문 객체 리스트 구하기. 1-1), 1-2)번 질문의 ID
            var QuestionIDArray: ArrayList<Question> = db.getQuestionID(WritingArray[i].WriteID, WritingArray[i].ContentID.toString())
            var QuestionIDSize = QuestionIDArray.size

            for(i in 0..QuestionIDSize-1)
            {
                //Question에 해당하는 대답 객체 리스트 받아오기
                var AnswerArray: ArrayList<Answer> = db.getAnswer(QuestionIDArray[i].QuestionID)
                var AnswerSize = AnswerArray.size
                var LastSize = AnswerSize-1
                if(AnswerSize==1)
                {
                    //답변이 한 개일 경우.
                    loadQuestionList.add(loadQuestionData(0, QuestionIDArray[i].Content,AnswerArray[0].Image,q_linkLayout,null,AnswerArray[0].Link,null,
                        null,AnswerArray[0].Content, AnswerArray[0].Date, false))
                    writingAdapter.updateList(loadQuestionList)
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
                            loadQuestionList.add(loadQuestionData(0, QuestionIDArray[i].Content,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                            writingAdapter.updateList(loadQuestionList)
                            writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                        }else{
                            loadQuestionList.add(loadQuestionData(0, null,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                            writingAdapter.updateList(loadQuestionList)
                            writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                        }
                    }
                    //마지막 내용!
                    loadQuestionList.add(loadQuestionData(0, null,AnswerArray[LastSize].Image,q_linkLayout,null,AnswerArray[LastSize].Link,null,
                        null,AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
                    writingAdapter.updateList(loadQuestionList)
                }else{
                    //질문만 있고, 대답 없는 경우.
                    loadQuestionList.add(loadQuestionData(0, QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                        null,null, null, false))
                    writingAdapter.updateList(loadQuestionList)

                }
            }

        }

        binding.docList.adapter = writingAdapter

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

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
                    this@WritingActivity.runOnUiThread(java.lang.Runnable {
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

    val REQUEST_TAKE_ALBUM = 2
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        Log.d("태그", "openGalleryForImage 들어옴.")
        this.startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_TAKE_ALBUM)
        Log.d("태그", "이미지 선택 완료")
    }

    var id4 = 0
    // onActivityResult 로 이미지 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            2 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_ALBUM) {
                    if (data != null) {
                        var photo:InputStream? = contentResolver.openInputStream(data.getData()!!)
                        val img = BitmapFactory.decodeStream(photo)
                        if (photo != null) {
                            photo.close()
                        }


                        writeContentList.add(WriteContentData(count, img, null, null, null, null,
                            null, null, null, null, null, null
                        ))

                        //내용 저장 후 본문 박스 생성
                        writingAdapter.updateList(writeContentList)

                        //객체에 저장
                        ContentArray.add(id4, sContent(null, img, null))


                        id4++
                        count++
                    }

                }
            }
        }
    }

}

