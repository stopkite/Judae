package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.backbone.databinding.FragmentBottomAddBinding
import com.example.backbone.databinding.FragmentBottomEditBinding
import com.example.backbone.databinding.FragmentBottomListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentEdit(db: DBHelper, ctName:String)  : BottomSheetDialogFragment(){
    var db:DBHelper = db
    private lateinit var binding:FragmentBottomEditBinding
    //mainactivity의 함수를 사용하기 위해 호출해준 부분
    var hoemActivity: HomeActivity? = null

    //수정 전 내용
    var cateName: String =ctName

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {
        var view:View = inflater.inflate(R.layout.fragment_bottom_edit, container, false)

        //카테고리 값 받아와 editTxt에 넣어주기
        var editText:EditText = view.findViewById(R.id.edit_txt)
        editText.setText(cateName)

        view.findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.cate_AddBtn).setOnClickListener { view ->
            if(!editText.text.equals("")&&!editText.text.equals(cateName))
            {
                //수정 실행
                db.editCategory(cateName, editText.text.toString())
                //수정 후 다시 업데이트된 카테고리 리스트 화면을 가기 위한 코드
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                hoemActivity?.loadCategory(db)
            }
        }

        return view

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binding = FragmentBottomEditBinding.inflate(layoutInflater)
        hoemActivity = getActivity() as HomeActivity
    }


}