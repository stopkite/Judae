package com.example.backbone

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.HomeWriteItemBinding
import com.example.backbone.databinding.NaviDrawerItemBinding

class NavigationAdapter(private var myNaviList: ArrayList<NavigationItemModel>) :
    RecyclerView.Adapter<NavigationItemViewHolder>() {

    //private lateinit var context: Context
    private lateinit var binding:NaviDrawerItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationItemViewHolder {
        val binding = NaviDrawerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NavigationItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return myNaviList.count()
    }

    override fun onBindViewHolder(holder: NavigationItemViewHolder, position: Int) {
       val naviListData = myNaviList.get(position)
        holder.setMenuList(naviListData)
    }

}

class NavigationItemViewHolder(val binding: NaviDrawerItemBinding): RecyclerView.ViewHolder(binding.root){

    // 화면에 데이터를 세팅하는 setDocList()메서드 구현
    fun setMenuList(items: NavigationItemModel){

        binding.naviTextList.text = items.selectList
    }
}