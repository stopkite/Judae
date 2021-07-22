package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.example.backbone.databinding.FragmentBottomAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentAdd()  : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentBottomAddBinding

    companion object {

        const val TAG = "BottomFragmentAdd"

    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binding = FragmentBottomAddBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        var view:View = inflater.inflate(R.layout.fragment_bottom_add, container, false)
        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}