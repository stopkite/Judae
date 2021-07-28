package com.example.backbone

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.MyQuestionItemBinding
import com.example.backbone.databinding.ReadQuestionItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding

class ReadingAdapter (var readList:ArrayList<ReadListData>): RecyclerView.Adapter<ReadingHolder>(){

    private lateinit var binding:ReadQuestionItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingHolder {
        val binding = ReadQuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ReadingHolder(binding)
    }

    override fun onBindViewHolder(holder: ReadingHolder, position: Int) {
        val myReadList = readList.get(position)
        holder.setReadList(myReadList)
    }

    override fun getItemCount(): Int {
        return readList.size
    }

}

class ReadingHolder(val binding: ReadQuestionItemBinding): RecyclerView.ViewHolder(binding.root){
    fun setReadList(myReadList:ReadListData){
        binding.qIcon.setImageDrawable(myReadList.qIcon?.drawable)

        binding.qTitle.text = myReadList.qTitle?.text
        binding.aImg.setImageDrawable(myReadList.aImg)

        // 링크
        binding.clLinkArea.visibility = myReadList.linkLayout?.visibility!!
        binding.linkTitle.text = myReadList.linkTitle
        binding.linkUri.text = myReadList.linkUri
        binding.linkIcon.setImageDrawable(myReadList.linkIcon)
        binding.linkImg.setImageDrawable(myReadList.linkImg)

        binding.aIcon.setImageDrawable(myReadList.aIcon?.drawable)
        binding.aTxt.text = myReadList.aTxt?.text
    }
}