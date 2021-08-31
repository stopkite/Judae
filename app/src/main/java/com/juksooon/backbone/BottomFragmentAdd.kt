package com.juksooon.backbone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import com.juksooon.backbone.databinding.FragmentBottomAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentAdd(db: DBHelper)  : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentBottomAddBinding

    //mainactivity의 함수를 사용하기 위해 호출해준 부분
    var hoemActivity: HomeActivity? = null

    //DBHelper와 이어주도록 클래스 선언
    var db: DBHelper = db

    companion object {

        const val TAG = "BottomFragmentAdd"

    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binding = FragmentBottomAddBinding.inflate(layoutInflater)
        hoemActivity = getActivity() as HomeActivity
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        //화면에 띄울 view 정의하기
        var view:View = inflater.inflate(R.layout.fragment_bottom_add, container, false)
        //새로운 카테고리 내용을 입력받을 에디트 텍스트 연결해주기
        var editText = view.findViewById<EditText>(R.id.edit_txt)
        
        //해당 뷰에서 추가하기 버튼을 눌렀을 때 이벤트 정의
        view.findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.cate_AddBtn).setOnClickListener { view ->
            //중복된 카테고리 이름있는지 검사
            if(!editText.getText().toString().isEmpty()&&db.isExistCategory(editText.text.toString())==0)
            {
                //db에서 카테고리 추가하기 실행
                db.addCategory(editText.text.toString())
                //추가한 후 현재 화면 지우기
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                hoemActivity?.loadCategory(db)
            }
            else if(!editText.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "중복된 카테고리 이름이 있습니다.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(getActivity(), "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        //뒤로가기 버튼
        view.findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.cate_backBtn).setOnClickListener { view ->
            // 카테고리 리스트 화면으로 이동
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
            hoemActivity?.loadCategory(db)
        }

        return view
    }



    //삭제
    override fun onDetach() {
        super.onDetach()
        hoemActivity = null
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}