package com.example.backbone

import android.content.ClipData
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView

interface WriteItem

data class WriteQuestionData(var qTitle: EditText?, var aImg: Drawable?,
                             var linkInsertTxt: EditText?, var linkInsertBtn: Button?,
                             var linkLayout: View?, var linkTitle:String?, var linkUri:String?, var linkIcon: Drawable?, var linkImg: Drawable?,
                             var aTxt: EditText?, var addAnswer: ImageButton?): WriteItem

data class WriteContentData(var contentImg: ImageView?,
                                var linkInsertTxt:EditText?, var linkInsertBtn:Button?,
                                var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Drawable?, var linkImg: Drawable?,
                                var docContent:EditText?): WriteItem
