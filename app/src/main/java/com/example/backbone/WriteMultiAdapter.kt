package com.example.backbone

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.UnknownHostException


private var isrun:Boolean = false

class WriteMultiAdapter(writingActivity: WritingActivity, contxt: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private lateinit var binding:WriteQuestionItemBinding
    private lateinit var binding2:WriteContentItemBinding
    private lateinit var binding3:ActivityWritingBinding
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    var context:Context = contxt
    var activity = writingActivity
    val items = mutableListOf<WriteItem>()

    companion object {
        private const val TYPE_Question = 0
        private const val TYPE_Content = 1
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is WriteQuestionData -> {
            TYPE_Question
        }
        is WriteContentData -> {
            TYPE_Content
        }

        else -> {
            throw IllegalStateException("Not Found ViewHolder Type")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            TYPE_Question -> {
                binding = WriteQuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                binding3 = ActivityWritingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyQHolder(binding, activity)
            }
            TYPE_Content -> {
                binding2 = WriteContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                binding3 = ActivityWritingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyContentHolder(binding2, activity)
            }
            else -> {
                throw IllegalStateException("Not Found ViewHolder Type $viewType")
            }
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //실행될 때: 버튼 누를때마다. 본문/질문 이런 거.
        when (holder) {
            is MyQHolder -> {
                (holder as MyQHolder).setQList(items[position] as WriteQuestionData)
                holder.setIsRecyclable(false)

                //선택된 아이템에 대한 정보 빼내오기
                var QuestionList = items[position] as WriteQuestionData
                //holder.setQList(items[position] as WriteQuestionData)

                //답변 추가될 때 리스너
                holder.binding.aTxt.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null

                    //val thisitem= item
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding.aTxt.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                afterTxt = binding.aTxt.getText().toString()
                                //items[position].
                                QuestionList.aTxt = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        QuestionList.Date = activity.today
                    }
                })

                //질문 입력됐을 때 리스너
                holder.binding.qTitle.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null

                    //val thisitem= item
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding.qTitle.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                afterTxt = binding.qTitle.getText().toString()
                                //items[position].
                                QuestionList.qTitle = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        QuestionList.qTitle = s.toString()
                        //updateQuestionItems(QuestionList, position)
                        if (s.length > 0) {
                            activity.countQT = 1
                            if (activity.countDT == 1 && activity.countDC == 1 && activity.countQT == 1) {
                                activity.setEnabledTrue()
                            } else {
                                activity.setEnabledFalse()
                            }
                        } else {
                            activity.countQT = 0
                            if (activity.countDT == 1 && activity.countDC == 1 && activity.countQT == 1) {
                                activity.setEnabledTrue()
                            } else {
                                activity.setEnabledFalse()
                            }
                        }
                    }
                })

                //답변 링크 입력 버튼 눌렀을 때!
                holder.binding.qLinkAddBtn.setOnClickListener {
                    //링크가 없을 때만 실행되도록
                    if (QuestionList.linkUri == "" ) {
                        holder.binding.linkInsertTxt.visibility = View.VISIBLE
                        holder.binding.linkInsertBtn.visibility = View.VISIBLE
                    }
                }

                //답변 링크 입력됐을 때 리스너
                holder.binding.linkInsertTxt.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null

                    //val thisitem= item
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding.linkInsertTxt.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                afterTxt = binding.linkInsertTxt.getText().toString()
                                QuestionList.linkUri = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        QuestionList.linkUri = s.toString()
                        //updateQuestionItems(QuestionList, position)
                    }

                })

                //링크 입력 후 확인을 누르면 실행되는 리스너
                holder.binding.linkInsertBtn.setOnClickListener {
                    holder.binding.linkInsertBtn.visibility = View.GONE
                    holder.binding.linkInsertTxt.visibility = View.GONE
                    holder.binding.clLinkArea.visibility = View.VISIBLE
                    //입력 받은 링크를 String으로 넣어 준 후
                    var linkUri = QuestionList.linkUri.toString()
                    //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
                    //쓰레드 실행(한번만 실행함.)
                    activity.QuestionloadLink(linkUri, QuestionList, context)
                    holder.binding.linkInsertTxt.setText("")

                }

                //링크 롱클릭 리스너 (변경, 삭제)
                holder.binding.clLinkArea.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if (which == 0) {
                                holder.binding.clLinkArea.visibility = View.GONE
                                holder.binding.linkInsertBtn.visibility = View.VISIBLE
                                holder.binding.linkInsertTxt.visibility = View.VISIBLE
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if (which == 1) {
                                holder.binding.clLinkArea.visibility = View.GONE
                                holder.binding.qLinkAddBtn.setImageResource(R.drawable.ic_write_add_link)
                                QuestionList.linkUri = ""
                            }
                        }
                        ).show()
                    true
                }


                //답변 사진 입력 버튼 눌렀을 때!
                holder.binding.qImgAddBtn.setOnClickListener {
                    //binding.aImg.visibility = View.VISIBLE
                    //권한이 허용되어있는지 self로 체크(확인)
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        //허용되지 않았을 때 - 권한이 필요한 알림창을 올림 )
                        //이전에 거부한 적이 있는지 확인
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                activity,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            var dlg = AlertDialog.Builder(context)
                            dlg.setTitle("권한이 필요한 이유")
                            dlg.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다")
                            //OK버튼
                            dlg.setPositiveButton("확인") { dialog, which ->
                                ActivityCompat.requestPermissions(
                                    activity,
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                    REQUEST_READ_EXTERNAL_STORAGE
                                )
                            }
                            dlg.setNegativeButton("취소", null)
                            dlg.show()
                        } else {
                            //권한 요청
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_READ_EXTERNAL_STORAGE
                            )
                        }
                    } else {
                        //이미지가 없을 때만 실행되도록
                        if (QuestionList.aImg == null) {
                            openGalleryForImage(QuestionList)
                        }

                    }
                }

                //사진 롱클릭 리스너 (변경, 삭제)
                holder.binding.aImg.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if (which == 0) {
                                openGalleryForImage(QuestionList)
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if (which == 1) {
                                holder.binding.aImg.visibility = View.GONE
                                holder.binding.qImgAddBtn.setImageResource(R.drawable.ic_write_add_img)
                                QuestionList.aImg = null
                            }
                        }
                        ).show()
                    true
                }
                holder.binding.clLinkArea.setOnClickListener {
                    if (holder.binding.linkTitle.text != "404Error") {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${QuestionList.linkUri}"))
                        holder.binding.root.context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "         유효하지 않은 링크입니다. \n" +
                                "            링크를 수정해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            is MyContentHolder -> {
                (holder as MyContentHolder).setContentList(items[position] as WriteContentData)
                holder.setIsRecyclable(false)

                //선택된 아이템에 대한 정보 빼내오기
                var WriteList = items[position] as WriteContentData
                //holder.setQList(items[position] as WriteQuestionData)

                holder.binding2.docContent.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null

                    //val thisitem= item
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding2.docContent.isFocusable() && !s.toString().equals(preTxt)) {


                            try {
                                WriteList.docContent = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        updateItems(WriteList, position)

                    }
                })
                holder.binding2.linkInsertTxt.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null

                    //val thisitem= item
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding2.linkInsertTxt.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                afterTxt = binding2.linkInsertTxt.getText().toString()
                                WriteList.linkUri = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        WriteList.linkUri = s.toString()
                        updateItems(WriteList, position)
                    }
                })
                //링크 입력 후 확인을 누르면 실행되는 리스너
                holder.binding2.linkInsertBtn.setOnClickListener {
                    holder.binding2.linkInsertBtn.visibility = View.GONE
                    holder.binding2.linkInsertTxt.visibility = View.GONE
                    holder.binding2.clLinkArea.visibility = View.VISIBLE
                    //입력 받은 링크를 String으로 넣어 준 후
                    var linkUri2 = WriteList.linkUri.toString()
                    //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
                    //쓰레드 실행(한번만 실행함.)
                    holder.binding2.linkInsertTxt.setText("")
                    binding3.docContent.requestFocus(View.FOCUS_DOWN)
                    //어답터 연결하기
                    binding3.docList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    var adapter = ReadMultiAdapter(context)
                    binding3.docList.adapter = adapter
                    activity.adapterContentloadLink(linkUri2, WriteList, context)
                    activity.hideKeyboard()
                }

                //링크 롱클릭 리스너 (변경, 삭제)
                holder.binding2.clLinkArea.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if (which == 0) {
                                holder.binding2.clLinkArea.visibility = View.GONE
                                //removeItems(position)
                                holder.binding2.linkInsertBtn.visibility = View.VISIBLE
                                holder.binding2.linkInsertTxt.visibility = View.VISIBLE
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if (which == 1) {
                                holder.binding2.clLinkArea.visibility = View.GONE
                                removeItems(position)
                            }
                        }
                        ).show()
                    true
                }

                //사진 롱클릭 리스너 (변경, 삭제)
                holder.binding2.contentImg.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if (which == 0) {
                                EditGalleryImage(WriteList)
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if (which == 1) {
                                holder.binding2.contentImg.visibility = View.GONE
                                removeItems(position)
                            }
                        }
                        ).show()
                    true
                }

                holder.binding2.clLinkArea.setOnClickListener {
                    if (holder.binding2.linkTitle.text != "404Error") {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${WriteList.linkUri}"))
                        holder.binding2.root.context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "         유효하지 않은 링크입니다. \n" +
                                "            링크를 수정해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
    // 질문 Holder
    class MyQHolder(val binding: WriteQuestionItemBinding, var activity: WritingActivity) : RecyclerView.ViewHolder(binding.root) {
        fun setQList(item: WriteQuestionData) {
            Log.d("태그", "${item.aTxt}")
            val REQUEST_READ_EXTERNAL_STORAGE = 1000
            var context = binding.aImg.context

            // 질문 제목
            if(item.qTitle == null){
            binding.qTitle.visibility = View.GONE
            }else{
            binding.qTitle.setText(item.qTitle)
            }

            binding.aImg.visibility = View.VISIBLE

            //이미지
            if (item.aImg != null) {
                //삽입 이미지
                binding.aImg.setImageBitmap(item.aImg)
                binding.qImgAddBtn.setImageResource(R.drawable.ic_write_add_img_done)
            } else {
                binding.aImg.visibility = View.GONE
            }

            binding.clLinkArea.visibility = View.GONE
            binding.linkInsertTxt.visibility = View.GONE
            binding.linkInsertBtn.visibility = View.GONE
            binding.addAnswer.visibility = View.GONE

            // 링크
            if (item.linkUri == "") {
                //링크 내용이 없으면?
                binding.clLinkArea.visibility = View.GONE
            } else {
                binding.qLinkAddBtn.setImageResource(R.drawable.ic_write_add_link_done)
                binding.clLinkArea.visibility = View.VISIBLE
                binding.linkTitle.text = item.linkTitle.toString()
                binding.linkContent.text = item.linkContent.toString()
                binding.linkUri.text = item.linkUri.toString()
                if (item.linkIcon != null) {
                    binding.linkIcon.setImageBitmap(item.linkIcon)
                } else {
                    binding.linkIcon.visibility = View.GONE
                }
            }

            // 대답 내용 삽입
            if (item.aTxt != "" && item.aTxt != null) {
                binding.aTxt.setText( item.aTxt.toString() )
                binding.addAnswer.visibility = View.GONE

            }
        }
    }

    // 본문 Hodler
    class MyContentHolder(val binding2: WriteContentItemBinding, var activity: WritingActivity) : RecyclerView.ViewHolder(binding2.root) {

        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content:String = ""

        fun setContentList(item: WriteContentData) {

            if(item.id == activity.currentContentID)
            {
                binding2.docContent.requestFocus(View.FOCUS_DOWN)
            }

            if(item.contentImg == null)
            {
                binding2.contentImg.visibility = View.GONE
            }else{
                // 본문 삽입 이미지
                binding2.contentImg.setImageBitmap(item.contentImg)
            }


            binding2.clLinkArea.visibility = View.GONE
            binding2.linkInsertBtn.visibility = View.GONE
            binding2.linkInsertTxt.visibility = View.GONE
            // 링크
            if(item.linkUri == ""||item.linkUri == null){
                if(item.linkInsertTxt != null && item.linkInsertBtn != null)
                {
                    binding2.linkInsertBtn.visibility = View.VISIBLE
                    binding2.linkInsertTxt.visibility = View.VISIBLE
                }else{
                    //링크 내용이 없으면?
                    binding2.clLinkArea.visibility = View.GONE
                    binding2.linkInsertBtn.visibility = View.GONE
                    binding2.linkInsertTxt.visibility = View.GONE
                }
            }else{
                // 링크 정보는 있는데. 두번째로 불러온 정보일 때 -> 첫번째 정보에서 이미 받아온 링크 내용, 이미지 등 정보가 있을 때
                if(item.linkContent != null || item.linkTitle != null)
                {
                    binding2.clLinkArea.visibility = View.VISIBLE
                    binding2.linkTitle.text = item.linkTitle.toString()
                    binding2.linkContent.text = item.linkContent.toString()
                    binding2.linkUri.text = item.linkUri.toString()
                    if(item.linkIcon != null)
                    {
                        binding2.linkIcon.setImageBitmap(item.linkIcon)
                    }else{
                        binding2.linkIcon.visibility = View.GONE
                    }
                }else{
                    // 링크 정보를 불러오는 것이 처음 일때!
                    binding2.clLinkArea.visibility = View.VISIBLE
                    activity.adapterContentloadLink(item.linkUri.toString(), item, context = WritingActivity())
                }
            }

            // 본문내용(텍스트)
            if(item.docContent == null){
                binding2.docContent.visibility = View.GONE
            }else{
                binding2.docContent.setText(item.docContent)
            }

        }

        fun setLink(linkUri: String, title: String, content: String, bm1: Bitmap?, item: WriteContentData)
        {
            try{
                if(bm1 == null)
                {
                    binding2.linkIcon.visibility = View.GONE
                }
                binding2.linkUri.text = linkUri
                binding2.linkTitle.text = title
                binding2.linkContent.text = content
                binding2.linkIcon.setImageBitmap(bm1)
            }catch (e: Exception)
            {
                if(bm1 == null)
                {
                    binding2.linkIcon.visibility = View.GONE
                }
                binding2.linkUri.text = linkUri
                binding2.linkTitle.text = title
                binding2.linkContent.text = content
                binding2.linkIcon.setImageBitmap(bm1)
            }
            updateItems(item)
        }

        private fun updateItems(item: WriteContentData) {
            var WriteList = item as WriteContentData
            activity.writeContentList[WriteList.id].docContent = WriteList.docContent
            activity.writeContentList[WriteList.id].linkUri = WriteList.linkUri
        }

    }

    /*
본문 제목: docTitle
본문 이미지: titleImg

<링크 삽입을 위한 요소들>
텍스트입력 받는 곳: linkInsertTxt
확인 버튼: linkInsertBtn

<링크된 영역>
링크영역 항목들을 감싸는 레이아웃 = linkLayout
제목 = linkTitle
내용 = linkContent
uri = linkUri
아이콘 = linkIcon
썸네일 = linkImg

본문 내용: docContent

 */

    override fun getItemCount() = items.size

    fun updateItems(item: WriteItem, position:Int)
    {
        //var activity:WritingActivity = WritingActivity()
        var WriteList = item as WriteContentData
        activity.writeContentList[WriteList.id].docContent = WriteList.docContent
        activity.writeContentList[WriteList.id].linkUri = WriteList.linkUri
    }

    fun updateQuestionItems(item: WriteItem, position: Int)
    {
        //var activity:WritingActivity = WritingActivity()
        var QList = item as WriteQuestionData
        activity.writeQuestionList[QList.id].qTitle = QList.qTitle
        activity.writeQuestionList[QList.id].linkUri = QList.linkUri
        activity.writeQuestionList[QList.id].Date = QList.Date
        activity.writeQuestionList[QList.id].aImg = QList.aImg
        activity.writeQuestionList[QList.id].aTxt = QList.aTxt
    }


    fun addItems(item: WriteItem) {
        this.items.add(getItemCount(), item)
        var itemsize = getItemCount(); // 배열 사이즈 다시 확인
        this.notifyDataSetChanged()
        this.notifyItemInserted(getItemCount())
    }

    //아이템 삭제 함수
    fun removeItems(position: Int) {
        this.items.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyDataSetChanged()

    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int)
    }

    //를릭 리스너
    private lateinit var itemClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    private val REQUEST_TAKE_ALBUM = 1
    var itemInfo: WriteQuestionData? = null
    private fun openGalleryForImage(item: WriteQuestionData) {
        itemInfo = item
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_TAKE_ALBUM)
    }

    private val REQUEST_TAKE_PHOTO = 0
    var ContentItem: WriteContentData? = null
    private fun EditGalleryImage(item: WriteContentData) {
        ContentItem = item
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_TAKE_PHOTO)
    }


    @Override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode){
            0 -> {
                // 본문에서 사진 변경할 때 사용
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
                    if (data != null) {
                        var photo: InputStream? = activity.contentResolver.openInputStream(data.getData()!!)
                        val img = BitmapFactory.decodeStream(photo)
                        if (photo != null) {
                            photo.close()
                        }
                        this.ContentItem?.contentImg = img
                        this.notifyDataSetChanged()
                    }
                }
            }
            1 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_ALBUM) {
                    if (data != null) {
                        var photo: InputStream? = activity.contentResolver.openInputStream(data.getData()!!)
                        val img = BitmapFactory.decodeStream(photo)
                        if (photo != null) {
                            photo.close()
                        }
                        this.itemInfo?.aImg = img
                        binding.aImg.visibility = View.VISIBLE
                        binding.aImg.setImageBitmap(img)
                        this.notifyDataSetChanged()
                        notifyDataSetChanged()

                    }
                }
            }
        }
    }

}