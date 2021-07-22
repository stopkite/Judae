package com.example.backbone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
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
    var item_Element: TextView = binding.homeCateName
   public fun onClick(View v):
    {
        var pos:Int = getAdaterPostition()
        if(pos != RecyclerView.NO_POSITION)
        {
            intent:Intent = Intent(context, )
        }
    }

    fun setCateList(myCateList: HomeCateListData){
        binding.homeCateName.text = myCateList.cateName
        //binding.editCateName.setImageDrawable(myCateList.editBtn?.drawable)
    }
}