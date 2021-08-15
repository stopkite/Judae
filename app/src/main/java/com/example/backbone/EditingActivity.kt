package com.example.backbone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.*
import com.google.android.material.internal.ContextUtils.getActivity
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditingBinding
    private lateinit var binding2:EditQuestionItemBinding
    private lateinit var binding3:EditContentItemBinding
    private lateinit var binding4:CancelWritingBinding
    // 카테고리 저장 화면
    private lateinit var binding5:ActivitySavingBinding
    private lateinit var binding6:SaveCategoryItemBinding

    // 리스트뷰에 붙일 adapter 변수 생성
    private lateinit var saveCateAdapter: SaveCateAdapter

    //DBHelper와 이어주도록 클래스 선언
    var db: DBHelper = DBHelper(this)

    private lateinit var WritingArray: ArrayList<Content>
    // 리사이클러뷰에 붙일 어댑터 선언
    private lateinit var writingAdapter:EditMultiAdapter

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    // 링크 삽입 관련 메소드
    var linkUri: String = ""
    var linktitle: String = ""
    var bm1: Bitmap? = null
    var url1: URL? = null
    var content:String = ""
    private var isrun:Boolean = false

    //날짜
    @RequiresApi(Build.VERSION_CODES.O)
    var now = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    var localDateTime: LocalDate = LocalDate.parse(now.toString())
    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    @RequiresApi(Build.VERSION_CODES.O)
    var today = formatter.format(localDateTime)

    // 본문으로 추가된 질문에 대한 배열 정보를 가진 질문 리스트
    val writeQuestionList = ArrayList<EditloadQuestionData>()
    val writeContentList = ArrayList<EditloadContentData>()
    // DB에서 로드 되는 데이터 중 수정된 데이터를 담는 리스트
    val writeUpdateList = ArrayList<Int>()
    // DB에서 로드 되는 데이터 중 삭제된 데이터를 담는 리스트
    val writeDeleteList = ArrayList<Int>()

    // 로드된 내용의 마지막 ContentID를 받아오는 메소드
    var currentContentID: Int = -1
    // 로드된 내용 이후 추가 되는 본문 내용 아이템의 위치를 받는 메소드
    var FirstAddItemPos: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 본문과 질문(대답) reyclerview 요소가 담긴 layout
        binding2 = EditQuestionItemBinding.inflate(layoutInflater)
        binding3 = EditContentItemBinding.inflate(layoutInflater)

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


        val docTitle = binding.docTitle
        var qTitle = binding2.qTitle
        val qTitleText: String = qTitle.getText().toString()
        var aTxt = binding2.aTxt
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

        // 글쓰기 취소 버튼 눌렀을 때 뜨는 팝업
        binding.cancelButton.setOnClickListener {
            //변경된 부분 없으면 바로 finish 해주기
                if (writeQuestionList == null && writeContentList == null) {
                    finish()
                } else {
                    val mBuilder =
                        AlertDialog.Builder(this, R.style.MyDialogTheme).setView(binding4.root)

                    // view의 중복 사용을 방지하기 위한 코드
                    if (binding4.root.parent != null)
                        (binding4.root.parent as ViewGroup).removeView(binding4.root)

                    val mAlertDialog = mBuilder.show()

                    // 확인 버튼 다이얼로그
                    binding4.confirmBtn.setOnClickListener {
                        // 이전 화면으로 이동
                        finish()
                    }

                    //취소 버튼 다이얼로그
                    binding4.cancelBtn.setOnClickListener {
                        mAlertDialog.dismiss()
                    }
                }

        }


        //삭제 클릭 리스너
        /*
        // 해당 글에 속하는 ContentID를 받아옴.
        var WritingArray: ArrayList<Content> = db.getWriting("${WriteID}")


         for (i in 0..WritingArray - 1) {
                db.deleteContent(WritingArray[i].ContentID.toString())

                 //Content에 해당하는 QuestionID를 받아옴.
                var QuestionIDArray: ArrayList<Question> = db.getQuestionID(WritingArray[i].WriteID, WritingArray[i].ContentID.toString())

                         for (i in 0..QuestionIDSize - 1) {
                // 해당하는 Question&Answer를 데베에서 삭제
                db.deleteQuestion(QuestionIDArray[i].QuestionID)
         }
         }

         // 모든 내용들이 다 삭제되었으면, 글 삭제.

         db.deleteWriting(WriteID.toString())


         */

        //어댑터 연결
        writingAdapter = EditMultiAdapter(this, this)
        binding.docList.adapter = writingAdapter

        var WriteID: String = ""
        if(intent.hasExtra("data"))
        {
            WriteID = intent.getStringExtra("data").toString()

            loadWriting(WriteID, writingAdapter)
        }

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

        //하단의 '본문' 버튼 클릭 리스너
        binding.addContentBTN.setOnClickListener {
            var checknull = true
            for (i in 0..writeContentList.size-1){
                if(writeContentList[i].docContent == null&&writeContentList[i].linkUri == null&&writeContentList[i].contentImg == null)
                {
                    checknull =  false
                }
            }

            //위에 본문 입력이 안 된 것이 있으면 본문 추가가 되지 않음.
            if(!checknull)
            {
                //내용을 다 입력해달라는 본문 메시지를 입력함.
                Toast.makeText(this, "본문 내용을 모두 입력해주세요.", Toast.LENGTH_LONG).show()
            }else{
                var id = writeContentList.size
                currentContentID = currentContentID + 1
                writeContentList.add(EditloadContentData(id, currentContentID, null, null, null, null, null,
                        null, null, null, "", null, null, false
                ))

                writingAdapter.addItems(
                        EditloadContentData(
                                id, currentContentID, null,null, null, null, null, null,
                                null, null, "",null,null, false
                        )
                )
            }
        }

        //하단의 '링크' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            //작성 버전
            var id = writeContentList.size
            currentContentID = currentContentID + 1
            writeContentList.add(EditloadContentData(id, currentContentID,null, clinkInsertTxt, clinkInsertBtn, clinkLayout, null,
                    null, null, null, null, null, null, false
            ))

            writingAdapter.addItems(
                    EditloadContentData(
                            id, currentContentID,null, clinkInsertTxt, clinkInsertBtn, clinkLayout, null, null,
                            null, null, null, null, null, false
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
                        ActivityCompat.requestPermissions(this@EditingActivity,
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
            //답변이 한 개일 경우.
            writingAdapter.addItems(EditloadQuestionData(currentContentID, "", null,null,clinkLayout,null,null,null,
                    null,null,addBtn, qAddImgBtn,qAddLinkBtn, today, false, true, false))
        }



        var countDT:Int = 0
        var countDC:Int = 0
        var countQT:Int = 0

        // 저장 버튼 클릭 리스너
        binding.saveBtn.setOnClickListener {
            // 카테고리 저장 팝업업
            val mBuilder = AlertDialog.Builder(this, R.style.CateSaveDialogTheme).setView(binding5.root)

            var cateIndex = db.getCategoryIndex(WriteID)
            // 저장되어있는 카테고리로 카테고리 어댑터 라디오 버튼 미리 설정해주기.
            saveCateAdapter.setPosition(cateIndex)

            // view의 중복 사용을 방지하기 위한 코드
            if (binding5.root.parent != null)
                (binding5.root.parent as ViewGroup).removeView(binding5.root)

            val mAlertDialog = mBuilder.show()

            //본문 내용 저장을 위해 필요한 변수
            var image: ByteArray? = null
            var contentID: Int = -1

            // 저장 -> 확인 버튼 다이얼로그
            binding5.cateSaveBtn.setOnClickListener {
                var index = saveCateAdapter.selectedPosition
                var category = ""
                var title = ""
                if(cateIndex != index)
                {
                    category = categoryList[index]
                    db.updateWriting(WriteID, "카테고리", category)
                }
                if(WritingArray[0].WritingTitle != binding.docTitle.text.toString())
                {
                    title = binding.docTitle.text.toString()
                    db.updateWriting(WriteID, "제목", title)
                }
                db.updateWriting(WriteID, "날짜", today)


                for(i in 0 until writingAdapter.itemCount)
                {
                    //질문/응답 하는 부분이라면?
                    if(writingAdapter.getItemViewType(i) == 0){
                        var question:Question = Question()
                        var questionID = -1
                        var answer:Answer = Answer()

                        var data = writingAdapter.items[i] as EditloadQuestionData

                        Log.d("태그", "저장 전 내용 확인: ${data.aTxt}")
                        Log.d("태그", "저장 전 내용 확인 사진: ${data.aImg}")

                        // 새로 추가된 질문과 답 관련
                        if(!data.isloadData!!){
                            // 본문에서 아예 새로 추가된 질문일때 
                            if(data.QuestionID == "")
                            {
                                if(data.qTitle != null && data.qTitle != "")
                                {
                                    question = Question(WriteID, data.id.toString(), data.qTitle!!)
                                }else{
                                    question = Question(WriteID, data.id.toString())
                                }
                                db.InsertQuestion(question)
                                questionID = db.getCurrentQuestionID()

                                if(data.aImg != null)
                                {
                                    image = drawableToByteArray(data.aImg!!)
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
                                Log.d("태그", "저장 전 내용 확인: ${data.aTxt}")
                                Log.d("태그", "저장 전 내용 확인 사진: ${data.aImg}")
                                // 답변을 추가하는 거라면!
                                if(data.aImg != null)
                                {
                                    image = drawableToByteArray(data.aImg!!)
                                }else{
                                    image = null
                                }
                                //대답 저장
                                if(data.aTxt != null && data.aTxt != "")
                                {
                                    answer = Answer(data.QuestionID, data.aTxt, data.Date, image, data.linkUri)

                                }else{
                                    answer = Answer(data.QuestionID, null, data.Date, image, data.linkUri)
                                }
                                db.InsertAnswer(answer)
                            }
                        }
                    }else if(writingAdapter.getItemViewType(i) == 1){
                        //본문 부분이라면?
                        var data = writingAdapter.items[i] as EditloadContentData

                        // 새로 추가된 본문
                        // 본문 추가 DB 관리!
                        if(!data.loadData!!) {
                            // DB에 새롭게 추가해주기!
                            if (data.contentImg != null) {
                                image = drawableToByteArray(data.contentImg!!)
                            } else {
                                image = null
                            }

                            var content = Content(
                                    WriteID,
                                    data.docContent,
                                    image,
                                    data.linkUri)
                            db.InsertContent(content)
                            try {
                                contentID = db.getCurrentContentID()
                            } catch (e: Exception) {
                                //데이터(사진) 크기가 너무 큰 경우 발생하는 익셉션
                                contentID++
                            }
                        }
                    }
                }

                // 기존에 있던 item의 본문 수정 DB 관리
                // 기존에 있던 데이터 중 수정된 데이터의 WriteID를 가진 친구들의 중복을 제거한 후 Update해주기.
                for(i in 0 .. writeUpdateList.distinct().size -1)
                {
                    // DB에 새롭게 추가해주기!
                    if (writeContentList[writeUpdateList.distinct()[i]].contentImg != null) {
                        image = drawableToByteArray(writeContentList[writeUpdateList.distinct()[i]].contentImg!!)
                    } else {
                        image = null
                    }
                    var content = Content(
                            WriteID,
                            writeContentList[writeUpdateList.distinct()[i]].contentID,
                            writeContentList[writeUpdateList.distinct()[i]].docContent,
                            image,
                            writeContentList[writeUpdateList.distinct()[i]].linkUri
                    )

                    //DB에 업데이트 해주기.
                    db.EditContent(content)
                }

                // 기존에 있던 본문 삭제에 대한 DB 관리
                for(i in 0 .. writeDeleteList.distinct().size -1)
                {
                    //삭제하려고 지정한 본문 ID를 불러옴.
                   var ContentID = writeDeleteList.distinct()[i]

                    //DB에 업데이트 해주기.
                    db.deleteContent(ContentID)
                }

                var t1 = Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT)
                t1.show()


                /*var readingActivity: ReadingActivity? = ReadingActivity()
                // 이전 화면 읽기 화면으로 넘어가기.
                if (readingActivity != null) {
                    readingActivity.refresh(WriteID)
                }
                */

                finish()

            }
        }



        binding.docContent.addTextChangedListener(object : TextWatcher {
            var preTxt: String? = null
            var afterTxt: String? = null
            //val thisitem= item
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                preTxt = s.toString()
            }

            //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
            //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
            //before: 삭제된 기존 문자열의 개수
            //count: 새로 추가된 문자열의 개수
            override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                if (binding.docContent.isFocusable() && !s.toString().equals(preTxt)) {
                    try {
                        writeContentList[0].docContent = s.toString()
                        writeUpdateList.add(0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun drawableToByteArray(contentImg: Bitmap): ByteArray? {
        //val bitmapDrawable = drawable
        val bitmap = contentImg
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()

        return byteArray
    }


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

    private fun loadWriting(WriteID: String, writingAdapter: EditMultiAdapter)
    {
        binding2 = EditQuestionItemBinding.inflate(layoutInflater)
        val q_linkLayout = binding2.clLinkArea
        val c_linkLayout = binding3.clLinkArea
        var isrun:Boolean = false


        //맨처음 본문-질문에 띄울 내용 불러오기.(multi adapter 사용X)
        WritingArray = db.getWriting(WriteID)
        binding.docTitle.setText(WritingArray[0].WritingTitle)
        binding.docContent.setText(WritingArray[0].content)
        val addBtn = binding2.addAnswer
        val qlinkLayout = binding2.clLinkArea
        val qlinkIcon = binding2.linkIcon
        val qlinkTitle = binding2.linkTitle
        val qlinkContent = binding2.linkContent
        val qlinkUri = binding2.linkUri
        val qAddImgBtn = binding2.qImgAddBtn
        val qAddLinkBtn = binding2.qLinkAddBtn
        val contentImg = binding3.contentImg
        val clinkLayout = binding3.clLinkArea
        val clinkIcon = binding3.linkIcon
        val clinkTitle = binding3.linkTitle
        val clinkContent = binding3.linkContent
        val clinkUri = binding3.linkUri
        val clinkInsertTxt = binding3.linkInsertTxt
        val clinkInsertBtn = binding3.linkInsertBtn

        binding.clLinkArea.visibility = View.GONE
        binding.linkInsertTxt.visibility = View.GONE
        binding.linkInsertBtn.visibility = View.GONE

        if(WritingArray[0].link != "")
        {
            //쓰레드 실행(한번만 실행함.)
            loadLink(WritingArray[0].link)
        }

        //사진 띄우기 **** - 나중에 하기.
        if(WritingArray[0].Image != null)
        {
            var Image = init(WritingArray[0].Image)
            binding.contentImg.setImageBitmap(Image)
        }else{
            binding.contentImg.visibility = View.GONE
        }


        var id = writeContentList.size
        writeContentList.add(EditloadContentData(id, WritingArray[0].ContentID, null,null ,null,null,null,
                null,null,null, WritingArray[0].content, qAddImgBtn, qAddLinkBtn, true))

        var WritingSize = WritingArray.size

        currentContentID = WritingArray[WritingSize-1].ContentID

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
                var Image: Bitmap? = null
                if (AnswerArray[0].Image != null)
                {
                   Image =  init(AnswerArray[0].Image)
                }
                //답변이 한 개일 경우.
                writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, QuestionIDArray[i].Content,Image,q_linkLayout,null,null,AnswerArray[0].Link,
                        null, AnswerArray[0].Content, addBtn, qAddImgBtn,qAddLinkBtn,AnswerArray[0].Date, false, false, true))
            } else if(AnswerSize>1)
            {
                //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                //답변의 갯수 만큼 반복문 - 첫번째
                for(j in 0..AnswerSize-2)
                {
                    if(j==0)
                    {
                        var Image: Bitmap? = null
                        if(AnswerArray[j].Image != null)
                        {
                            Image = init(AnswerArray[j].Image)
                        }

                        writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, QuestionIDArray[i].Content,Image,q_linkLayout,null,null,AnswerArray[j].Link,
                                null,AnswerArray[j].Content, addBtn, qAddImgBtn,qAddLinkBtn,AnswerArray[j].Date, true, false, true))
                        writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                    }else{
                        var Image: Bitmap? = null
                        if(AnswerArray[j].Image != null)
                        {
                            Image = init(AnswerArray[j].Image)
                        }
                        writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, null,Image,q_linkLayout,null,null,AnswerArray[j].Link,
                                null,AnswerArray[j].Content,addBtn, qAddImgBtn,qAddLinkBtn, AnswerArray[j].Date, true, false, true))
                        writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                    }
                }
                //마지막 내용!
                var Image: Bitmap? = null
                if(AnswerArray[LastSize].Image != null)
                {
                    Image = init(AnswerArray[LastSize].Image)
                }
                writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, null,Image,q_linkLayout,null,null,AnswerArray[LastSize].Link,
                        null,AnswerArray[LastSize].Content, addBtn, qAddImgBtn,qAddLinkBtn,AnswerArray[LastSize].Date, false, false, true))
            }else{
                //질문만 있고, 대답 없는 경우.
                writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                        null,null,null, null, null, null,false, false, true))

            }
        }



        //맨 처음 내용을 출력한 후 그다음 부터 본문 Content 덩이를 출력함.
        for(i in 1..WritingSize-1)
        {
            // 본문 추가
                var Image:Bitmap? = null
                if(WritingArray[i].Image != null)
                {
                    Image = init(WritingArray[i].Image)
                }

            var id = writeContentList.size

            writingAdapter.addItems(EditloadContentData(id, WritingArray[i].ContentID, Image,null ,null,null,null,
                    null,WritingArray[i].link,null, WritingArray[i].content, qAddImgBtn, qAddLinkBtn, true))

            writeContentList.add(EditloadContentData(id, WritingArray[i].ContentID, Image,null ,null,null,null,
            null,WritingArray[i].link,null, WritingArray[i].content, qAddImgBtn, qAddLinkBtn, true))



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
                    var Image:Bitmap? = null
                    if(AnswerArray[0].Image != null)
                    {
                        Image = init(AnswerArray[0].Image)
                    }
                    writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, QuestionIDArray[i].Content,Image,q_linkLayout,null,null,AnswerArray[0].Link,
                            null,AnswerArray[0].Content,addBtn, qAddImgBtn,qAddLinkBtn, AnswerArray[0].Date, false, false, true))
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
                            var Image:Bitmap? = null
                            if(AnswerArray[j].Image != null)
                            {
                                Image = init(AnswerArray[j].Image)
                            }
                            writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, QuestionIDArray[i].Content,Image,q_linkLayout,null,null,AnswerArray[j].Link,
                                    null,AnswerArray[j].Content,addBtn, qAddImgBtn,qAddLinkBtn, AnswerArray[j].Date, true, false, true))
                            writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                        }else{
                            var Image:Bitmap? = null
                            if(AnswerArray[j].Image != null)
                            {
                                Image = init(AnswerArray[j].Image)
                            }
                            writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, null,Image,q_linkLayout,null,null,AnswerArray[j].Link,
                                    null,AnswerArray[j].Content,addBtn, qAddImgBtn,qAddLinkBtn, AnswerArray[j].Date, true, false, true))
                            writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                        }
                    }
                    //마지막 내용!
                    var Image:Bitmap? = null
                    if(AnswerArray[LastSize].Image != null)
                    {
                        Image = init(AnswerArray[LastSize].Image)
                    }
                    writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, null,Image,q_linkLayout,null,null,AnswerArray[LastSize].Link,
                            null,AnswerArray[LastSize].Content,addBtn, qAddImgBtn,qAddLinkBtn, AnswerArray[LastSize].Date, false, false, true))
                }else{
                    //질문만 있고, 대답 없는 경우.
                    writingAdapter.addItems(EditloadQuestionData(QuestionIDArray[i].ContentID, QuestionIDArray[i].QuestionID, QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                            null,null,addBtn, qAddImgBtn,qAddLinkBtn,null, false, false, true))

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
                    this@EditingActivity.runOnUiThread(java.lang.Runnable {
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
    private fun init(ba:ByteArray?): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(ba, 0,ba!!.size)
        return bitmap
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
                        var photo: InputStream? = contentResolver.openInputStream(data.getData()!!)
                        val img = BitmapFactory.decodeStream(photo)
                        if (photo != null) {
                            photo.close()
                        }
                        currentContentID= currentContentID + 1
                        var id = writeContentList.size
                        writeContentList.add(EditloadContentData(id,currentContentID, img, null, null, null, null,
                                null, null, null, null, null, null, false
                        ))
                        writingAdapter.addItems(
                                EditloadContentData(id,currentContentID, img, null, null, null, null, null,
                                        null, null, null, null, null, false
                                )
                        )

                    }

                }
            }
        }
    }
}