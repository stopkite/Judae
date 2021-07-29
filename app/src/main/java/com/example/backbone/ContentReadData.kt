package com.example.backbone

import android.view.View
import android.widget.ImageView
import android.widget.TextView

data class ContentReadData(var docTitle:TextView?, var titleImg:ImageView?,
                           var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon:ImageView?, var linkImg:ImageView?,
                            var docContent:TextView?)


/*
본문 제목: docTitle
본문 이미지: titleImg

<링크된 영역>
링크 항목들을 감싸는 레이아웃 = linkLayout
제목 = linkTitle
내용 = linkContent
uri = linkUri
아이콘 = linkIcon
썸네일 = linkImg

본문 내용: docContent

 */