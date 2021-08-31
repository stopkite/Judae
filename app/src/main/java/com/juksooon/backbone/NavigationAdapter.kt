package com.juksooon.backbone

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.juksooon.backbone.databinding.NaviDrawerItemBinding

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

        // 아이템 간 간격 설정
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 55.toPx()
        holder.itemView.requestLayout()
    }

    // px을 dp 단위로 바꿔주는 코드 (layoutParamas가 px로만 값을 받기 때문에 바꿔줘야 한다.)
    fun Int.toPx():Int = (this * Resources.getSystem().displayMetrics.density).toInt()

}

class NavigationItemViewHolder(val binding: NaviDrawerItemBinding): RecyclerView.ViewHolder(binding.root){

    // 화면에 데이터를 세팅하는 setDocList()메서드 구현
    fun setMenuList(items: NavigationItemModel){

        binding.naviTextList.text = items.selectList
    }
}