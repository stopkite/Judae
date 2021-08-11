package com.example.backbone

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.ActivitySavingBinding
import com.example.backbone.databinding.SaveCategoryItemBinding

class SaveCateAdapter(context: Context, val categoryArrayCat:ArrayList<String>,saveBtn: Button):BaseAdapter() {

    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    // category_select_item.xml 화면 불러오기
    lateinit var binding: SaveCategoryItemBinding
    var selectedPosition = -1
    // WritingActivity 에서 가져온 저장하기 버튼
    var saveBtn = saveBtn

    // single 선택과 버튼 (비)활성화를 위한 변수
    companion object {

    }

    // 리스트에 아이템이 몇 개가 들어있는 지 갯수 반환
    override fun getCount(): Int = categoryArrayCat.size

    // 몇 번 째 아이템을 가져올 건지
    override fun getItem(position: Int): Any = categoryArrayCat[position]
    override fun getItemId(position: Int): Long = position.toLong()

    // 리스트 뷰에 보이는 내용
    override fun getView(position: Int, contextView: View?, parent: ViewGroup?): View {
        binding = SaveCategoryItemBinding.inflate(inflater,parent,false)

        /*
        if(selectedPosition>-1)
        {
            binding.popupCategoryRbtn.isChecked = selectedPosition == position
        }
         */



        // 라디오 버튼 - 카테고리 이름 설정
        binding.popupCategoryRbtn.text = categoryArrayCat[position]

        // 라디오 버튼 클릭 이벤트
        //다중선택 방지 코드
        binding.popupCategoryRbtn.isChecked = selectedPosition == position
        binding.popupCategoryRbtn.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
        }

        // 버튼 비활성화 이벤트 -> 카테고리를 선택해야 활성화가 됨
        if(selectedPosition == -1){
            saveBtn.isEnabled = false
        }else if(selectedPosition != -1){
            saveBtn.isEnabled = true
        }

        return binding.root
    }

    fun setPosition(position: Int){
        selectedPosition = position -2
        notifyDataSetChanged()
    }
}