package com.example.backbone

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.ViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.HomeCateItemBinding

//class invite_friend_list_Adapter (val context: Context, val friend_DataArray: MutableList<friend_data>,
//                                  var list_onClick_interface: list_onClick_interface,
//                                  ) : RecyclerView.Adapter<mViewH>()
class HomeCateListAdapter(var myCateList:ArrayList<HomeCateListData>, val fragment_s:Fragment, var onClick_interface: onClick_interface): RecyclerView.Adapter<CateHolder>() {

    lateinit var fragment:Fragment
    lateinit var binding: HomeCateItemBinding
    private var activity: HomeActivity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CateHolder {
        val binding = HomeCateItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        this.fragment = fragment_s
        return CateHolder(binding)
    }


    override fun getItemCount(): Int {
        return myCateList.size
    }

    override fun onBindViewHolder(holder: CateHolder, position: Int) {
        val cateList = myCateList.get(position)
        holder.setCateList(cateList)
        holder.binding.editCateName.setOnClickListener{
            onClick_interface.EditCategory(cateList.cateName)
        }
    }

}

class CateHolder(val binding: HomeCateItemBinding): RecyclerView.ViewHolder(binding.root){
    var item_Element: TextView = binding.homeCateName

    fun setCateList(myCateList: HomeCateListData){
        binding.homeCateName.text = myCateList.cateName
    }
    interface ItemClickListener{
        fun onClick(view: View,position: Int)

    }
    //를릭 리스너
    private lateinit var itemClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}