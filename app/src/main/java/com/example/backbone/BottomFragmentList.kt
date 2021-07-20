package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.FragmentBottomListBinding
import com.example.backbone.databinding.HomeCateItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentList()  : BottomSheetDialogFragment(){

    private lateinit var binding1:FragmentBottomListBinding
    private lateinit var recyclerView: RecyclerView
    // 리사이클러뷰에 붙일 어댑터 선언
    private lateinit var homeCateListAdapter: HomeCateListAdapter

    private lateinit var binding2:HomeCateItemBinding

    // HomeDocListData 클래스를 담는 배열 생성
    val myDocList = ArrayList<HomeDocListData>()

    companion object {

        const val TAG = "BottomFragmentList"

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Context context = view.getContext();
        //val context: Context? = context
        super.onCreateView(inflater, container, savedInstanceState)
        binding1 = FragmentBottomListBinding.inflate(layoutInflater)
        // xml에서 리사이클러뷰를 가져와서 변수 선언함.
        recyclerView = binding1.docList
        // HomeCateListData 클래스를 담는 배열 생성
        val myDocList = ArrayList<HomeCateListData>()

        binding2 = HomeCateItemBinding.inflate(layoutInflater)
        myDocList.add(
            HomeCateListData("기본")
        )

        // 어댑터 변수 초기화
        homeCateListAdapter = HomeCateListAdapter(myDocList)

        // 만든 어댑터 recyclerview에 연결
        recyclerView.adapter = homeCateListAdapter

        // 리사이클러 뷰 타입 설정
        recyclerView.layoutManager = LinearLayoutManager(context)

        Log.d("태그태그", "실행이 되기는 하냐?")

        return inflater.inflate(R.layout.fragment_bottom_list, container, false)
    }


}