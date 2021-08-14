package com.example.backbone

import android.Manifest
import android.R.attr.button
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.*
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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

    var WriteID: String = ""

    //DBHelper와 이어주도록 클래스 선언
    var db: DBHelper = DBHelper(this)

    // 리사이클러뷰에 붙일 어댑터 선언
   // private lateinit var writingAdapter: WritingAdapter
    private lateinit var writingAdapter:WriteMultiAdapter

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    @RequiresApi(Build.VERSION_CODES.O)
    var now = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    var localDateTime: LocalDateTime = LocalDateTime.parse(now.toString())
    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH:mm")
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

    //var writeID: String = ""

    val writeQuestionList = ArrayList<WriteQuestionData>()
    val writeContentList = ArrayList<WriteContentData>()
    var id = writeContentList.size


    var countDT:Int = 0
    var countDC:Int = 0
    var countQT:Int = 0


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
        categoryList.removeAt(0)

        // adapter 초기화
        saveCateAdapter = SaveCateAdapter(this, categoryList, binding5.cateSaveBtn)
        // 리스트뷰에 방금 생성한 adapter를 붙여서 화면에 연결해준다.
        cateList.adapter = saveCateAdapter

        class qSave(
            qTitle: String, aImg: ByteArray?,
            linkLayout: View?, linkTitle: String, linkUri: String, linkIcon: ByteArray?,
            aTxt: String,
        ) {
            var qTitle = qTitle
            var aImg = aImg
            var linkLayout = linkLayout
            var linkTitle = linkTitle
            var linkUri = linkUri
            var linkIcon = linkIcon
            var aTxt = aTxt

        }
        var questionsave = ArrayList<qSave>()

        class cSave(
            contentImg: ByteArray?,
            linkLayout: View?, linkTitle: String?, linkContent: String?, linkUri:String?, linkIcon: Bitmap?,
            docContent: String) {
            var contentImg = contentImg
            var linkLayout = linkLayout
            var linkTitle = linkTitle
            var linkContent = linkContent
            var linkUri = linkUri
            var linkIcon = linkIcon
            var docContent = docContent

        }
        var contentsave = ArrayList<cSave>()

        //저장하기 위한 객체 생성
        //answer 객체
        class sAnswer(content: String?, date: String, image: Bitmap?, link: String?){
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
        class sQuestion (content: String?) {
            var content = content
        }
        var QuestionArray = ArrayList<sQuestion>()

        //writing 객체
        class sWriting (title: String, date: String, category: String) {
            var title = title
            var date = date
            var category = category
        }


        val docTitle = binding.docTitle
        val docTitleText: String = docTitle.getText().toString()

        // write_qustion_item.xml에서 view들 가져오기
        val qIcon = binding2.qIcon
        //val aIcon = binding2.aIcon
        var qTitle = binding2.qTitle
        //qTitle = findViewById(R.id.qTitle);
        val qTitleText: String = qTitle.getText().toString()

        var aTxt = binding2.aTxt
        //aTxt = findViewById(R.id.aTxt);
        val aTxtText: String = aTxt.getText().toString()

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
        //docContent = findViewById(R.id.docContent);
        val docContentText: String = docContent.getText().toString()

        val contentImg = binding3.contentImg
        val clinkLayout = binding3.clLinkArea
        val clinkIcon = binding3.linkIcon
        val clinkTitle = binding3.linkTitle
        val clinkContent = binding3.linkContent
        val clinkUri = binding3.linkUri
        val clinkInsertTxt = binding3.linkInsertTxt
        val clinkInsertBtn = binding3.linkInsertBtn


        fun drawableToByteArray(drawable: Bitmap?): ByteArray {
            //val bitmapDrawable = drawable
            val bitmap = drawable
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val byteArray = stream.toByteArray()

            return byteArray
        }


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
                finish()
            }

            //취소 버튼 다이얼로그
            binding4.cancelBtn.setOnClickListener {
                mAlertDialog.dismiss()
            }

        }

        //어댑터 연결
        writingAdapter = WriteMultiAdapter(this, this)
        binding.docList.adapter = writingAdapter


        //최초 실행 여부 판단하는 구문
        //최초 실행 여부 판단하는 구문
        val pref = getSharedPreferences("isFirst", MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", false)
        if (first == false) {
            Log.d("Is first Time?", "first")
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.commit()
            //앱 최초 실행시 하고 싶은 작업
            var id = writeQuestionList.size

            writeQuestionList.add(WriteQuestionData(id, qTitleText, null, null, null, null, null, null, null,null,
                aTxtText, null, null, null, null
            ))

            // 질문 추가
            writingAdapter.addItems(
                WriteQuestionData(
                    id, "", null, null, null, null, null,
                    null, "",null,"", null, qAddImgBtn, qAddLinkBtn, null
                )
            )
        } else {
            Log.d("Is first Time?", "not first")
        }

        //시작할 때 title과 content만 뜨도록 하기
        binding.contentImg.visibility = View.GONE
        binding.linkContent.visibility = View.GONE
        binding.linkUri.visibility = View.GONE
        binding.linkInsertTxt.visibility = View.GONE
        binding.linkInsertBtn.visibility = View.GONE
        binding.linkIcon.visibility = View.GONE
        binding.linkTitle.visibility = View.GONE
        binding.clLinkArea.visibility = View.GONE



        var WriteID: String = ""
        if(intent.hasExtra("data"))
        {
            Log.d("태그", "${WriteID}")
            WriteID = intent.getStringExtra("data").toString()
            loadWriting(WriteID, writingAdapter)
        }

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

        //하단의 '본문' 버튼 클릭 리스너
        binding.addContentBTN.setOnClickListener {
            var checknull = true
            for (i in 0..writeContentList.size-1){
                if(writeContentList[i].docContent == null&&writeContentList[i].contentImg ==null && writeContentList[i].linkUri == null)
                {
                    checknull =  false
                }
            }

            //위에 본문 입력이 안 된 것이 있으면 본문 추가가 되지 않음.
            if(!checknull)
            {

            }else{
                var id = writeContentList.size
                writeContentList.add(WriteContentData(id, null, null, null, null, null,
                    null, null, null, "", null, null
                ))

                writingAdapter.addItems(
                    WriteContentData(
                        id, null,null, null, null, null, null,
                        null, null, "",null,null
                    )
                )
            }
        }

        //하단의 '링크' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            //작성 버전
            var id = writeContentList.size
            writeContentList.add(WriteContentData(id, null, clinkInsertTxt, clinkInsertBtn, clinkLayout, null,
                null, null, null, null, null, null
            ))

            writingAdapter.addItems(
                WriteContentData(
                    id, null, clinkInsertTxt, clinkInsertBtn, clinkLayout, null, null,
                    null, null, null, null, null
                )
            )
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


        //하단의 '질문' 버튼 클릭 리스너
        binding.addQBtn.setOnClickListener {
            //수정 버전
            if(WriteID != "")
            {
                //답변이 한 개일 경우.
                writingAdapter.addItems(loadQuestionData("본문에서 추가로 넣는 거", null,null,clinkLayout,null,null,null,
                    null,null,null, today, false, true))
            }else{
                var id = writeQuestionList.size

                writeQuestionList.add(WriteQuestionData(id, qTitleText, null, null, null, null, null, null, null,null,
                    aTxtText, null, null, null, null
                ))

                // 질문 추가
                writingAdapter.addItems(
                    WriteQuestionData(
                        id, "", null, null, null, null, null,
                        null, "",null,"", null, qAddImgBtn, qAddLinkBtn, null
                    )
                )
            }

        }

        //만약 제목, 본문, 질문이 하나 이상 입력되어 있다면
        docTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                    if (editable.length > 0) {
                        countDT = 1
                        Log.d("count","${countDC}")
                        Log.d("count","${countQT}")
                        if (countDT == 1 && countDC == 1 && countQT ==1) {
                            binding.saveBtn.setEnabled(true)
                        } else {
                            binding.saveBtn.setEnabled(false)
                        }
                    } else {
                        countDT = 0
                        if (countDT == 1 && countDC == 1 && countQT ==1) {
                            binding.saveBtn.setEnabled(true)
                        } else {
                            binding.saveBtn.setEnabled(false)
                        }
                    }
                }
        })

        binding.docContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length > 0) {
                    countDC = 1
                    if (countDT == 1 && countDC == 1 && countQT ==1) {
                        binding.saveBtn.setEnabled(true)
                    } else {
                        binding.saveBtn.setEnabled(false)
                    }
                } else {
                    countDC = 0
                    if (countDT == 1 && countDC == 1 && countQT ==1) {
                        binding.saveBtn.setEnabled(true)
                    } else {
                        binding.saveBtn.setEnabled(false)
                    }
                }
            }
        })

        // 저장 버튼 클릭 리스너
        binding.saveBtn.setOnClickListener {


            // 카테고리 저장 팝업업
            val mBuilder = AlertDialog.Builder(this, R.style.CateSaveDialogTheme).setView(binding5.root)

            // view의 중복 사용을 방지하기 위한 코드
            if (binding5.root.parent != null)
                (binding5.root.parent as ViewGroup).removeView(binding5.root)

            val mAlertDialog = mBuilder.show()


            // 확인 버튼 다이얼로그
            binding5.cateSaveBtn.setOnClickListener {
                var index = saveCateAdapter.selectedPosition
                var category = categoryList[index]
                //1. 첫번째 Writing 테이블에 입력해주기 - 내용만 입력하면 됨!

                //Log로 확인하라고 일부러 주석 안지웠어요 ~
                // 제목, 본문, 사진, 링크, 질문, 답변 객체에 따로 저장
                // 제목 객체 저장 <sWriting>
                var writing = Writing(docTitle.getText().toString(), today, category)
                db.InsertWriting(writing)
                // 저장한 글 객체의 ID를 불러옴.
                writing.WriteID = db.getCurrentWriteID()
                WriteID = writing.WriteID.toString()

                var image: ByteArray? = null
                var contentID: Int = -1
                //2. 0번째 (고정 content)부분 데이터를 입력해주기. - 내용만 입력하면 됨! 내용 입력 후, getContentID 받아와서 contentID에 누적해주기.
                if(binding.contentImg.drawable != null)
                {
                    try{
                        image = drawableToByteArray(binding.contentImg.drawToBitmap())
                    }catch (e: Exception){
                        image = null
                    }
                }
                var content = Content(
                    writing.WriteID.toString(),
                    binding.docContent.text.toString(),
                    image,
                    binding.linkUri.text.toString())
                 db.InsertContent(content)

                contentID = db.getCurrentContentID()

                /*
                getItemViewType 참고하기.
                private const val TYPE_Question = 0
                private const val TYPE_Content = 1
                private const val TYPE_RContent = 2
                private const val TYPE_RCQuestion = 3
                 */
                Log.d("태그", "${writingAdapter.getItemViewType(0)}")
                Log.d("태그", "${writingAdapter.itemCount}")
                var question:Question = Question()
                var questionID = -1
                var answer:Answer = Answer()
                //3. 그 뒤에 어댑터 item의 position과 data 유형에 따라 저장하는 순서 변경해주기.
                // 맨 첫번째로 나오는 데이터 position 0이 질문/대답이면 0번째 content의 ID로 데이터 insert
                // 맨 첫번째로 나오는 데이터 position 0이 content이면 해당 content 데이터 그냥 insert
                for(i in 0..writingAdapter.itemCount-1)
                {
                    //질문/응답 하는 부분이라면?
                    if(writingAdapter.getItemViewType(i) == 0){
                        //질문 저장 - 질문 ID를 받아와 대답에도 넣어주기.
                        var data = writingAdapter.items[i] as WriteQuestionData
                        if(data.qTitle != null && data.qTitle != "")
                        {
                            question = Question(WriteID, contentID.toString(), data.qTitle!!)
                        }else{
                            question = Question(WriteID, contentID.toString())
                        }
                        db.InsertQuestion(question)
                        questionID = db.getCurrentQuestionID()


                        if(data.aImg != null)
                        {
                            image = drawableToByteArray(data.aImg)
                        }else{
                            image = null
                        }
                        //대답 저장
                        if(data.aTxt != null && data.aTxt != "")
                        {
                            answer = Answer(questionID.toString(), data.aTxt, data.Date, image, data.linkUri)

                        }else{
                            answer = Answer(questionID.toString(), null, data.Date, image, data.linkUri)
                        }
                        db.InsertAnswer(answer)
                    }else{
                        //본문 부분이라면?
                        var data = writingAdapter.items[i] as WriteContentData
                        if(data.docContent != null && data.docContent != "")
                        {

                        }

                        if(data.contentImg != null)
                        {
                            image = drawableToByteArray(data.contentImg)
                        }else{
                            image = null
                        }

                        var content = Content(
                            writing.WriteID.toString(),
                            data.docContent,
                            image,
                            data.linkUri)
                        db.InsertContent(content)
                        try{
                            contentID = db.getCurrentContentID()
                        }catch(e:Exception)
                        {
                            //데이터(사진) 크기가 너무 큰 경우 발생하는 익셉션
                            contentID ++
                        }
                    }
                }


                var t1 = Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT)
                t1.show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()

                /*
                //본문 객체 저장 <sContent>
                for (i in 0..(writeContentList.size - 1)) {
                    if (writeContentList[i].docContent != null) {
                        ContentArray.add(i, sContent(writeContentList[i].docContent, null, null))
                        //Log.d("출력","${writeContentList[i].docContent}")
                    } else if (writeContentList[i].contentImg != null) {
                        ContentArray.add(i, sContent(null, writeContentList[i].contentImg, null))
                        //Log.d("출력","${writeContentList[i].contentImg}")
                    } else {
                        ContentArray.add(i, sContent(null, null, writeContentList[i].linkUri))
                        //Log.d("출력","${writeContentList[i].linkUri}")
                    }
                }

            //질문 객체 저장 <sQuestion>
            /*for (i in 0..(writeQuestionList.size - 1)) {
                if (writeQuestionList[i].qTitle != null ) {
                    QuestionArray.add(i, sQuestion(writeQuestionList[i].qTitle))
                    Log.d("출력","${writeQuestionList[i].qTitle}")
                }
            }

                //답변 객체 저장 <sAnswer> - 아직 사진이랑 링크 추가 구현이 안되어서 null로 나옴
                for (i in 0..(writeQuestionList.size - 1)) {
                    //글, 사진, 링크 모두 있을 때
                    if (writeQuestionList[i].aTxt != null && writeQuestionList[i].aImg != null && writeQuestionList[i].linkUri != null) {
                        AnswerArray.add(i, sAnswer(writeQuestionList[i].aTxt, today, writeQuestionList[i].aImg, writeQuestionList[i].linkUri))

                        //글, 사진만 있을 때
                    } else if (writeQuestionList[i].aTxt != null && writeQuestionList[i].aImg != null && writeQuestionList[i].linkUri == null) {
                        AnswerArray.add(i, sAnswer(writeQuestionList[i].aTxt, today, writeQuestionList[i].aImg, null))

                        //글, 링크만 있을 때
                    } else if (writeQuestionList[i].aTxt != null && writeQuestionList[i].aImg == null && writeQuestionList[i].linkUri != null) {
                        AnswerArray.add(i, sAnswer(writeQuestionList[i].aTxt, today, null, writeQuestionList[i].linkUri))

                        //사진, 링크만 있을 때
                    } else if (writeQuestionList[i].aTxt == null && writeQuestionList[i].aImg != null && writeQuestionList[i].linkUri != null) {
                        AnswerArray.add(i, sAnswer(null, today, writeQuestionList[i].aImg, writeQuestionList[i].linkUri))

                        //글만 있을 때
                    } else if (writeQuestionList[i].aTxt != null && writeQuestionList[i].aImg == null && writeQuestionList[i].linkUri == null) {
                        AnswerArray.add(i, sAnswer(writeQuestionList[i].aTxt, today, null, null))
                        Log.d("출력","${writeQuestionList[i].aTxt}")

                        //사진만 있을 때
                    } else if (writeQuestionList[i].aTxt == null && writeQuestionList[i].aImg != null && writeQuestionList[i].linkUri == null) {
                        AnswerArray.add(i, sAnswer(null, today, writeQuestionList[i].aImg, null))

                        //링크만 있을 때
                    } else {
                        AnswerArray.add(i, sAnswer(null, today, null, writeQuestionList[i].linkUri))
                    }

                 */
            }
        }
            }*/


            // 카테고리 저장 팝업업
            val mBuilder = AlertDialog.Builder(this, R.style.CateSaveDialogTheme).setView(binding5.root)

            // view의 중복 사용을 방지하기 위한 코드
            if (binding5.root.parent != null)
                (binding5.root.parent as ViewGroup).removeView(binding5.root)

    }
        }

    }

    private fun loadWriting(WriteID: String, writingAdapter: WriteMultiAdapter)
    {
        Log.d("태그", "${WriteID}")
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
                writingAdapter.addItems(loadQuestionData(i.toString()+"-0", QuestionIDArray[i].Content,AnswerArray[0].Image,q_linkLayout,null,null,AnswerArray[0].Link,
                    null, null, AnswerArray[0].Content, AnswerArray[0].Date, false, false))
            } else if(AnswerSize>1)
            {
                //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                //답변의 갯수 만큼 반복문 - 첫번째
                for(j in 0..AnswerSize-2)
                {
                    if(j==0)
                    {
                        writingAdapter.addItems(loadQuestionData(i.toString()+"-${j}", QuestionIDArray[i].Content,AnswerArray[j].Image,q_linkLayout,null,null,AnswerArray[j].Link,
                            null,null,AnswerArray[j].Content, AnswerArray[j].Date, true, false))
                        writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                    }else{
                        writingAdapter.addItems(loadQuestionData(i.toString()+"-${j}", null,AnswerArray[j].Image,q_linkLayout,null,null,AnswerArray[j].Link,
                            null,null,AnswerArray[j].Content, AnswerArray[j].Date, true, false))
                        writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                    }
                }
                //마지막 내용!
                writingAdapter.addItems(loadQuestionData(i.toString()+"-last", null,AnswerArray[LastSize].Image,q_linkLayout,null,null,AnswerArray[LastSize].Link,
                    null,null,AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false, false))
            }else{
                Log.d("태그", "${QuestionIDArray[i].Content}")
                //질문만 있고, 대답 없는 경우.
                writingAdapter.addItems(loadQuestionData(i.toString(), QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                    null,null,null, null, false, false))

            }
        }



        //맨 처음 내용을 출력한 후 그다음 부터 본문 Content 덩이를 출력함.
        for(i in 1..WritingSize-1)
        {
            // 본문 추가
            writingAdapter.addItems(loadContentData(0, WritingArray[i].Image,c_linkLayout,null,null,WritingArray[i].link,
                null,null,WritingArray[i].content))

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
                    writingAdapter.addItems(loadQuestionData(i.toString(), QuestionIDArray[i].Content,AnswerArray[0].Image,q_linkLayout,null,null,AnswerArray[0].Link,
                        null,null,AnswerArray[0].Content, AnswerArray[0].Date, false, false))
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
                            writingAdapter.addItems(loadQuestionData(i.toString()+"-${j}", QuestionIDArray[i].Content,AnswerArray[j].Image,q_linkLayout,null,null,AnswerArray[j].Link,
                                null,null,AnswerArray[j].Content, AnswerArray[j].Date, true, false))
                            writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                        }else{
                            writingAdapter.addItems(loadQuestionData(i.toString()+"-${j}", null,AnswerArray[j].Image,q_linkLayout,null,null,AnswerArray[j].Link,
                                null,null,AnswerArray[j].Content, AnswerArray[j].Date, true, false))
                            writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                        }
                    }
                    //마지막 내용!
                    writingAdapter.addItems(loadQuestionData(i.toString()+"-last", null,AnswerArray[LastSize].Image,q_linkLayout,null,null,AnswerArray[LastSize].Link,
                        null,null,AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false, false))
                }else{
                    //질문만 있고, 대답 없는 경우.
                    writingAdapter.addItems(loadQuestionData(i.toString(), QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                        null,null,null, null, false, false))

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
    fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        this.startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_TAKE_ALBUM)
    }

    // onActivityResult 로 이미지 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            0->{
                // 본문에서 사진 변경할 때 사용
                writingAdapter.onActivityResult(requestCode, resultCode, data)
            }
            1->{
                writingAdapter.onActivityResult(requestCode, resultCode, data)
            }
            2 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_ALBUM) {
                    if (data != null) {
                        var photo:InputStream? = contentResolver.openInputStream(data.getData()!!)
                        val img = BitmapFactory.decodeStream(photo)
                        if (photo != null) {
                            photo.close()
                        }
                        var id = writeContentList.size
                        writeContentList.add(WriteContentData(id, img, null, null, null, null,
                            null, null, null, null, null, null
                        ))
                        writingAdapter.addItems(
                            WriteContentData(id, img, null, null, null, null, null,
                                null, null, null, null, null
                            )
                        )

                    }

                }
            }
        }
    }

    fun setEnabledTrue(){
        binding.saveBtn.setEnabled(true)
    }

    fun setEnabledFalse(){
        binding.saveBtn.setEnabled(false)
    }

}

