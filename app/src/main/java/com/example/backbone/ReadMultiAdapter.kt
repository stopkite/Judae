package com.example.backbone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.*

class ReadMultiAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private lateinit var binding: ReadQuestionItemBinding
    private lateinit var binding2: ReadContentItemBinding

    private val items = mutableListOf<ReadItem>()

    companion object {
        private const val TYPE_Question = 0
        private const val TYPE_Content = 1
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
                holder.setQList(items[position] as ReadQuestionData)
            }
            is MyContentHolder -> {
                holder.setContentList(items[position] as ReadContentData)
            }
        }
    }

    // 질문 Holder
    class MyQHolder(val binding: ReadQuestionItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setQList(item: ReadQuestionData) {

            // 질문 아이콘
            binding.qIcon.setImageDrawable(item.qIcon)

            // 질문 제목
            if(item.qTitle == null){
                binding.qTitle.visibility = View.GONE
            }else{
                binding.qTitle.text = item.qTitle?.text
            }

            // 삽입 이미지
            binding.aImg.setImageDrawable(item.aImg)

            // 링크
            if(item.linkLayout == null){
                binding.clLinkArea.visibility = View.GONE
            }else{
                binding.clLinkArea.visibility = item.linkLayout?.visibility!!
            }

            // 링크된 요소들
            binding.linkTitle.text = item.linkTitle
            binding.linkUri.text = item.linkUri
            binding.linkIcon.setImageDrawable(item.linkIcon)

            // 대답 아이콘
            binding.aIcon.setImageDrawable(item.aIcon)

            // 대답
            binding.aTxt.text = item.aTxt?.text

        }

        companion object Factory {
            fun create(parent: ViewGroup): MyQHolder {
                val binding = ReadQuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return MyQHolder(binding)
            }
        }
    }

    // 본문 Hodler
    class MyContentHolder(val binding2:ReadContentItemBinding) : RecyclerView.ViewHolder(binding2.root) {

        fun setContentList(item: ReadContentData) {
            // 본문 삽입 이미지
            binding2.contentImg.setImageDrawable(item.contentImg?.drawable)

            // 링크영역
            if(item.linkLayout == null){
                binding2.clLinkArea.visibility = View.GONE
            }else{
                binding2.clLinkArea.visibility = item.linkLayout?.visibility!!
            }

            // 링크된 요소들
            binding2.linkTitle.text = item.linkTitle
            binding2.linkUri.text = item.linkUri
            binding2.linkIcon.setImageDrawable(item.linkIcon)

            //본문내용(텍스트)
            binding2.docContent.text = item.docContent?.text

        }

        companion object Factory {
            fun create(parent: ViewGroup): MyContentHolder {
                val binding2 = ReadContentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return MyContentHolder(binding2)
            }
        }
    }


    override fun getItemCount() = items.size

    fun addItems(item: ReadItem) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }
}