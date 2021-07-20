package com.example.backbone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.HomeCateItemBinding

class HomeCateListAdapter(var myCateList:ArrayList<HomeCateListData>): RecyclerView.Adapter<CateHolder>() {

    private lateinit var binding: HomeCateItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CateHolder {
        val binding = HomeCateItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return CateHolder(binding)
    }


    override fun getItemCount(): Int {
        return myCateList.size
    }

    override fun onBindViewHolder(holder: CateHolder, position: Int) {
        val cateList = myCateList.get(position)
        holder.setCateList(cateList)
    }

}

class CateHolder(val binding: HomeCateItemBinding): RecyclerView.ViewHolder(binding.root){

    fun setCateList(myCateList: HomeCateListData){
        binding.homeCateName.text = myCateList.cateName
        //binding.editCateName.setImageDrawable(myCateList.editBtn?.drawable)
    }
}