package com.example.backbone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.ActivityWritingBinding
import com.example.backbone.databinding.WriteContentItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding

class WriteMultiAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private lateinit var binding:WriteQuestionItemBinding
    private lateinit var binding2:WriteContentItemBinding
    private lateinit var binding3:ActivityWritingBinding

    private val items = mutableListOf<WriteItem>()
    private var wData: ArrayList<String>? = null
    private var qData: ArrayList<String>? = null

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

        fun setContentList(item: WriteContentData) {

            // 본문 삽입 이미지
            binding2.contentImg.setImageDrawable(item.contentImg?.drawable)

            // 링크영역
            if(item.linkLayout == null){
                binding2.clLinkArea.visibility = View.GONE
            }else{
                binding2.clLinkArea.visibility = item.linkLayout?.visibility!!
            }

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

            // 링크된 요소들
            binding2.linkTitle.text = item.linkTitle
            binding2.linkUri.text = item.linkUri
            binding2.linkIcon.setImageDrawable(item.linkIcon)

            // 본문내용(텍스트)
            if(item.docContent == null){
                binding2.docContent.visibility = View.GONE
            }else{
                binding2.docContent.text = item.docContent?.text
            }

        }


        companion object Factory {
            fun create(parent: ViewGroup): MyContentHolder {
                val binding2 = WriteContentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return MyContentHolder(binding2)
            }
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