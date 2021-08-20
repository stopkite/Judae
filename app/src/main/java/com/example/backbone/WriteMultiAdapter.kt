package com.example.backbone

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.*
import com.google.android.material.internal.ContextUtils.getActivity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.UnknownHostException


private var isrun:Boolean = false

class WriteMultiAdapter(writingActivity: WritingActivity,contxt:Context): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
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
                return MyQHolder(binding)
            }
            TYPE_Content -> {
                //MyContentHolder.create(parent)
                binding2 = WriteContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyContentHolder(binding2)
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
                        after: Int
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
                        Log.d("태그", "afterTextChanged")
                        QuestionList.Date = activity.today
                        //updateQuestions에 저장해주기.
                        //updateQuestionItems(QuestionList, position)
                        //Log.d("태그", "afterTextChanged ${QuestionList.id}: ${QuestionList.aTxt}")
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
                        after: Int
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
                            Log.d("count", "${activity.countDT}")
                            Log.d("count", "${activity.countDC}")
                            Log.d("count", "${activity.countQT}")
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
                    holder.binding.linkInsertTxt.visibility = View.VISIBLE
                    holder.binding.linkInsertBtn.visibility = View.VISIBLE
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
                        after: Int
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
                    holder.loadLink(linkUri, QuestionList, context)
                    holder.binding.qLinkAddBtn.setClickable(false)
                    binding.qLinkAddBtn.setImageResource(R.drawable.ic_write_add_link_done)
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
                                holder.binding.qLinkAddBtn.setClickable(true)
                                holder.binding.qLinkAddBtn.imageTintList =
                                    ColorStateList.valueOf(Color.WHITE)
                                QuestionList.linkUri = null
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
                                holder.binding.qImgAddBtn.setClickable(true)
                                holder.binding.qImgAddBtn.imageTintList =
                                    ColorStateList.valueOf(Color.WHITE)
                                QuestionList.aImg = null
                            }
                        }
                        ).show()
                    true
                }
                holder.binding.clLinkArea.setOnClickListener {
                    Log.d("태그", "${holder.binding.linkTitle.text}")
                    if(holder.binding.linkTitle.text != "404Error")
                    {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${QuestionList.linkUri}"))
                        holder.binding.root.context.startActivity(intent)
                    }else{
                        Toast.makeText(context, "         유효하지 않은 링크입니다. \n" +
                                "            링크를 수정해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            is MyContentHolder -> {
                //holder.setContentList(items[position] as WriteContentData)
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
                        after: Int
                    ) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding2.docContent.isFocusable() && !s.toString().equals(preTxt)) {
                            /*
                                                        Log.d("태그", "초기화 되었는지 확인: ${afterTxt}")
                            Log.d("태그", "아이템 아이디: ${WriteList.id}")
                            Log.d("태그", "${s.toString()}")
                            Log.d("태그", "질문 수정중")
                             */

                            try {
                                //afterTxt = binding.aTxt.getText().toString()
                                //items[position].
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
                        after: Int
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
                    holder.loadLink(linkUri2, WriteList, context)
                    holder.binding2.linkInsertTxt.setText("")
                }

                //링크 롱클릭 리스너 (변경, 삭제) //뭔가 이상함 ㅎㅎ;;
                holder.binding2.clLinkArea.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if(which == 0){
                                holder.binding2.clLinkArea.visibility = View.GONE
                                //removeItems(position)
                                holder.binding2.linkInsertBtn.visibility = View.VISIBLE
                                holder.binding2.linkInsertTxt.visibility = View.VISIBLE
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if(which == 1){
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
                    Log.d("태그", "${holder.binding2.linkTitle.text}")
                    if(holder.binding2.linkTitle.text != "404Error")
                    {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${WriteList.linkUri}"))
                        holder.binding2.root.context.startActivity(intent)
                    }else{
                        Toast.makeText(context, "         유효하지 않은 링크입니다. \n" +
                                "            링크를 수정해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
    // 질문 Holder
    class MyQHolder(val binding: WriteQuestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setQList(item: WriteQuestionData) {

            val REQUEST_READ_EXTERNAL_STORAGE = 1000
            var context = binding.aImg.context

            if(item.qTitle == null){
                //binding.qTitle.visibility = View.GONE
            }else{
                binding.qTitle.setText(item.qTitle)
            }


            if(item.aImg == null)
            {
                binding.aImg.visibility = View.GONE
            }else{
                // 대답 삽입 이미지
                binding.aImg.setImageBitmap(item.aImg)
                binding.qImgAddBtn.setImageResource(R.drawable.ic_write_add_img_done)
            }

            // 링크
            if(item.linkLayout == null){
                binding.clLinkArea.visibility = View.GONE
            } else {
                binding.qLinkAddBtn.setImageResource(R.drawable.ic_write_add_link_done)
            }

            // 링크 삽입이 이뤄지는 곳(editText영역)
            if(item.linkInsertTxt == null){
                binding.linkInsertTxt.visibility = View.GONE
            }

            // 링크 삽입이 이뤄지는 곳(버튼 영역)
            if(item.linkInsertTxt == null){
                binding.linkInsertBtn.visibility = View.GONE
            }

            // 링크된 요소들
            binding.linkTitle.setText(item.linkTitle)
            binding.linkUri.setText(item.linkUri)
           // binding.linkIcon.setImageDrawable(item.linkIcon.dr)

            // 대답
            binding.aTxt.setText(item.aTxt)

            // 대답 추가 버튼
            if(item.addAnswer == null){
                binding.addAnswer.visibility = View.GONE
            }else {
                binding.addAnswer.setImageDrawable(item.addAnswer?.drawable)
            }

            //이미지 추가 버튼
            if(item.qImgAddBtn == null){
                binding.qImgAddBtn.visibility = View.GONE
            }else {
                binding.qImgAddBtn.setImageDrawable(item.qImgAddBtn?.drawable)
            }
            //링크
            if(item.qLinkAddBtn == null){
                binding.qLinkAddBtn.visibility = View.GONE
            }else {
                binding.qLinkAddBtn.setImageDrawable(item.qLinkAddBtn?.drawable)
            }

            // 링크
            if(item.linkUri == ""||item.linkUri == null){
                //링크 내용이 없으면?
                binding.clLinkArea.visibility = View.GONE
            }else{
                // 링크 정보는 있는데. 두번째로 불러온 정보일 때 -> 첫번째 정보에서 이미 받아온 링크 내용, 이미지 등 정보가 있을 때
                if(item.linkContent != null || item.linkTitle != null)
                {
                    binding.clLinkArea.visibility = View.VISIBLE
                    binding.linkTitle.text = item.linkTitle.toString()
                    binding.linkContent.text = item.linkContent.toString()
                    binding.linkUri.text = item.linkUri.toString()
                    if(item.linkIcon != null)
                    {
                        binding.linkIcon.setImageBitmap(item.linkIcon)
                    }else{
                        binding.linkIcon.visibility = View.GONE
                    }
                    //링크 내용이 있으면?
                    //binding.clLinkArea.visibility = item.linkLayout?.visibility!!
                }else{
                    // 링크 정보를 불러오는 것이 처음 일때!
                    binding.clLinkArea.visibility = View.VISIBLE
                    loadLink(item.linkUri.toString(), item, WritingActivity())
                }
            }

            if(item.Date != null)
            {
                var date: String? = item.Date
                var text:String = item.aTxt + "\n${date}"
                var start = text.indexOf(date!!)
                var end = start + date!!.length
                val spannableString = SpannableString(text)
                spannableString.setSpan(ForegroundColorSpan(Color.GRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(RelativeSizeSpan(0.8f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.aTxt.setText(spannableString)
            }
        }


        fun setLink(linkUri: String, title: String, content: String, bm1: Bitmap?)
        {
            try{
                if(bm1 == null)
                {
                    binding.linkIcon.visibility = View.GONE
                }
                binding.linkUri.text = linkUri
                binding.linkTitle.text = title
                binding.linkContent.text = content
                binding.linkIcon.setImageBitmap(bm1)
            }catch(e:Exception)
            {
                if(bm1 == null)
                {
                    binding.linkIcon.visibility = View.GONE
                }
                binding.linkUri.text = linkUri
                binding.linkTitle.text = title
                binding.linkContent.text = content
                binding.linkIcon.setImageBitmap(bm1)
            }
        }

        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content:String = ""

        fun loadLink(linkUri: String, item: WriteQuestionData, context:Context) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            var linkUri = linkUri
            title = ""
            bm1 = null
            url1 = null
            content = ""
            isrun = true

            Thread(Runnable {
                while (isrun) {//네이버의 경우에만 해당되는 것 같아.
                    try {
                        if (linkUri.contains("naver")) {
                            if (!linkUri.contains("https://")) {
                                linkUri = "https://${linkUri}"
                            }
                            try{
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

                                if(title == "")
                                {
                                    throw UnknownHostException()
                                }

                                url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                if (bm1 == null) {
                                    binding.linkIcon.visibility = View.GONE
                                }

                                Log.d("태그", "제목: ${title}")
                                bis.close()
                                item.linkUri = linkUri
                                item.linkTitle = title
                                item.linkContent = content
                                item.linkIcon = bm1

                                if(title != "")
                                {
                                    setLink(linkUri, title, content, bm1)
                                }else{
                                    isrun = false
                                }


                            }catch (e: UnknownHostException)
                            {
                                Handler(Looper.getMainLooper()).post { Toast.makeText(context, "                 유효하지 않은 링크입니다. \n" +
                                        " 입력을 원한다면 하이퍼링크를 이용해주세요.", Toast.LENGTH_SHORT).show() }
                                isrun = false
                                item.linkUri = ""
                                setLink("","404Error", "유효하지 않은 링크입니다.", null)
                            }

                        } else {
                            if (!linkUri.contains("https://")) {
                                linkUri = "https://${linkUri}"
                            }
                            var doc:Document
                            try{
                               doc = Jsoup.connect("${linkUri}").get()

                                Log.d("태그", "Document로 불러오나?")
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
                                    binding.linkIcon.visibility = View.GONE
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
                                if(content=="")
                                {
                                    content = "${title}를 이용하실 수 있습니다."
                                }
                                if(title == "")
                                {
                                    throw UnknownHostException()

                                }

                                item.linkUri = linkUri
                                item.linkTitle = title
                                item.linkContent = content
                                item.linkIcon = bm1

                                 if(item.linkUri != "")
                                {
                                    setLink(item.linkUri!!, item.linkTitle!!, item.linkContent!!, item.linkIcon)
                                }


                                isrun = false
                            }catch (e: UnknownHostException)
                            {
                                Handler(Looper.getMainLooper()).post { Toast.makeText(context, "                 유효하지 않은 링크입니다. \n" +
                                        " 입력을 원한다면 하이퍼링크를 이용해주세요.", Toast.LENGTH_SHORT).show() }
                                isrun = false
                                item.linkUri = ""
                                setLink("","404Error", "유효하지 않은 링크입니다.", null)
                            }

                        }
                    } catch (e: Exception) {

                    }
                }
            }).start()
        }
    }

    // 본문 Hodler
    class MyContentHolder(val binding2: WriteContentItemBinding) : RecyclerView.ViewHolder(binding2.root) {

        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content:String = ""

        fun setContentList(item: WriteContentData) {

            Log.d("태그", "MyContentHolder")

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
                    loadLink(item.linkUri.toString(), item, context = WritingActivity())
                }
            }

            // 링크된 요소들
            /*binding2.linkTitle.text = item.linkTitle
            binding2.linkUri.text = item.linkUri
            binding2.linkIcon.setImageDrawable(item.linkIcon)*/

            // 본문내용(텍스트)
            if(item.docContent == null){
                binding2.docContent.visibility = View.GONE
            }else{
                binding2.docContent.setText(item.docContent)
            }

        }

        fun setLink(linkUri: String, title: String, content: String, bm1: Bitmap?)
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
            }catch(e:Exception)
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
        }




        fun loadLink(linkUri: String, item: WriteContentData, context:Context) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            var linkUri = linkUri
            title = ""
            bm1 = null
            url1 = null
            content = ""
            isrun = true

            Thread(Runnable {
                while (isrun) {//네이버의 경우에만 해당되는 것 같아.
                    Log.d("태그", "링크 스레드 실행 중")
                    try {
                        if (linkUri.contains("naver")) {
                            if (!linkUri.contains("https://")) {
                                linkUri = "https://${linkUri}"
                            }
                            try{
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
                                if (bm1 == null) {
                                    binding2.linkIcon.visibility = View.GONE
                                }

                                if(title == "")
                                {
                                    throw UnknownHostException()

                                }

                                bis.close()
                                item.linkUri = linkUri
                                item.linkTitle = title
                                item.linkContent = content
                                item.linkIcon = bm1

                                if(title != "")
                                {
                                    setLink(linkUri, title, content, bm1)
                                }

                                isrun = false
                            }catch (e: UnknownHostException)
                            {
                                Handler(Looper.getMainLooper()).post { Toast.makeText(context, "                 유효하지 않은 링크입니다. \n" +
                                        " 입력을 원한다면 하이퍼링크를 이용해주세요.", Toast.LENGTH_SHORT).show() }
                                isrun = false
                                item.linkInsertTxt = null
                                item.linkUri = ""
                                setLink("","404Error", "유효하지 않은 링크입니다.", null)
                            }

                        } else {
                            if (!linkUri.contains("https://")) {
                                linkUri = "https://${linkUri}"
                            }
                            var doc:Document
                            try{
                                doc = Jsoup.connect("${linkUri}").get()

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
                                    binding2.linkIcon.visibility = View.GONE
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
                                    binding2.linkIcon.visibility = View.GONE
                                }
                                if(content=="")
                                {
                                    content = "${title}를 이용하실 수 있습니다."
                                }

                                if(title == "")
                                {
                                    throw UnknownHostException()

                                }

                                item.linkUri = linkUri
                                item.linkTitle = title
                                item.linkContent = content
                                item.linkIcon = bm1

                                if(item.linkUri != "")
                                {
                                    setLink(item.linkUri!!, item.linkTitle!!, item.linkContent!!, item.linkIcon)
                                }
                                isrun = false
                            }catch (e: UnknownHostException)
                            {
                                Handler(Looper.getMainLooper()).post { Toast.makeText(context, "                 유효하지 않은 링크입니다. \n" +
                                        " 입력을 원한다면 하이퍼링크를 이용해주세요.", Toast.LENGTH_SHORT).show() }
                                isrun = false
                                item.linkInsertTxt = null
                                item.linkUri = ""
                                setLink("","404Error", "유효하지 않은 링크입니다.", null)
                            }

                        }
                    } catch (e: Exception) {

                    }
                }
            }).start()
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

    fun updateItems(item: WriteItem, position: Int)
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


    /*
        fun updateLoadQuestionItem(item: WriteItem)
    {
        //var activity:WritingActivity = WritingActivity()
        var WriteList = item as ReadQuestionData
        activity.writeContentList[WriteList.id].docContent = WriteList.docContent
        activity.writeContentList[WriteList.id].linkUri = WriteList.linkUri
    }
     */

    fun addItems(item: WriteItem) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }
    fun removeItems(position: Int) {
        this.items.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyDataSetChanged()

    }

    fun AddAnswer(item: WriteItem, position: Int) {
        //선택한 대답 바로 밑에 내용 추가.
        this.items.add(position+1, item)

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

    class ContentDiffUtil(private val oldList: List<WriteItem>, private val currentList: List<WriteItem>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = currentList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == currentList[newItemPosition].id

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldList = oldList[oldItemPosition]
            val currentList = currentList[newItemPosition]

            return oldList == currentList
        }

    }

    fun updateList(items: List<WriteItem>?) {
        items?.let {
            val diffCallback = ContentDiffUtil(this.items, items)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.items.run {
                clear()
                addAll(items)
                diffResult.dispatchUpdatesTo(this@WriteMultiAdapter)
            }
        }
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
            0->{
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
                        binding.qImgAddBtn.setImageResource(R.drawable.ic_write_add_img_done)
                        binding.qImgAddBtn.setImageResource(R.drawable.ic_write_add_img_done)
                        notifyDataSetChanged()
                        Log.d("태그", "순서가 어케 되는 겨?onActivityResult 함수 안")

                    }
                }
            }
        }
    }

}