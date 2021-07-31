package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.FragmentBottomListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomFragmentList(db: DBHelper)  : BottomSheetDialogFragment(), onClick_interface {
    //데이터베이스 연결하기
    var db:DBHelper = db
    //레이아웃 연결하기 위해 필요한 변수
    private lateinit var binding1:FragmentBottomListBinding

    // 리사이클러뷰에 붙일 어댑터 선언
    private lateinit var homeCateListAdapter: HomeCateListAdapter

    // 리사이클러뷰 선언
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // HomeCateListData 클래스를 담는 배열 생성
        var myDocList = ArrayList<HomeCateListData>()
        // HomeCateListData 배열을 가진 homecatelist어댑터 선언, 연결해주기.
        homeCateListAdapter = HomeCateListAdapter(myDocList, this, this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // 화면에 띄울 뷰를 지정
        var view:View = inflater.inflate(R.layout.fragment_bottom_list, container, false)

        // 카테고리 추가 버튼 클릭 리스너 선언
        view.findViewById<ImageButton>(R.id.category_add).setOnClickListener { view ->
            hoemActivity?.onButtonClicked()
            //해당 프래그먼트 창 지우기
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 리스트가 딸려있는 곳의 binding 연결
        binding1 = FragmentBottomListBinding.inflate(layoutInflater)
        // xml에서 리사이클러뷰를 가져와서 변수 선언함.
        recyclerView = binding1.docList

        // HomeCateListData 클래스를 담는 배열 생성
        var myDocList = ArrayList<HomeCateListData>()

        //참고용!
        //수영아 사랑해...
        //카테고리 데이터 받아와서 카테고리용 배열에 넣어놓기. - 이해 안되면 HomeActivity에 띄운 거 참고하기.
        var categoryList = ArrayList<String>()
        categoryList = db.getCategory()
        //배열로 받아온 글 객체를 순서대로 출력하기.
        for(i in 0..(categoryList.size-1))
        {
            myDocList.add(
                    HomeCateListData("${categoryList[i]}")
            )
        }
        // 어댑터 변수 초기화
        homeCateListAdapter = HomeCateListAdapter(myDocList, this, this)

        // 아이템 구분선 색상 설정
        val dividerItemDecoration = DividerItemDecoration(this.context,LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.recycler_divider_qlist))

        // 아이템 구분선 삽입
        view.findViewById<RecyclerView>(R.id.docList).addItemDecoration(dividerItemDecoration)

        // 리사이클러 뷰 타입 설정
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 만든 어댑터 recyclerview에 내용 설정한 배열 띄우기
        view.findViewById<RecyclerView>(R.id.docList).adapter = homeCateListAdapter
    }

    //mainactivity의 함수를 사용하기 위해 호출해준 부분
    var hoemActivity: HomeActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        hoemActivity = getActivity() as HomeActivity
    }

    //삭제
    override fun onDetach() {
        super.onDetach()
        hoemActivity = null
    }

    //interface에서 선언한 함수 정의하기
    //카테고리 수정하기 위해 다른 프래그먼트 열기.
    //HomeActivity에 정의되어있는 함수(수정할 카테고리 값을 들고 BottomFragmentEdit를 여는 함수)와 연결하기 - 그리고 현재 프래그먼트 창 닫기
    override fun EditCategory(cateName: String) {
        //추가하기 버튼 누르면 될 것 같음
        hoemActivity = activity as HomeActivity?
        //fragmentChange_for_adapter mainactivity에 구현
        hoemActivity?.fragmentChange_for_adapter(cateName)
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.popBackStack()
    }

    //interface에서 선언한 함수 정의하기
    //클릭한 카테고리와 같은 카테고리를 가진 글 목록을 불러오는 함수
    //HomeActivity에 정의되어있는 함수와 연결하기 - 그리고 현재 프래그먼트 창 닫기
    override fun LoadWriting(cateName: String) {
        //추가하기 버튼 누르면 될 것 같음
        hoemActivity = activity as HomeActivity?
        hoemActivity?.refresh(cateName, db)
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.popBackStack()
    }

}
