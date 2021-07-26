package com.example.backbone

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.HomeCateItemBinding
import com.example.backbone.databinding.HomeWriteItemBinding
import com.example.backbone.databinding.MyQuestionItemBinding

class TitleTabAdapter(var myDocList:ArrayList<HomeDocListData>, val fragment_s: Fragment): RecyclerView.Adapter<Holder>()
{

    lateinit var fragment: Fragment
    lateinit var binding: HomeWriteItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HomeWriteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        this.fragment = fragment_s
        return Holder(binding)
    }

    // 목록에 보여줄 아이템의 개수
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val docListData =  myDocList.get(position)
        holder.setDocList(docListData)
    }

    // 목록에 보여줄 아이템의 개수
    override fun getItemCount(): Int {
        return myDocList.size
    }

}