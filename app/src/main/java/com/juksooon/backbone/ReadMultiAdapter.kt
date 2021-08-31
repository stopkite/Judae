package com.juksooon.backbone

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.juksooon.backbone.databinding.*


private var isrun:Boolean = false
class ReadMultiAdapter(contxt: Context, activity: ReadingActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private lateinit var binding: ReadQuestionItemBinding
    private lateinit var binding2: ReadContentItemBinding
    private var items = mutableListOf<ReadItem>()
    var activity = activity
    var context:Context = contxt

    companion object {
        private const val TYPE_Question = 0
        private const val TYPE_Content = 1
    }
    fun setData(data : ArrayList<ReadItem>){
        this.items = data
        notifyDataSetChanged()
    }
    override fun getItemViewType(position: Int) = when (items[position]) {
        is ReadQuestionData -> {
            TYPE_Question
        }
        is ReadContentData -> {
            TYPE_Content
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)  : RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            TYPE_Question -> {
                binding = ReadQuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyQHolder(binding, activity)
            }
            TYPE_Content -> {
                binding2 = ReadContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyContentHolder(binding2, activity, context)
            }
            else -> {
                throw IllegalStateException("Not Found ViewHolder Type $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyQHolder -> {
                holder.setQList(items[position] as ReadQuestionData)
                holder.setIsRecyclable(false)

                //선택된 아이템에 대한 정보 빼내오기
                var ReadList = items[position] as ReadQuestionData

                holder.binding.clLinkArea.setOnClickListener {
                    if(binding.linkTitle.text != "404Error")
                    {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${ReadList.linkUri}"))
                        binding.root.context.startActivity(intent)
                    }else{
                        Toast.makeText(context, "         유효하지 않은 링크입니다. \n 편집을 이용해 링크를 수정해주세요,", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            is MyContentHolder -> {
                holder.setContentList(items[position] as ReadContentData)
                holder.setIsRecyclable(false)

                //선택된 아이템에 대한 정보 빼내오기
                var ReadList = items[position] as ReadContentData

                holder.binding2.clLinkArea.setOnClickListener {
                    if(binding2.linkTitle.text != "404Error")
                    {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${ReadList.linkUri}"))
                        binding2.root.context.startActivity(intent)
                    }else{
                        Toast.makeText(context, "         유효하지 않은 링크입니다. \n 편집을 이용해 링크를 수정해주세요,", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // 질문 Holder
    class MyQHolder(val binding: ReadQuestionItemBinding, var activity:ReadingActivity) : RecyclerView.ViewHolder(binding.root) {
        fun setQList(item: ReadQuestionData) {
            // 질문 제목
            if(item.qTitle == ""|| item.qTitle == null){
                binding.qIcon.visibility = View.GONE
                //질문 부분 삭제하는 코드
                binding.qTitle.visibility = View.GONE
            }else{
                binding.qTitle.setText(item.qTitle)
            }

            if(item.aImg != null)
            {
                // 삽입 이미지
                binding.aImg.visibility = View.VISIBLE
                binding.aImg.setImageBitmap(item.aImg)
            }else{
                binding.aImg.visibility = View.GONE
            }

            // 링크
            if(item.linkUri != ""&&item.linkUri != null && item.linkUri != "null"){
                // 링크 정보는 있는데. 두번째로 불러온 정보일 때 -> 첫번째 정보에서 이미 받아온 링크 내용, 이미지 등 정보가 있을 때
                if (item.linkContent != null || item.linkTitle != null) {
                    binding.clLinkArea.visibility = View.VISIBLE
                    binding.linkTitle.text = item.linkTitle.toString()
                    binding.linkContent.text = item.linkContent.toString()
                    binding.linkUri.text = item.linkUri.toString()
                    if (item.linkIcon != null) {
                        binding.linkIcon.setImageBitmap(item.linkIcon)
                    } else {
                        binding.linkIcon.visibility = View.GONE
                    }
                    //링크 내용이 있으면?
                    //binding.clLinkArea.visibility = item.linkLayout?.visibility!!
                } else {
                    // 링크 정보를 불러오는 것이 처음 일때!
                    binding.clLinkArea.visibility = View.VISIBLE
                    activity.QuestionloadLink(item.linkUri.toString(), item, ReadingActivity())
                }
            }else{
                //링크 내용이 없으면?
                //binding.clLinkArea.visibility = item.linkLayout?.visibility!!
                binding.clLinkArea.visibility = View.GONE
            }

            // 대답 내용 삽입
            if(item.aTxt != ""&&item.aTxt!=null)
            {
                // 대답 상태에 따라 색 바꿔줌.
                if(item.ColorChanged == true)
                {
                    var date: String? = item.Date
                    var text:String = item.aTxt + "\n${date}"
                    var start = text.indexOf(date!!)
                    var end = start + date!!.length
                    val spannableString = SpannableString(text)
                    spannableString.setSpan(ForegroundColorSpan(Color.GRAY),0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(RelativeSizeSpan(0.8f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.aTxt.setText(spannableString)
                }else{
                    var date: String? = item.Date
                    var text:String = item.aTxt + "\n${date}"
                    var start = text.indexOf(date!!)
                    var end = start + date!!.length
                    val spannableString = SpannableString(text)
                    spannableString.setSpan(ForegroundColorSpan(Color.GRAY),start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(RelativeSizeSpan(0.8f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.aTxt.setText(spannableString)
                }
            }
        }


    }

    // 본문 Hodler
    class MyContentHolder(val binding2: ReadContentItemBinding, var activity: ReadingActivity, context: Context) : RecyclerView.ViewHolder(binding2.root) {
        fun setContentList(item: ReadContentData) {
            //사진 띄우기 **** - 나중에 하기.
            if(item.contentImg != null)
            {
                binding2.contentImg.setImageBitmap(item.contentImg)
            }else{
                binding2.contentImg.visibility = View.GONE
            }

            //본문내용(텍스트)
            if(item.docContent=="" || item.docContent == "null")
            {
                binding2.docContent.visibility = View.GONE
            }else{
                binding2.docContent.text = item.docContent
            }

            if(item.linkUri != ""){
                // 링크 정보는 있는데. 두번째로 불러온 정보일 때 -> 첫번째 정보에서 이미 받아온 링크 내용, 이미지 등 정보가 있을 때
                if(item.linkContent != null || item.linkTitle != null)
                {
                    binding2.clLinkArea.visibility = View.VISIBLE
                    binding2.linkTitle.text = item.linkTitle.toString()
                    binding2.linkContent.text = item.linkContent.toString()
                    binding2.linkUri.text = item.linkUri.toString()
                    if(item.linkIcon != null)
                    {
                        binding2.linkIcon.setImageBitmap(item.linkIcon)
                    }else{
                        binding2.linkIcon.visibility = View.GONE
                    }
                }else{
                    // 링크 정보를 불러오는 것이 처음 일때!
                    binding2.clLinkArea.visibility = View.VISIBLE
                    activity.adapterContentloadLink(item.linkUri.toString(), item, context = WritingActivity())
                }
            }else{
                binding2.clLinkArea.visibility = View.GONE
            }

        }

    }


    override fun getItemCount() = items.size

    fun addItems(item: ReadItem) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }
}