package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import com.example.backbone.databinding.FragmentBottomAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentAdd(db: DBHelper)  : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentBottomAddBinding
    lateinit var editTxt:EditText

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
        var view:View = inflater.inflate(R.layout.fragment_bottom_add, container, false)
        var editText = view.findViewById<EditText>(R.id.edit_txt)
        view.findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.cate_AddBtn).setOnClickListener { view ->
            if(!editText.text.equals(""))
            {
                db.addCategory(editText.text.toString())
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                hoemActivity?.loadCategory(db)
            }
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