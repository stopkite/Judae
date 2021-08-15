package com.example.backbone

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.icu.lang.UCharacter.IndicPositionalCategory.RIGHT
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedInputStream
import java.net.URL
import java.net.URLConnection
import java.net.UnknownHostException


private var isrun:Boolean = false
class ReadMultiAdapter(contxt: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private lateinit var binding: ReadQuestionItemBinding
    private lateinit var binding2: ReadContentItemBinding
    private var items = mutableListOf<ReadItem>()

    var context:Context = contxt

    companion object {
        private const val TYPE_Question = 0
        private const val TYPE_Content = 1
    }
    fun setData(data : ArrayList<ReadItem>){
        this.items = data
        notifyDataSetChanged()
    }
    override fun getItemViewType(position: Int) = when (items[position]) {
        is ReadQuestionData -> {
            TYPE_Question
        }
        is ReadContentData -> {
            TYPE_Content
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)  : RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            TYPE_Question -> {
                MyQHolder.create(parent)
            }
            TYPE_Content -> {
                binding2 = ReadContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyContentHolder(binding2, context)
            }
            else -> {
                throw IllegalStateException("Not Found ViewHolder Type $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyQHolder -> {
                holder.setQList(items[position] as ReadQuestionData)
                holder.setIsRecyclable(false)

                //선택된 아이템에 대한 정보 빼내오기
                var ReadList = items[position] as ReadQuestionData

                holder.binding.clLinkArea.setOnClickListener {
                    if(binding.linkTitle.text != "404Error")
                    {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${ReadList.linkUri}"))
                        binding.root.context.startActivity(intent)
                    }else{
                        Toast.makeText(context, "         유효하지 않은 링크입니다. \n 편집을 이용해 링크를 수정해주세요,", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            is MyContentHolder -> {
                holder.setContentList(items[position] as ReadContentData)
                holder.setIsRecyclable(false)

                //선택된 아이템에 대한 정보 빼내오기
                var ReadList = items[position] as ReadContentData

                holder.binding2.clLinkArea.setOnClickListener {
                    if(binding2.linkTitle.text != "404Error")
                    {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${ReadList.linkUri}"))
                        binding2.root.context.startActivity(intent)
                    }else{
                        Toast.makeText(context, "         유효하지 않은 링크입니다. \n 편집을 이용해 링크를 수정해주세요,", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // 질문 Holder
    class MyQHolder(val binding: ReadQuestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setQList(item: ReadQuestionData) {
            // 질문 제목
            if(item.qTitle == ""|| item.qTitle == null){
                binding.qIcon.visibility = View.GONE
                //질문 부분 삭제하는 코드
                binding.qTitle.visibility = View.GONE
            }else{
                binding.qTitle.setText(item.qTitle)
            }

            if(item.aImg != null)
            {
                Log.d("태그", "${item.qTitle}, ${item.aImg}")
                // 삽입 이미지
                binding.aImg.visibility = View.VISIBLE
                binding.aImg.setImageBitmap(item.aImg)
            }else{
                binding.aImg.visibility = View.GONE
            }

            // 링크
            if(item.linkUri != ""&&item.linkUri != null && item.linkUri != "null"){
                //링크 내용이 없으면?
                loadLink(item.linkUri.toString(), context = ReadingActivity())
            }else{
                //링크 내용이 있으면?
                //binding.clLinkArea.visibility = item.linkLayout?.visibility!!
                binding.clLinkArea.visibility = View.GONE
            }

            // 대답 내용 삽입
            if(item.aTxt != ""&&item.aTxt!=null)
            {
                // 대답 상태에 따라 색 바꿔줌.
                if(item.ColorChanged == true)
                {
                    var date: String? = item.Date
                    var text:String = item.aTxt + "\n${date}"
                    var start = text.indexOf(date!!)
                    var end = start + date!!.length
                    val spannableString = SpannableString(text)
                    spannableString.setSpan(ForegroundColorSpan(Color.GRAY),0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(RelativeSizeSpan(0.8f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.aTxt.setText(spannableString)
                }else{
                    var date: String? = item.Date
                    var text:String = item.aTxt + "\n${date}"
                    var start = text.indexOf(date!!)
                    var end = start + date!!.length
                    val spannableString = SpannableString(text)
                    spannableString.setSpan(ForegroundColorSpan(Color.GRAY),start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(RelativeSizeSpan(0.8f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.aTxt.setText(spannableString)
                }
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
        var content: String = ""

        fun loadLink(linkUri: String, context:Context) {
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
                                url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                if (bm1 == null) {
                                    binding.linkIcon.visibility = View.GONE
                                }

                                bis.close()

                                if(title != "")
                                {
                                    setLink(linkUri, title, content, bm1)
                                }

                                isrun = false
                            }catch (e: UnknownHostException)
                            {
                                isrun = false
                                setLink("","404Error", "유효하지 않은 링크입니다.", null)
                            }

                        } else {
                            if (!linkUri.contains("https://")) {
                                linkUri = "https://${linkUri}"
                            }
                            var doc: Document
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

                                if(title != "")
                                {
                                    setLink(linkUri, title, content, bm1)
                                }


                                isrun = false
                            }catch (e: UnknownHostException)
                            {
                                isrun = false
                                setLink("","404Error", "유효하지 않은 링크입니다.", null)
                            }

                        }
                    } catch (e: Exception) {

                    }
                }
            }).start()
        }



    companion object Factory {
            fun create(parent: ViewGroup): MyQHolder {
                val binding = ReadQuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyQHolder(binding)
            }
        }
    }

    // 본문 Hodler
    class MyContentHolder(val binding2: ReadContentItemBinding, context: Context) : RecyclerView.ViewHolder(binding2.root) {
        fun setContentList(item: ReadContentData) {
            //사진 띄우기 **** - 나중에 하기.
            if(item.contentImg != null)
            {
                binding2.contentImg.setImageBitmap(item.contentImg)
            }else{
                binding2.contentImg.visibility = View.GONE
            }

            //본문내용(텍스트)
            if(item.docContent=="" || item.docContent == "null")
            {
                binding2.docContent.visibility = View.GONE
            }else{
                binding2.docContent.text = item.docContent
            }

            if(item.linkUri != ""){
                binding2.clLinkArea.visibility = View.VISIBLE
                loadLink(item.linkUri.toString(), context = ReadingActivity())
            }else{
                binding2.clLinkArea.visibility = View.GONE
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

        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content:String = ""

        fun loadLink(linkUri: String, context: Context) {
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
                                url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                if (bm1 == null) {
                                    binding2.linkIcon.visibility = View.GONE
                                }

                                Log.d("태그", "제목: ${title}")
                                bis.close()

                                if(title != "")
                                {
                                    setLink(linkUri, title, content, bm1)
                                }

                                isrun = false
                            }catch (e: UnknownHostException)
                            {
                                isrun = false
                                setLink("","404Error", "유효하지 않은 링크입니다.", null)
                            }

                        } else {
                            if (!linkUri.contains("https://")) {
                                linkUri = "https://${linkUri}"
                            }
                            var doc: Document
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

                                if(title != "")
                                {
                                    setLink(linkUri, title, content, bm1)
                                }

                                isrun = false
                            }catch (e: UnknownHostException)
                            {
                                isrun = false
                                setLink("","404Error", "유효하지 않은 링크입니다.", null)
                            }

                        }
                    } catch (e: Exception) {

                    }
                }
            }).start()
        }

        /*
                companion object Factory {
            fun create(parent: ViewGroup): MyContentHolder {
                val binding2 = ReadContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyContentHolder(binding2, context)
            }
        }
         */

    }


    override fun getItemCount() = items.size

    fun addItems(item: ReadItem) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }
}