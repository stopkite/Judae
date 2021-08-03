package com.example.backbone

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
//import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
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
import androidx.viewpager.widget.ViewPager
import com.example.backbone.databinding.ActivityWritingBinding
import com.example.backbone.databinding.CancelWritingBinding
import com.example.backbone.databinding.WriteContentItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.backbone.databinding.*
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
    // 본문 화면
    private lateinit var binding:ActivityWritingBinding

    // 질문 및 대답 추가 화면
    private lateinit var binding2:WriteQuestionItemBinding
    private lateinit var binding3:WriteContentItemBinding

    // 글쓰기 취소 화면
    private lateinit var binding4:CancelWritingBinding

    // 카테고리 저장 화면
    private lateinit var binding5:ActivitySavingBinding
    // 리스트뷰에 붙일 adapter 변수 생성
    private lateinit var saveCateAdapter: SaveCateAdapter

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
    var Gallery = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        //DBHelper와 이어주도록 클래스 선언
        var db: DBHelper = DBHelper(this)

        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 본문과 질문(대답) reyclerview 요소가 담긴 layout
        binding2 = WriteQuestionItemBinding.inflate(layoutInflater)
        binding3 = WriteContentItemBinding.inflate(layoutInflater)

        // 글쓰기 취소 팝업창 요소가 담긴 레이아웃
        binding4 = CancelWritingBinding.inflate(layoutInflater)


<<<<<<< HEAD
        // 카테고리 저장 요소가 담긴 레이아웃
        binding5 = ActivitySavingBinding.inflate(layoutInflater)
        // xml에서 리스트뷰(cateList)를 가져와서 변수로 선언
        val cateList = binding5.cateList
        // CategoryList 클래스를 담는 배열 생성
        val categoryList = ArrayList<SaveCateListData>()
        // 카테고리에 들어갈 목록들 삽입(임의로 넣은 데이터)
        categoryList.add(SaveCateListData("우정"))
        categoryList.add(SaveCateListData("사랑"))
        categoryList.add(SaveCateListData("진로"))
        categoryList.add(SaveCateListData("가족"))
        categoryList.add(SaveCateListData("감정"))
        // adapter 초기화
        saveCateAdapter = SaveCateAdapter(this,categoryList)
        // 리스트뷰에 방금 생성한 adapter를 붙여서 화면에 연결해준다.
        cateList.adapter = saveCateAdapter



        var WriteID: String = ""
        if(intent.hasExtra("data"))
        {
            Log.d("태그", "${WriteID}")
            WriteID = intent.getStringExtra("data").toString()
            loadWriting(WriteID)
        }
=======
>>>>>>> b4b0f47b4458b2389a4082287957d82d4d23a2f1

        val writeQuestionList = ArrayList<WriteQuestionData>()
        val writeContentList = ArrayList<WriteContentData>()

        val saveQuestionList = ArrayList<saveQuestionData>()
        val saveContentList = ArrayList<saveContentData>()

        class qSave(qTitle: String, aImg: ByteArray?,
                    linkLayout: View?, linkTitle:String, linkUri:String, linkIcon: ByteArray?,
                    aTxt: String) {
            var qTitle = qTitle
            var aImg = aImg
            var linkLayout = linkLayout
            var linkTitle = linkTitle
            var linkUri = linkUri
            var linkIcon = linkIcon
            var aTxt = aTxt

        }
        var questionsave = ArrayList<qSave>()

        class cSave(contentImg: ByteArray?,
                    linkLayout: View?, linkTitle:String, linkContent:String, linkUri:String, linkIcon: ByteArray?,
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
        class sAnswer(content: String, date: String, image: ByteArray?, link: String){
            var content = content
            var date = date
            var image = image
            var link = link
        }
        var AnswerArray: Array<sAnswer>? = null

        //content 객체
        class sContent (content: String, image: ByteArray?, link: String) {
            var content = content
            var image = image
            var link = link
        }
        var ContentArray: Array<sContent>? = null

        //question 객체
        class sQuestion (content: String) {
            var content = content
        }
        var QuestionArray: Array<sQuestion>? = null

        //writing 객체
        class sWriting (title: String, date: String, category: String) {
            var title = title
            var date = date
            var category = category
        }


        val docTitle = binding.docTitle

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

        fun drawableToByteArray(drawable: Drawable?): ByteArray? {
            val bitmapDrawable = drawable as BitmapDrawable?
            val bitmap = bitmapDrawable?.bitmap
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
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

            /*var count = 0
            if (writeContentList.size > 0){
                contentsave.add(count, cSave(drawableToByteArray(contentImg.drawable),
                clinkLayout, clinkTitle.toString(), clinkContent.toString(), clinkUri.toString(), drawableToByteArray(clinkIcon.drawable),
                docContent.toString()))
                count++
            }*/
            //본문 박스 생성
            var id = 0
            saveContentList.add(id, saveContentData(id, null, null, null, null, null,
                null, null, null, docContent.text.toString(), null, null))
            binding3.docContent.setText("${saveContentList[id].docContent}")
            writingAdapter.addItems(
                WriteContentData(
                    id, null,null, null, null, null, null,
                    null, null, docContent,null,null
                )
            )
            //Log.d("돼라","${writeContentList}")


            writeContentList.add(WriteContentData(id, null, null, null, null, null,
                        null, null, null, docContent, null, null
            ))

            id++

        }

        //하단의 '링크' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            // 본문에 링크 생성
            var id = 0
            writingAdapter.addItems(
                WriteContentData(
                    id, null, clinkInsertTxt, clinkInsertBtn, clinkLayout, clinkTitle.text.toString(), clinkContent.text.toString(),
                    clinkUri.text.toString(), clinkIcon.drawable, null, null, null
                )
            )
            id++

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '사진' 버튼 클릭 리스너
        binding.addImgBtn.setOnClickListener {
            setContentView(R.layout.activity_writing)

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
                //권한이 이미 제대로 허용돰
                val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(intent, OPEN_GALLERY)

                var currentImageUrl : Uri? = intent?.data

                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImageUrl)
                    binding3.contentImg.setImageBitmap(bitmap)
                    var id = 0
                    writingAdapter.addItems(
                        WriteContentData(id, contentImg.drawable, null, null, null, null, null,
                            null, null, null, null, null
                        )
                    )
                    id++

                } catch(e:Exception) {
                    e.printStackTrace()
                }
            }

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }


        //하단의 '질문' 버튼 클릭 리스너
        binding.addQBtn.setOnClickListener {

            var count = 0
            if (writeQuestionList.size > 0){
                questionsave.add(count, qSave(qTitleText, null, qlinkLayout, qlinkTitle.toString(), qlinkUri.toString(),
                            drawableToByteArray(qlinkIcon.drawable), aTxt.toString()))
                Log.d("되나?","${questionsave[count].qTitle}")
                count++


            }

            var id = 0
            writeQuestionList.add(WriteQuestionData(id, qTitle, null, null, null, null, null, null, null,aTxt,
                null, null, null
            ))


            // 질문 추가
            writingAdapter.addItems(
                WriteQuestionData(
                    id, qTitle, null, null, null, null, null,
                    null, null, aTxt, null, qAddImgBtn, qAddLinkBtn
                )
            )

            id++



            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            //writingAdapter.notifyDataSetChanged()

        }

        //저장 활성화
        binding.saveBtn.setEnabled(true)

        // 저장 버튼 클릭 리스너
        binding.saveBtn.setOnClickListener {

            // 제목, 본문, 사진, 링크, 질문, 답변 객체에 따로 저장
            // 제목 객체 저장
            sWriting (docTitle.toString(), today, "기본")


            // 질문 - 답변 관련 데이터 리스트에 저장
            for (i in 0..(writeQuestionList.size - 1)) {
                if (writeQuestionList != null) {
                    QuestionArray?.set(i, sQuestion(questionsave[i].qTitle))
                }
                if (AnswerArray != null) {
                    AnswerArray.set(i, sAnswer(questionsave[i].aTxt, today, questionsave[i].aImg, questionsave[i].linkUri))
                }
            }

            for (i in 0..(writeContentList.size - 1)) {
                if (ContentArray != null) {
                    ContentArray.set(i, sContent(contentsave[i].docContent, contentsave[i].contentImg, contentsave[i].linkUri))
                }
            }

            Log.d("객체", "${ContentArray}")
            Log.d("객체", "${QuestionArray}")

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
                writingAdapter.addItems(loadQuestionData(0, QuestionIDArray[i].Content,AnswerArray[0].Image,q_linkLayout,null,AnswerArray[0].Link,null,
                        null,AnswerArray[0].Content, AnswerArray[0].Date, false))
            } else if(AnswerSize>1)
            {
                //답변의 갯수가 2개 이상일 때 -> 기존에 있던 답변에서 답변을 추가했을 경우!
                //답변수가 1개 이상이면? -> 맨 마지막 내용을 제외하고는 흰색 내용으로 띄워야 함.
                //답변의 갯수 만큼 반복문 - 첫번째
                for(j in 0..AnswerSize-2)
                {
                    if(j==0)
                    {
                        writingAdapter.addItems(loadQuestionData(0, QuestionIDArray[i].Content,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                        writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                    }else{
                        writingAdapter.addItems(loadQuestionData(0, null,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                        writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                    }
                }
                //마지막 내용!
                writingAdapter.addItems(loadQuestionData(0, null,AnswerArray[LastSize].Image,q_linkLayout,null,AnswerArray[LastSize].Link,null,
                        null,AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
            }else{
                Log.d("태그", "${QuestionIDArray[i].Content}")
                //질문만 있고, 대답 없는 경우.
                writingAdapter.addItems(loadQuestionData(0, QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                        null,null, null, false))

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
                    writingAdapter.addItems(loadQuestionData(0, QuestionIDArray[i].Content,AnswerArray[0].Image,q_linkLayout,null,AnswerArray[0].Link,null,
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
                            writingAdapter.addItems(loadQuestionData(0, QuestionIDArray[i].Content,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                    null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                            writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                        }else{
                            writingAdapter.addItems(loadQuestionData(0, null,AnswerArray[j].Image,q_linkLayout,null,AnswerArray[j].Link,null,
                                    null,AnswerArray[j].Content, AnswerArray[j].Date, true))
                            writingAdapter.notifyItemChanged(writingAdapter.itemCount, "color")
                        }
                    }
                    //마지막 내용!
                    writingAdapter.addItems(loadQuestionData(0, null,AnswerArray[LastSize].Image,q_linkLayout,null,AnswerArray[LastSize].Link,null,
                            null,AnswerArray[LastSize].Content, AnswerArray[LastSize].Date, false))
                }else{
                    //질문만 있고, 대답 없는 경우.
                    writingAdapter.addItems(loadQuestionData(0, QuestionIDArray[i].Content,null,q_linkLayout,null,null,null,
                            null,null, null, false))

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
}

