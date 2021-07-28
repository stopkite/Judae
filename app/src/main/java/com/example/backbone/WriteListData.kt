package com.example.backbone

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView

data class WriteListData(var qTitle:EditText?, var aImg:Drawable?,
                         var linkLayout: View?, var linkTitle:String?, var linkUri:String?, var linkIcon:Drawable?, var linkImg:Drawable?,
                         var aTxt: EditText?, var addAnswer: ImageButton?)



/*
질문 아이콘 = qIcon
질문 제목 = qTitle
첨부 사진 = aImg

<링크>
제목 = linkTitle
uri = linkUri
아이콘 = linkIcon
썸네일 = linkImg

대답 아이콘 = aIcon
대답 = aTxt
추가버튼 = addAnswer


 */