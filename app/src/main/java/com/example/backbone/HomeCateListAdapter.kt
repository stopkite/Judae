package com.example.backbone

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.ViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.HomeCateItemBinding

class HomeCateListAdapter(var myCateList:ArrayList<HomeCateListData>, val fragment_s:Fragment): RecyclerView.Adapter<CateHolder>() {

    private lateinit var binding: HomeCateItemBinding
    private var activity: HomeActivity? = null
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
        holder.itemView.setOnClickListener{
            //SelectPostFrag는 메인 액티비티에 써져있다.
            var fragment: Fragment =BottomFragmentEdit()
            var bundle: Bundle= Bundle()
            bundle.putString("cateName",cateList.cateName.toString())

            fragment.arguments=bundle
            // 나는 fragment안에 fragment가 있기 때문에 이런식으로 bundle을 붙여줘야했다.
            /*그런게 아니라면
            fragment_s.fragmentManager!!.beginTransaction().replace(R.id.content).commit()
            해주면 된다.*/

            activity = fragment_s.activity as HomeActivity?
            //change_for_adapter는 mainactivity에 구현
            activity?.fragmentChange_for_adapter(fragment)
        }
        /*
                holder.itemView.setOnClickListener{
            ViewBindingAdapter.setClickListener()
            itemClickListner.onClick(it,position)
        }
         */

    }

}

class CateHolder(val binding: HomeCateItemBinding): RecyclerView.ViewHolder(binding.root){
    var item_Element: TextView = binding.homeCateName
    /*
       public fun onClick(View v):
    {
        var pos:Int = getAdaterPostition()
        if(pos != RecyclerView.NO_POSITION)
        {
            intent:Intent = Intent(context, HomeActivirt)
        }


        item_Element.text = item.cateName
        itemView.setOnClickListener {
            Intent(context, HomeActivity::class.java).apply{
                putExtra("data", item.toString())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run{requireContext().startActivity(this)}
        }

     */



    fun bind(item: HomeCateListData)
    {

    }

    fun setCateList(myCateList: HomeCateListData){
        binding.homeCateName.text = myCateList.cateName
        //binding.editCateName.setImageDrawable(myCateList.editBtn?.drawable)
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