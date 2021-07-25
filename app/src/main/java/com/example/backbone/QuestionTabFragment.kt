package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.FragmentQuestionTabBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [QuestionTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuestionTabFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding:FragmentQuestionTabBinding
    // 리사이클러뷰에 붙일 어댑터 선언
    lateinit var  qAdapter:QuestionTabAdapter

    private lateinit var recyclerView: RecyclerView
    lateinit var text:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        // HomeCateListData 클래스를 담는 배열 생성
        var myDocList = ArrayList<MyQListData>()
        qAdapter = QuestionTabAdapter(myDocList, this)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        var view:View = inflater.inflate(R.layout.fragment_question_tab, container, false)
        // Inflate the layout for this fragment

        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        //리스트가 딸려있는 곳의 binding 연결
        binding = FragmentQuestionTabBinding.inflate(layoutInflater)
        // xml에서 리사이클러뷰를 가져와서 변수 선언함.
        recyclerView = binding.qList

        // 질문 리스트를 담기 위한 배열 생성
        var myDocList = ArrayList<MyQListData>()

        // Question 클래스를 담는 배열 생성
        var qList = ArrayList<Question>()


        qList = searchActivity!!.qList

        //질문 데이터 받아온 객체를 순서대로 출력하기.
        for(i in 0..(qList.size-1))
        {

            myDocList.add(MyQListData(resources.getDrawable(R.drawable.ic_launcher_background),
                    "${qList[i].Content}", "${qList[i].WritingTitle}"))

        }
        // 어댑터 변수 초기화
        qAdapter = QuestionTabAdapter(myDocList, this)

        // 리사이클러 뷰 타입 설정
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 만든 어댑터 recyclerview에 연결
        view.findViewById<RecyclerView>(R.id.qList).adapter = qAdapter
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
         * @return A new instance of fragment QuestionTabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuestionTabFragment(db).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
 */


}