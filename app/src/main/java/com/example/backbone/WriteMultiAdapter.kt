package com.example.backbone

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.ActivityWritingBinding
import com.example.backbone.databinding.WriteContentItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding
import com.google.android.material.internal.ContextUtils.getActivity
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files.size

private var isrun:Boolean = false
class WriteMultiAdapter(context: WritingActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private lateinit var binding:WriteQuestionItemBinding
    private lateinit var binding2:WriteContentItemBinding
    private lateinit var binding3:ActivityWritingBinding

    private val items = mutableListOf<WriteItem>()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_Question -> {
            MyQHolder.create(parent)
        }
        TYPE_Content -> {
            MyContentHolder.create(parent)
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is MyQHolder -> {
                holder.setQList(items[position] as WriteQuestionData)
            }
            is MyContentHolder -> {

                holder.setContentList(items[position] as WriteContentData)

                holder.itemView.setOnClickListener{
                    itemClickListner.onClick(it,position)
                }
            }
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

            binding.aImg.setImageDrawable(item.aImg)

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
        }

        companion object Factory {
            fun create(parent: ViewGroup): MyQHolder {
                val binding = WriteQuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return MyQHolder(binding)
            }
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

            // 본문 삽입 이미지
            binding2.contentImg.setImageDrawable(item.contentImg)

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

            //링크 입력 후 확인을 누르면 실행되는 리스너
            binding2.linkInsertBtn.setOnClickListener {
                //입력 받은 링크를 String으로 넣어 준 후
                linkUri = binding2.linkInsertTxt.getText().toString()
                //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
                isrun = true
                //쓰레드 실행(한번만 실행함.)
                loadLink(linkUri)
                binding2.clLinkArea.visibility = View.VISIBLE
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

        var writingactivity: WritingActivity? = null
        private fun loadLink(linkUri: String) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            title = ""
            bm1 = null
            url1 = null
            content = ""
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

    fun addItems(item: WriteItem) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }

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

}