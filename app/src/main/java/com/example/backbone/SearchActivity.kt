package com.example.backbone

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.backbone.databinding.ActivitySearchBinding
import com.google.android.material.tabs.TabLayout

//검색 기능 문제점
//현재 프레임레이아웃에 올려져있는 프래그먼트 정보를 못 받아오는 중입니다.
class SearchActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySearchBinding
    lateinit var fragment:Fragment
    private lateinit var fragmentManager:FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    // 글 리스트를 담기 위한 배열 생성
    var wList = ArrayList<Writing>()
    // 질문 리스트를 담기 위한 배열 생성
    var qList = ArrayList<Question>()
    //DBHelper와 이어주도록 클래스 선언
    var db: DBHelper = DBHelper(this)
    var Tag:String = "Q"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        //화면을 키면 기본으로 보여지는 fragment 설정
        fragment = QuestionTabFragment()
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.frameLayout, fragment, Tag)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction!!.commit()


        // 각각의 탭들을 누를 때, framgent 변경
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // creating cases for fragment
                when (tab.position) {
                    // '질문' 탭 화면으로 변경

                    0 -> {fragment = QuestionTabFragment()
                        Tag = "Q"}

                    // '글' 탭 화면으로 변경
                    1 -> {fragment = TitleTabFragment()
                        Tag = "W"}
                }


                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                ft.replace(R.id.frameLayout, fragment, Tag)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        //검색 창 클릭 리스너
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //키보드에서 검색 버튼을 누르면 검색을 함
            override fun onQueryTextSubmit(query: String?): Boolean {
                //입력되면 db에서 검색하기
                if (query != null) {
                    qList = db.searchQuestion(query)
                    wList = db.searchWriting(query)
                }
                for(fragment: Fragment in supportFragmentManager.fragments) {
                    if (fragment.isVisible) {
                        val tag = fragment.tag
                        lateinit var frag: Fragment
                        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                        supportFragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        when(tag) {
                            "Q" -> {
                                frag = QuestionTabFragment()
                            }
                            "W" -> {
                                frag = TitleTabFragment()
                            }
                        }
                        transaction.replace(R.id.frameLayout, frag, tag)
                        transaction.addToBackStack(tag)
                        transaction.commit()
                        transaction.isAddToBackStackAllowed
                        break
                    }
                }

                return false
            }

            //입력창에서 글이 바뀔때마다 검색을 함
            override fun onQueryTextChange(newText: String?): Boolean {
                //입력되면 db에서 검색하기
                if (newText != null) {
                    qList = db.searchQuestion(newText)
                    wList = db.searchWriting(newText)
                }

                for(fragment: Fragment in supportFragmentManager.fragments) {
                    if (fragment.isVisible) {
                        val tag = fragment.tag
                        lateinit var frag: Fragment
                        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                        supportFragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        when(tag) {
                            "Q" -> {
                                frag = QuestionTabFragment()
                            }
                            "W" -> {
                                frag = TitleTabFragment()
                            }
                        }
                        transaction.replace(R.id.frameLayout, frag, tag)
                        transaction.addToBackStack(tag)
                        transaction.commit()
                        transaction.isAddToBackStackAllowed
                        break
                    }
                }

                return false
            }
        })

        //뒤로가기 버튼 클릭 리스너
        binding.backBtn.setOnClickListener {
            // 홈 화면으로 이동
            val backIntent = Intent(this@SearchActivity, HomeActivity::class.java)
            startActivity(backIntent)
            finish()
        }


        // searchView 클릭 전에도 텍스트 띄우기
        binding.searchView.onActionViewExpanded()
        Handler().postDelayed({ binding.searchView.clearFocus() }, 100)

        binding.searchView.setOnClickListener(View.OnClickListener
        { binding.searchView.isIconified = false }
        )

    }

}