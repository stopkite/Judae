package com.example.backbone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.backbone.databinding.FragmentBottomListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentList()  : BottomSheetDialogFragment(){

    private lateinit var binding:FragmentBottomListBinding

    companion object {

        const val TAG = "BottomFragmentList"

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        binding = FragmentBottomListBinding.inflate(layoutInflater)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_bottom_list,container,false)
    }


}