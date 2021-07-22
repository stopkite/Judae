package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.example.backbone.databinding.FragmentBottomEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentEdit()  : BottomSheetDialogFragment(){
    //var db:DBHelper = db
    private lateinit var binding:FragmentBottomEditBinding
    lateinit var cateName:String


    //onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("태그", "BottomFragmentEdit onCreate로 옴")
        }




    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {

        Log.d("태그", "BottomFragmentEdit onCreateView로 옴")
        var view:View = inflater.inflate(R.layout.fragment_bottom_edit, container, false)
        //val view: View = inflater.inflate(R.layout.fragment_bottom_list, container, false)
        //val category=requireArguments().getString("cateName")

        Log.d("태그", "BottomFragmentEdit onCreateView로 옴")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    { super.onViewCreated(view, savedInstanceState)
        Log.d("태그", "BottomFragmentEdit onCreateView로 옴")
        val tvName = view.findViewById<TextView>(R.id.edit_txt)
        val bundle = arguments
        tvName.text = bundle?.getString("cateName") }



}