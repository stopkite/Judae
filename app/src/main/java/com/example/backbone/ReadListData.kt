package com.example.backbone

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView

data class ReadListData(var qIcon:ImageView?, var qTitle: TextView?, var aImg:Drawable?,
                        var linkLayout: View?, var linkTitle:String?, var linkUri:String?, var linkIcon:Drawable?, var linkImg:Drawable?,
                        var aIcon:ImageView?, var aTxt:TextView?)

/*
질문 아이콘 = qIcon
질문 제목 = qTitle
첨부 사진 = aImg

<링크>
링크항목들을 감싸는 레이아웃 = linkLayout
제목 = linkTitle
uri = linkUri
아이콘 = linkIcon
썸네일 = linkImg

대답 아이콘 = aIcon
대답 = aTxt

 */

