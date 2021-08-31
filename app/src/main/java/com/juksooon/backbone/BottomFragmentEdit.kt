package com.juksooon.backbone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.juksooon.backbone.databinding.ActivityHomeBinding
import com.juksooon.backbone.databinding.FragmentBottomEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentEdit(db: DBHelper, ctName:String)  : BottomSheetDialogFragment(){
    var db:DBHelper = db
    private lateinit var binding:FragmentBottomEditBinding
    private lateinit var binding2: ActivityHomeBinding
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
            //User 테이블에 해당 카테고리 내용이 중복 안 되는 내용인지, 공백이 아닌지 확인하기
            if(editText.getText().toString().length != 0&&db.isExistCategory(editText.text.toString())==0)
            {
                //수정 실행 - 이전 카테고리 내용과 새로운 카테고리 내용을 넣음
                //이전 카테고리 내용을 찾아와 해당 row의 값을 새로운 카테고리 내용으로 변경해줌.
                db.editCategory(cateName, editText.text.toString())
                //수정 후 다시 업데이트된 카테고리 리스트 화면을 가기 위한 코드
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().remove(this).commit()


                var afterCate:String = editText.text.toString()
                //홈 화면에 해당 카테고리가 띄워져 있다면 내용 갱신
                //그 후 카테고리 화면 띄우게 하기
                hoemActivity?.loadCategory(db)
                val cate = binding2.cateName.text
                hoemActivity?.refresh("${cate}", db)

                fragmentManager.popBackStack()

            }
            else if(editText.text.toString().equals(cateName)){
                //이전 카테고리 내용과 중복 된다면?
                Toast.makeText(getActivity(), "이전 카테고리 이름입니다.", Toast.LENGTH_SHORT).show()
            }else if(!editText.getText().toString().isEmpty()){
                //중복된 카테고리 이름이 있다면?
                Toast.makeText(getActivity(), "중복된 카테고리 이름이 있습니다.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(getActivity(), "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        
        //삭제 버튼
        view.findViewById<Button>(R.id.cate_DeleteBtn).setOnClickListener { view ->
            //DB에서 삭제 실행
            db.deleteCategory(cateName)
            //삭제 후 다시 업데이트된 카테고리 리스트 화면을 가기 위한 코드
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            hoemActivity?.refresh("전체", db)
            fragmentManager.popBackStack()
        }

        //뒤로가기 버튼
        view.findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.cate_backBtn).setOnClickListener { view ->
            // 카테고리 리스트 화면으로 이동
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            hoemActivity?.loadCategory(db)
            hoemActivity?.refresh("전체", db)
            fragmentManager.popBackStack()
        }

        return view

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binding = FragmentBottomEditBinding.inflate(layoutInflater)
        binding2 = ActivityHomeBinding.inflate(layoutInflater)
        hoemActivity = getActivity() as HomeActivity
    }


}