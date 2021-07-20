package com.example.backbone

import android.content.Context
import android.os.Build
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
   // private lateinit var recyclerView: RecyclerView
    // 리사이클러뷰에 붙일 어댑터 선언
    private lateinit var homeCateListAdapter: HomeCateListAdapter

    private lateinit var binding2:HomeCateItemBinding
    val recyclerView by lazy {
        // xml에서 리사이클러뷰를 가져와서 변수 선언함.
        //recyclerView =
        binding1 = FragmentBottomListBinding.inflate(layoutInflater)
        binding1.docList }


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

        Log.d("태그", "이게 되기는 하냐?????? 되는 거냐고 onCreateView")
        // HomeCateListData 클래스를 담는 배열 생성
        val myDocList = ArrayList<HomeCateListData>()

        //binding2 = HomeCateItemBinding.inflate(layoutInflater)
        myDocList.add(
            HomeCateListData("기본")
        )

        // 어댑터 변수 초기화
        homeCateListAdapter = HomeCateListAdapter(myDocList)

        // 만든 어댑터 recyclerview에 연결
        recyclerView.adapter = homeCateListAdapter

        // 리사이클러 뷰 타입 설정
        recyclerView.layoutManager = LinearLayoutManager(context)


        return inflater.inflate(R.layout.fragment_bottom_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        recyclerView.apply {
            setHasFixedSize(true) // this.. 즉 rv_movie_list.setHasFixedSize()와 같다
            val linearLayout = LinearLayoutManager(context)
            layoutManager = linearLayout // this.layoutManager
        }

                Log.d("태그", "이게 되기는 하냐?????? 되는 거냐고 onAttach")

        }

        /*
                recyclerView.setHasFixedSize(true)  // lazy 접근 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
         */


}