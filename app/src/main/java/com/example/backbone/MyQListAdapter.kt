package com.example.backbone

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.MyQuestionItemBinding

class MyQListAdapter(context: MyQuestionActivity, var myQList:ArrayList<MyQListData>):RecyclerView.Adapter<QHolder>() {
    private lateinit var binding: MyQuestionItemBinding
    var context: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QHolder {
        val binding = MyQuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

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
            Intent(context, ReadingActivity::class.java).apply {
                putExtra("data", myQList[position].writeID.toString())
                Log.d("태그", "${myQList[position].writeID}")
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

class QHolder(val binding: MyQuestionItemBinding, var context: Context): RecyclerView.ViewHolder(binding.root) {
    fun setQList(myQList:MyQListData){
        binding.myQImg.setImageDrawable(myQList.icon)
        binding.myQQContent.text = myQList.qContent
        binding.myQQTitle.text = myQList.qTitle

    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int)

    }
    //를릭 리스너
    private lateinit var itemClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}