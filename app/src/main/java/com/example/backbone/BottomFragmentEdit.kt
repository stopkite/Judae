package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.backbone.databinding.FragmentBottomEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentEdit()  : BottomSheetDialogFragment(){

    private lateinit var binding:FragmentBottomEditBinding

    companion object {

        const val TAG = "BottomFragmentEdit"

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        binding = FragmentBottomEditBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_bottom_edit,container,false)
    }


}