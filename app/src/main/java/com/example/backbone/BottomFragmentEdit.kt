package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.backbone.databinding.FragmentBottomEditBinding
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
        //editText.setText(cateName)
        editText.setHint(cateName)

        //수정 버튼
        view.findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.cate_AddBtn).setOnClickListener { view ->
            if(editText.getText().toString().length != 0&&db.isExistCategory(editText.text.toString())==0)
            {
                //수정 실행
                db.editCategory(cateName, editText.text.toString())
                //수정 후 다시 업데이트된 카테고리 리스트 화면을 가기 위한 코드
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()

                //홈 화면에도 변경사항 반영되게 하기
                hoemActivity?.loadAdapter(db)

                //카테고리 화면 띄우게 하기
                hoemActivity?.loadCategory(db)
            }
            else if(editText.text.toString().equals(cateName)){
                Toast.makeText(getActivity(), "이전 카테고리 이름입니다.", Toast.LENGTH_SHORT).show()
            }else if(!editText.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "중복된 카테고리 이름이 있습니다.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(getActivity(), "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        
        //삭제 버튼
        view.findViewById<Button>(R.id.cate_DeleteBtn).setOnClickListener { view ->
            //삭제 실행
            db.deleteCategory(cateName)
            //삭제 후 다시 업데이트된 카테고리 리스트 화면을 가기 위한 코드
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
            hoemActivity?.loadCategory(db)
        }

        return view

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binding = FragmentBottomEditBinding.inflate(layoutInflater)
        hoemActivity = getActivity() as HomeActivity
    }


}