package com.example.backbone

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.FragmentQuestionTabBinding
import com.example.backbone.databinding.FragmentTitleTabBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TitleTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TitleTabFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentTitleTabBinding
    // 리사이클러뷰에 붙일 어댑터 선언
    lateinit var  wAdapter:TitleTabAdapter

    private lateinit var recyclerView: RecyclerView
    lateinit var text:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // HomeCateListData 클래스를 담는 배열 생성
        var myDocList = ArrayList<SearchDocListData>()
        wAdapter = TitleTabAdapter(myDocList, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view:View = inflater.inflate(R.layout.fragment_title_tab, container, false)
        // Inflate the layout for this fragment

        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        //리스트가 딸려있는 곳의 binding 연결
        binding = FragmentTitleTabBinding.inflate(layoutInflater)
        // xml에서 리사이클러뷰를 가져와서 변수 선언함.
        recyclerView = binding.titleList

        // 글 리스트를 담기 위한 배열 생성
        var myDocList = ArrayList<SearchDocListData>()

        // 글 클래스를 담는 배열 생성
        var wList = ArrayList<Writing>()


        wList = searchActivity!!.wList

        //글 데이터 받아온 객체를 순서대로 출력하기.
        //배열로 받아온 글 객체를 순서대로 출력하기.
        for (i in 0..(wList.size - 1)) {
            myDocList.add(
                SearchDocListData("${wList[i].Title}", "${wList[i].Category}",
                    "| ${wList[i].Date}")
            )
        }
        // 어댑터 변수 초기화
        wAdapter = TitleTabAdapter(myDocList, this)

        // 리사이클러 뷰 타입 설정
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 만든 어댑터 recyclerview에 연결
        view.findViewById<RecyclerView>(R.id.titleList).adapter = wAdapter
    }

    //mainactivity의 함수를 사용하기 위해 호출해준 부분
    var searchActivity: SearchActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchActivity = getActivity() as SearchActivity
    }
/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TitleTabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TitleTabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}