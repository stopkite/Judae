package com.example.backbone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
//import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class WritingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWritingBinding

    private lateinit var binding2:WriteQuestionItemBinding
    private lateinit var binding3:WriteContentItemBinding

    private lateinit var binding4:CancelWritingBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {

        //DBHelper와 이어주도록 클래스 선언
        var db: DBHelper = DBHelper(this)

        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding2 = WriteQuestionItemBinding.inflate(layoutInflater)
        binding3 = WriteContentItemBinding.inflate(layoutInflater)

        binding4 = CancelWritingBinding.inflate(layoutInflater)


        val writeQuestionList = ArrayList<WriteQuestionData>()
        val writeContentList = ArrayList<WriteContentData>()

        val saveQuestionList = ArrayList<saveQuestionData>()
        val saveContentList = ArrayList<saveContentData>()

        class qSave(qTitle: String, aImg: Drawable?,
                    linkLayout: View?, linkTitle:String, linkUri:String, linkIcon: Drawable?,
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

        class cSave(contentImg: Drawable?,
                    linkLayout: View?, linkTitle:String, linkContent:String, linkUri:String, linkIcon: Drawable?,
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
        class sAnswer(content: String, date: String, image: Drawable?, link: String){
            var content = content
            var date = date
            var image = image
            var link = link
        }
        var AnswerArray: Array<sAnswer>? = null

        //content 객체
        class sContent (content: String, image: Drawable?, link: String) {
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
        val aIcon = binding2.aIcon
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
        writingAdapter = WriteMultiAdapter(this, saveQuestionList, saveContentList)
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

            var count = 0
            if (writeContentList.size > 0){
                contentsave.add(count, cSave(contentImg.drawable,
                clinkLayout, clinkTitle.toString(), clinkContent.toString(), clinkUri.toString(), clinkIcon.drawable,
                docContent.toString()))
                count++
            }
            //본문 박스 생성
            writingAdapter.addItems(
                WriteContentData(
                    null, null, null, null, null, null,
                    null, null, docContent
                )
            )

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            //writingAdapter.notifyDataSetChanged()
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
                    writingAdapter.addItems(
                        WriteContentData(contentImg.drawable, null, null, null, null, null,
                            null, null, null
                        )
                    )

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
                            qlinkIcon.drawable, aTxt.toString()))
                Log.d("되나?","${questionsave[count].qTitle}")
                count++


            }

            // 질문 추가
            writingAdapter.addItems(
                WriteQuestionData(
                    qTitle, null, null, null, null, null,
                    null, null, aTxt, null
                )
            )

            writeQuestionList.add(WriteQuestionData(qTitle, null, null, null, null, null, null, null,aTxt,
                null
            ))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            //writingAdapter.notifyDataSetChanged()

        }

        //저장 활성화
        binding.editBtn.setEnabled(true)

        // 저장 버튼 클릭 리스너
        //binding.saveBtn.setOnClickListener {
        binding.editBtn.setOnClickListener {

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
        }

    }

}
