package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.FragmentBottomListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragmentList()  : BottomSheetDialogFragment(){


    private lateinit var binding1:FragmentBottomListBinding

    // 리사이클러뷰에 붙일 어댑터 선언
    private lateinit var homeCateListAdapter: HomeCateListAdapter

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("태그", "이게 되기는 하냐?????? 되는 거냐고 onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("태그", "이게 되기는 하냐?????? 되는 거냐고 onCreateView")
        return inflater.inflate(R.layout.fragment_bottom_list, container, false)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //밑에 두 줄은 프레그먼트 띄우고 다른 프레그먼트로 이동할 때 사용하면 될 것 같음!
        //view.findViewById<View>(R.id.btnAddMoreItems).setOnClickListener { addMoreOnItem() }
        //(view.findViewById<View>(R.id.rclItems) as RecyclerView).adapter = adapter

        Log.d("태그", "이게 되기는 하냐?????? 되는 거냐고 onViewCreated")
        //리스트가 딸려있는 곳의 binding 연결
        binding1 = FragmentBottomListBinding.inflate(layoutInflater)
        // xml에서 리사이클러뷰를 가져와서 변수 선언함.
        recyclerView = binding1.docList

        // HomeCateListData 클래스를 담는 배열 생성
        var myDocList = ArrayList<HomeCateListData>()

        //그리고 HomeCateListData에 이미지는 넣지 않았어요! 그냥 나중에 그림 파일 나오면 그거 지정시키면 되는 거니깐 ㅎㅎ
        //참고용!
        //수영아 사랑해...
        myDocList.add(
            HomeCateListData("기본")
        )
        myDocList.add(
            HomeCateListData("일상")
        )


        // 어댑터 변수 초기화
        homeCateListAdapter = HomeCateListAdapter(myDocList)

        // 리사이클러 뷰 타입 설정
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 만든 어댑터 recyclerview에 연결
        //recyclerView.adapter = homeCateListAdapter
        view.findViewById<RecyclerView>(R.id.docList).adapter = homeCateListAdapter
    }

    private fun addMoreOnItem() {
        //추가하기 버튼 누르면 될 것 같음
        //homeCateListAdapter = HomeCateListAdapter(myDocList)
    }


}