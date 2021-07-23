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
import com.example.backbone.databinding.FragmentBottomEditBinding
import com.example.backbone.databinding.FragmentBottomListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentEdit(db: DBHelper, ctName:String)  : BottomSheetDialogFragment(){
    var db:DBHelper = db
    private lateinit var binding:FragmentBottomEditBinding

    var cateName: String =ctName

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {
        var view:View = inflater.inflate(R.layout.fragment_bottom_edit, container, false)

        //카테고리 값 받아와 editTxt에 넣어주기
        var editt:EditText = view.findViewById(R.id.edit_txt)
        editt.setText(cateName)

        return view

    }
/*
   override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        Log.d("태그", "BottomFragmentEdit onCreateView로 옴")
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d("태그", "BottomFragmentEdit onCreateView로 옴")
        var view:View = inflater.inflate(R.layout.fragment_bottom_edit, container, false)
        //val view: View = inflater.inflate(R.layout.fragment_bottom_list, container, false)
        //val category=requireArguments().getString("cateName")

        Log.d("태그", "BottomFragmentEdit onCreateView로 옴")
        return view
    }

 */

}