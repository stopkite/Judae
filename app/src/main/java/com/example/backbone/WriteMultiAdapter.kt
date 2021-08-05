package com.example.backbone

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.*
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.net.URL
import java.net.URLConnection

private var isrun:Boolean = false
class WriteMultiAdapter(context: WritingActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private lateinit var binding:WriteQuestionItemBinding
    private lateinit var binding2:WriteContentItemBinding
    private lateinit var binding3:ActivityWritingBinding

    private val items = mutableListOf<WriteItem>()

    companion object {
        private const val TYPE_Question = 0
        private const val TYPE_Content = 1
        private const val TYPE_RContent = 2
        private const val TYPE_RCQuestion = 3
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is WriteQuestionData -> {
            TYPE_Question
        }
        is WriteContentData -> {
            TYPE_Content
        }
        is loadContentData -> {
            TYPE_RContent
        }
        is loadQuestionData -> {
            TYPE_RCQuestion
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_Question -> {
            MyQHolder.create(parent)
        }
        TYPE_Content -> {
            MyContentHolder.create(parent)
        }
        TYPE_RCQuestion -> {
            LoadQHolder.create(parent)
        }
        TYPE_RContent -> {
            LoadContentHolder.create(parent)
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            //실행될 때: 버튼 누를때마다. 본문/질문 이런 거.
        when (holder) {
            is MyQHolder -> {
                holder.setQList(items[position] as WriteQuestionData)
            }
            is MyContentHolder -> {

                holder.setContentList(items[position] as WriteContentData)
            }
            is LoadQHolder -> {
                holder.setQList(items[position] as loadQuestionData)
            }
            is LoadContentHolder -> {

                holder.setContentList(items[position] as loadContentData)
            }
        }
    }

    // 질문 Holder
    class LoadQHolder(val binding: WriteQuestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setQList(item: loadQuestionData) {
            // 질문 제목
            if(item.qTitle == ""|| item.qTitle == null){
                binding.qIcon.visibility = View.GONE
                binding.qTitle.visibility = View.GONE
            }else{
                binding.qTitle.setText(item.qTitle)
            }

            if(item.aImg != null)
            {
                //*****나중에 구현
                // 삽입 이미지
                //binding.aImg.setImageDrawable(item.aImg)
            }else{
                binding.aImg.visibility = View.GONE
            }

            binding.linkInsertTxt.visibility = View.GONE
            binding.linkInsertBtn.visibility = View.GONE

            // 링크
            if(item.linkUri != "")
            {
                //링크 내용이 있으면?
                loadLink(item.linkUri.toString())
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
            binding.clLinkArea.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${item.linkUri}"))

                binding.root.context.startActivity(intent)

            }
        }

        companion object Factory {
            fun create(parent: ViewGroup): LoadQHolder {
                val binding = WriteQuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return LoadQHolder(binding)
            }
        }

        fun setLink(linkUri: String, title: String, content:String, bm1:Bitmap)
        {
            binding.linkUri.text = linkUri
            binding.linkTitle.text = title
            binding.linkContent.text = content
            binding.linkIcon.setImageBitmap(bm1)
        }
        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content:String = ""

        private fun loadLink(linkUri: String) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            title = ""
            bm1 = null
            url1 = null
            content = ""
            isrun = true
            Thread(Runnable {
                while(isrun)
                {//네이버의 경우에만 해당되는 것 같아.
                    try{
                        if (linkUri.contains("naver")) {
                            //linkIcon에 파비콘 추출해서 삽입하기
                            val doc = Jsoup.connect("${linkUri}").get()

                            //제목 들고 오기
                            val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")
                            if(linkUri.contains("blog"))
                            {
                                val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
                                title = doc2.title()
                                content = doc2.select("meta[property=\"og:description\"]").attr("content")
                            }else if(linkUri == "https://www.naver.com/"){
                                title = doc.title()
                                content = doc.select("meta[name=\"og:description\"]").attr("content")
                            }else{
                                title = doc.title()
                                content = doc.select("meta[property=\"og:description\"]").attr("content")
                            }
                            url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                            var conn: URLConnection = url1!!.openConnection()
                            conn.connect()
                            var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                            bm1 = BitmapFactory.decodeStream(bis)

                            bis.close()
                            setLink(linkUri, title, content, bm1!!)
                            isrun=false
                        } else {
                            val doc = Jsoup.connect("${linkUri}").get()
                            var favicon:String
                            var link:String
                            if(linkUri.contains("google"))
                            {
                                favicon = doc.select("meta[itemprop=\"image\"]").attr("content")
                                link = "https://www.google.com"+favicon
                                url1 = URL("${link}")
                            }else{
                                //파비콘 이미지 들고 오기
                                favicon = doc.select("link[rel=\"icon\"]").attr("href")
                                if(favicon=="")
                                {
                                    favicon = doc.select("link[rel=\"SHORTCUT ICON\"]").attr("href")
                                }
                                if (!favicon.contains("https:")) {
                                    link = "https://"+favicon
                                    url1 = URL("${link}")
                                }else{
                                    url1 = URL("${favicon}")
                                }
                            }

                            try{
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                bis.close()
                            }catch (e:Exception)
                            {
                            }
                            title = doc.title()

                            content = doc.select("meta[name=\"description\"]").attr("content")
                            if(content == "")
                            {
                                content = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if(title == "")
                            {
                                title = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if(bm1==null)
                            {
                                binding.linkIcon.visibility= View.GONE
                            }
                            setLink(linkUri, title, content, bm1!!)
                            isrun=false
                        }
                    }catch(e:Exception){
                        //링크가 올바르지 않을때->안내 토스트 메시지를 띄움

                    }
                }
            }).start()
        }
    }

    // 본문 Hodler
    class LoadContentHolder(val binding2:WriteContentItemBinding) : RecyclerView.ViewHolder(binding2.root) {

        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content:String = ""

        fun setContentList(item: loadContentData) {

            //사진 띄우기 **** - 나중에 하기.
            if(item.contentImg != null)
            {
                //binding.contentImg.setImageBitmap()
            }else{
                binding2.contentImg.visibility = View.GONE
            }

            //본문내용(텍스트)
            if(item.docContent=="")
            {
                binding2.docContent.visibility = View.GONE
            }else{
                binding2.docContent.setText(item.docContent)
            }
            binding2.linkInsertTxt.visibility = View.GONE
            binding2.linkInsertBtn.visibility = View.GONE
            binding2.clLinkArea.visibility = View.GONE

            // 링크
            if(item.linkUri != "")
            {
                //링크 내용이 있으면?
                loadLink(item.linkUri.toString())
            }

        }


        fun setLink(linkUri: String, title: String, content:String, bm1:Bitmap)
        {
            binding2.linkUri.text = linkUri
            binding2.linkTitle.text = title
            binding2.linkContent.text = content
            binding2.linkIcon.setImageBitmap(bm1)
        }

        companion object Factory {
            fun create(parent: ViewGroup): LoadContentHolder {
                val binding2 = WriteContentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return LoadContentHolder(binding2)
            }
        }

        private fun loadLink(linkUri: String) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            var linkUri = linkUri
            title = ""
            bm1 = null
            url1 = null
            content = ""
            isrun = true
            Thread(Runnable {
                while(isrun)
                {//네이버의 경우에만 해당되는 것 같아.
                    try{
                        if (linkUri.contains("naver")) {
                            if(!linkUri.contains("https://")){
                                linkUri = "https://${linkUri}"
                            }
                            //linkIcon에 파비콘 추출해서 삽입하기
                            val doc = Jsoup.connect("${linkUri}").get()

                            //제목 들고 오기
                            val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")
                            if(linkUri.contains("blog"))
                            {
                                val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
                                title = doc2.title()
                                content = doc2.select("meta[property=\"og:description\"]").attr("content")
                            }else if(linkUri == "https://www.naver.com/"){
                                title = doc.title()
                                content = doc.select("meta[name=\"og:description\"]").attr("content")
                            }else{
                                title = doc.title()
                                content = doc.select("meta[property=\"og:description\"]").attr("content")
                            }
                            url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                            var conn: URLConnection = url1!!.openConnection()
                            conn.connect()
                            var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                            bm1 = BitmapFactory.decodeStream(bis)

                            bis.close()
                            setLink(linkUri, title, content, bm1!!)
                            isrun=false
                        } else {
                            if(!linkUri.contains("https://")){
                                linkUri = "https://${linkUri}"
                            }
                            val doc = Jsoup.connect("${linkUri}").get()
                            var favicon:String
                            var link:String
                            if(linkUri.contains("google"))
                            {
                                favicon = doc.select("meta[itemprop=\"image\"]").attr("content")
                                link = "https://www.google.com"+favicon
                                url1 = URL("${link}")
                            }else{
                                //파비콘 이미지 들고 오기
                                favicon = doc.select("link[rel=\"icon\"]").attr("href")
                                if(favicon=="")
                                {
                                    favicon = doc.select("link[rel=\"SHORTCUT ICON\"]").attr("href")
                                }
                                if (!favicon.contains("https:")) {
                                    link = "https://"+favicon
                                    url1 = URL("${link}")
                                }else{
                                    url1 = URL("${favicon}")
                                }
                            }

                            try{
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                bis.close()
                            }catch (e:Exception)
                            {
                            }
                            title = doc.title()

                            content = doc.select("meta[name=\"description\"]").attr("content")
                            if(content == "")
                            {
                                content = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if(title == "")
                            {
                                title = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if(bm1==null)
                            {
                                binding2.linkIcon.visibility= View.GONE
                            }
                            setLink(linkUri, title, content, bm1!!)
                            isrun=false
                        }
                    }catch(e:Exception){
                        //링크가 올바르지 않을때->안내 토스트 메시지를 띄움

                    }
                }
            }).start()
        }
    }

    // 질문 Holder
    class MyQHolder(val binding: WriteQuestionItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setQList(item: WriteQuestionData) {

            if(item.qTitle == null){
                binding.qTitle.visibility = View.GONE
            }else{
                binding.qTitle.text = item.qTitle?.text
            }

            if(item.aImg == null)
            {
                binding.aImg.visibility = View.GONE
            }else{
                // 본문 삽입 이미지
                binding.aImg.setImageBitmap(item.aImg)
            }

            // 링크
            if(item.linkLayout == null){
                binding.clLinkArea.visibility = View.GONE
            }else{
                binding.clLinkArea.visibility = item.linkLayout?.visibility!!
            }

            // 링크 삽입이 이뤄지는 곳(editText영역)
            if(item.linkInsertTxt == null){
                binding.linkInsertTxt.visibility = View.GONE
            }else{
                binding.linkInsertTxt.visibility = item.linkInsertTxt?.visibility!!
            }

            // 링크 삽입이 이뤄지는 곳(버튼 영역)
            if(item.linkInsertTxt == null){
                binding.linkInsertBtn.visibility = View.GONE
            }else{
                binding.linkInsertBtn.visibility = item.linkInsertTxt?.visibility!!
            }

            // 링크된 요소들
            binding.linkTitle.text = item.linkTitle
            binding.linkUri.text = item.linkUri
            binding.linkIcon.setImageDrawable(item.linkIcon)

            // 대답
            binding.aTxt.text = item.aTxt?.text

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
        }

        companion object Factory {
            fun create(parent: ViewGroup): MyQHolder {
                val binding = WriteQuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return MyQHolder(binding)
            }
        }
        
        fun setLink(linkUri: String, title: String, content:String, bm1:Bitmap)
        {
            binding.linkUri.text = linkUri
            binding.linkTitle.text = title
            binding.linkContent.text = content
            binding.linkIcon.setImageBitmap(bm1)
        }


        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content:String = ""

        private fun loadLink(linkUri: String) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            var linkUri = linkUri
            title = ""
            bm1 = null
            url1 = null
            content = ""
            isrun = true

            Thread(Runnable {
                while(isrun)
                {//네이버의 경우에만 해당되는 것 같아.
                    try{
                        if (linkUri.contains("naver")) {
                            if(!linkUri.contains("https://")){
                                linkUri = "https://${linkUri}"
                            }
                            //linkIcon에 파비콘 추출해서 삽입하기
                            val doc = Jsoup.connect("${linkUri}").get()

                            //제목 들고 오기
                            val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")
                            if(linkUri.contains("blog"))
                            {
                                val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
                                title = doc2.title()
                                content = doc2.select("meta[property=\"og:description\"]").attr("content")
                            }else if(linkUri == "https://www.naver.com/"){
                                title = doc.title()
                                content = doc.select("meta[name=\"og:description\"]").attr("content")
                            }else{
                                title = doc.title()
                                content = doc.select("meta[property=\"og:description\"]").attr("content")
                            }
                            url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                            var conn: URLConnection = url1!!.openConnection()
                            conn.connect()
                            var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                            bm1 = BitmapFactory.decodeStream(bis)

                            bis.close()
                            setLink(linkUri, title, content, bm1!!)
                            isrun=false
                        } else {
                            if(!linkUri.contains("https://")){
                                linkUri = "https://${linkUri}"
                            }
                            val doc = Jsoup.connect("${linkUri}").get()
                            var favicon:String
                            var link:String
                            if(linkUri.contains("google"))
                            {
                                favicon = doc.select("meta[itemprop=\"image\"]").attr("content")
                                link = "https://www.google.com"+favicon
                                url1 = URL("${link}")
                            }else{
                                //파비콘 이미지 들고 오기
                                favicon = doc.select("link[rel=\"icon\"]").attr("href")
                                if(favicon=="")
                                {
                                    favicon = doc.select("link[rel=\"SHORTCUT ICON\"]").attr("href")
                                }
                                if (!favicon.contains("https:")) {
                                    link = "https://"+favicon
                                    url1 = URL("${link}")
                                }else{
                                    url1 = URL("${favicon}")
                                }
                            }

                            try{
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                bis.close()
                            }catch (e:Exception)
                            {
                            }
                            title = doc.title()

                            content = doc.select("meta[name=\"description\"]").attr("content")
                            if(content == "")
                            {
                                content = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if(title == "")
                            {
                                title = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if(bm1==null)
                            {
                                binding.linkIcon.visibility= View.GONE
                            }
                            setLink(linkUri, title, content, bm1!!)
                            isrun=false
                        }
                    }catch(e:Exception){
                        //링크가 올바르지 않을때->안내 토스트 메시지를 띄움

                    }
                }
            }).start()
        }

    }

    // 본문 Hodler
    class MyContentHolder(val binding2:WriteContentItemBinding) : RecyclerView.ViewHolder(binding2.root) {

        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content:String = ""

        fun setContentList(item: WriteContentData) {
            if(item.contentImg == null)
            {
                binding2.contentImg.visibility = View.GONE
            }else{
                // 본문 삽입 이미지
                binding2.contentImg.setImageBitmap(item.contentImg)
            }


            binding2.clLinkArea.visibility = View.GONE

            // 링크영역
            /*if(item.linkLayout == null){
                binding2.clLinkArea.visibility = View.GONE
            }else{
                binding2.clLinkArea.visibility = item.linkLayout?.visibility!!
            }*/

            // 링크 삽입이 이뤄지는 곳(editText영역)
            if(item.linkInsertTxt == null){
                binding2.linkInsertTxt.visibility = View.GONE
            }else{
                binding2.linkInsertTxt.visibility = item.linkInsertTxt?.visibility!!
            }

            // 링크 삽입이 이뤄지는 곳(버튼 영역)
            if(item.linkInsertTxt == null){
                binding2.linkInsertBtn.visibility = View.GONE
            }else{
                binding2.linkInsertBtn.visibility = item.linkInsertTxt?.visibility!!
            }

            if(item.linkUri == null){
                binding2.linkUri.visibility = View.GONE
            }

            //링크 입력 후 확인을 누르면 실행되는 리스너
            binding2.linkInsertBtn.setOnClickListener {
                //입력 받은 링크를 String으로 넣어 준 후
                linkUri = binding2.linkInsertTxt.getText().toString()
                //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
                //쓰레드 실행(한번만 실행함.)
                    loadLink(linkUri)
                    binding2.clLinkArea.visibility = View.VISIBLE
                    //로딩되는 시간동안 그냥 멈춰두기
                    Thread.sleep(350L)
                    binding2.linkInsertTxt.visibility = View.GONE
                    binding2.linkInsertBtn.visibility = View.GONE
            }
            // 링크된 요소들
            /*binding2.linkTitle.text = item.linkTitle
            binding2.linkUri.text = item.linkUri
            binding2.linkIcon.setImageDrawable(item.linkIcon)*/


            // 본문내용(텍스트)
            if(item.docContent == null){
                binding2.docContent.visibility = View.GONE
            }else{
                binding2.docContent.text = item.docContent?.text
            }

        }

        fun setLink(linkUri: String, title: String, content:String, bm1:Bitmap)
        {
            binding2.linkUri.text = linkUri
            binding2.linkTitle.text = title
            binding2.linkContent.text = content
            binding2.linkIcon.setImageBitmap(bm1)
        }

        companion object Factory {
            fun create(parent: ViewGroup): MyContentHolder {
                val binding2 = WriteContentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return MyContentHolder(binding2)
            }
        }

        private fun loadLink(linkUri: String) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            var linkUri = linkUri
            title = ""
            bm1 = null
            url1 = null
            content = ""
            isrun = true

            Thread(Runnable {
                while(isrun)
                {//네이버의 경우에만 해당되는 것 같아.
                    try{
                        if (linkUri.contains("naver")) {
                            if(!linkUri.contains("https://")){
                                linkUri = "https://${linkUri}"
                            }
                            //linkIcon에 파비콘 추출해서 삽입하기
                            val doc = Jsoup.connect("${linkUri}").get()

                            //제목 들고 오기
                            val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")
                            if(linkUri.contains("blog"))
                            {
                                val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
                                title = doc2.title()
                                content = doc2.select("meta[property=\"og:description\"]").attr("content")
                            }else if(linkUri == "https://www.naver.com/"){
                                title = doc.title()
                                content = doc.select("meta[name=\"og:description\"]").attr("content")
                            }else{
                                title = doc.title()
                                content = doc.select("meta[property=\"og:description\"]").attr("content")
                            }
                            url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                            var conn: URLConnection = url1!!.openConnection()
                            conn.connect()
                            var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                            bm1 = BitmapFactory.decodeStream(bis)

                            bis.close()
                            setLink(linkUri, title, content, bm1!!)
                            isrun=false
                        } else {
                            if(!linkUri.contains("https://")){
                                linkUri = "https://${linkUri}"
                            }
                            val doc = Jsoup.connect("${linkUri}").get()
                            var favicon:String
                            var link:String
                            if(linkUri.contains("google"))
                            {
                                favicon = doc.select("meta[itemprop=\"image\"]").attr("content")
                                link = "https://www.google.com"+favicon
                                url1 = URL("${link}")
                            }else{
                                //파비콘 이미지 들고 오기
                                favicon = doc.select("link[rel=\"icon\"]").attr("href")
                                if(favicon=="")
                                {
                                    favicon = doc.select("link[rel=\"SHORTCUT ICON\"]").attr("href")
                                }
                                if (!favicon.contains("https:")) {
                                    link = "https://"+favicon
                                    url1 = URL("${link}")
                                }else{
                                    url1 = URL("${favicon}")
                                }
                            }

                            try{
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                bis.close()
                            }catch (e:Exception)
                            {
                            }
                            title = doc.title()

                            content = doc.select("meta[name=\"description\"]").attr("content")
                            if(content == "")
                            {
                                content = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if(title == "")
                            {
                                title = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if(bm1==null)
                            {
                                binding2.linkIcon.visibility= View.GONE
                            }
                            setLink(linkUri, title, content, bm1!!)
                            isrun=false
                        }
                    }catch(e:Exception){
                        //링크가 올바르지 않을때->안내 토스트 메시지를 띄움

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

    fun updateItems(item:WriteItem, position: Int)
    {
        this.items[position].apply {item}
    }

    /*fun addItems(String s) {
        this.items.add(item)
        //this.notifyDataSetChanged()
    }*/

    interface ItemClickListener{
        fun onClick(view: View,position: Int)
    }

    //를릭 리스너
    private lateinit var itemClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    // answer 이미지, 링크 추가 시 사용
    fun modifyItems(position: Int, item: WriteItem) {
        this.items.set(position, item)
        this.notifyDataSetChanged()
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

}