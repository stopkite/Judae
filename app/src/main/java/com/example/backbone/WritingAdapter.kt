package com.example.backbone

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.MyQuestionItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding

class WritingAdapter (var writeList:ArrayList<WriteListData>): RecyclerView.Adapter<WritingHolder>(){

    private lateinit var binding:WriteQuestionItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WritingHolder {
        val binding = WriteQuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return WritingHolder(binding)
    }

    override fun onBindViewHolder(holder: WritingHolder, position: Int) {
        val myWriteList = writeList.get(position)
        holder.setWriteList(myWriteList)
    }

    override fun getItemCount(): Int {
        return writeList.size
    }

}

class WritingHolder(val binding: WriteQuestionItemBinding): RecyclerView.ViewHolder(binding.root){
    fun setWriteList(myWriteList:WriteListData){
        binding.qIcon.setImageDrawable(myWriteList.qIcon?.drawable)

        if(myWriteList.qTitle == null){
            binding.qTitle.visibility = View.GONE
        }else{
            binding.qTitle.text = myWriteList.qTitle?.text
        }

        binding.aImg.setImageDrawable(myWriteList.aImg)

        // 링크
        if(myWriteList.linkLayout == null){
            binding.clLinkArea.visibility = View.GONE
        }else{
            binding.clLinkArea.visibility = myWriteList.linkLayout?.visibility!!
        }

        binding.linkTitle.text = myWriteList.linkTitle
        binding.linkUri.text = myWriteList.linkUri
        binding.linkIcon.setImageDrawable(myWriteList.linkIcon)
        binding.linkImg.setImageDrawable(myWriteList.linkImg)

        binding.aIcon.setImageDrawable(myWriteList.aIcon?.drawable)
        binding.aTxt.text = myWriteList.aTxt?.text

        // 대답 추가 버튼
        if(myWriteList.addAnswer == null){
            binding.addAnswer.visibility = View.GONE
        }else {
            binding.addAnswer.setImageDrawable(myWriteList.addAnswer?.drawable)
        }
    }
}