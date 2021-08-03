package com.example.backbone

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.HomeCateItemBinding
import com.example.backbone.databinding.HomeWriteItemBinding
import com.example.backbone.databinding.MyQuestionItemBinding

class QuestionTabAdapter(var myQList:ArrayList<MyQListData>, val fragment_s: Fragment, var context:Context): RecyclerView.Adapter<QHolder>()
{

    var contxt: Context = context
    lateinit var fragment: Fragment
    lateinit var binding: HomeCateItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QHolder {
        val binding = MyQuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        this.fragment = fragment_s
        return QHolder(binding, context)
    }

    override fun onBindViewHolder(holder: QHolder, position: Int) {
        val qList = myQList.get(position)
        holder.setQList(qList)

        // 아이템 간 간격 설정
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 59.toPx()
        holder.itemView.requestLayout()
        holder.itemView.setOnClickListener {
            Intent(contxt, ReadingActivity::class.java).apply {
                putExtra("data", myQList[position].writeID.toString())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { context.startActivity(this) }
        }
    }

    override fun getItemCount(): Int {
        return myQList.size
    }

    // px을 dp 단위로 바꿔주는 코드 (layoutParamas가 px로만 값을 받기 때문에 바꿔줘야 한다.)
    fun Int.toPx():Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}