package com.juksooon.backbone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.juksooon.backbone.databinding.HomeCateItemBinding


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
        //카테고리 리스트 제일 첫번째 데이터 - '전체' 카테고리(무조건 고정)
        //수정하지 못하도록 설정하기
        if(position == 0)
        {
            //전체 카테고리일 경우 수정 버튼 숨기기 - 수정, 삭제 아예 반영되지 않도록
            holder.setLayout()
        }
        
        //프래그먼트 리스트에 카테고리 이름 순서대로 띄우기
        holder.setCateList(cateList)

        //수정 버튼 누르면 발생하는 이벤트
        holder.binding.editCateName.setOnClickListener{
            //onClick_interface 통해서 BottomFragmentList.kt에서 바로 함수 정의할 수 있도록 선언
            //해당 함수에 대한 설명은 BottomFragmentList.kt에서 EditCategory 함수에 있음
            //BottomFragmentEdit로 이동시켜줌
            onClick_interface.EditCategory(cateList.cateName)
        }

        //카테고리 내용을 누르면 홈 액티비티에서 해당 카테고리를 가지고 있는 글 목록 불러오기
        holder.binding.homeCateName.setOnClickListener{
            //onClick_interface 통해서 BottomFragmentList.kt에서 바로 함수 정의할 수 있도록 선언
            //해당 함수에 대한 설명은 BottomFragmentList.kt에서 LoadWriting 함수에 있음
            onClick_interface.LoadWriting(cateList.cateName)
        }
    }
}

class CateHolder(val binding: HomeCateItemBinding): RecyclerView.ViewHolder(binding.root){
    //프래그먼트 리스트에 카테고리 이름을 인자로 받은 myCateList로 띄우기
    fun setCateList(myCateList: HomeCateListData){
        binding.homeCateName.text = myCateList.cateName
    }
    //리스트 첫번째의 수정 버튼은 GONE처리(아예 레이아웃에도 사라지도록 처리하기)
    fun setLayout(){
        binding.editCateName.visibility = View.GONE
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