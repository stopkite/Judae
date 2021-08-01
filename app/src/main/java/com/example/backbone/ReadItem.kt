package com.example.backbone

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.*

interface ReadItem

data class ReadQuestionData(var qTitle: String, var aImg: Drawable?,
                             var linkLayout: View?, var linkTitle:String?, var linkUri:String?, var linkIcon: Drawable?, var linkImg: Drawable?,
                            var aTxt: TextView?): ReadItem

data class ReadContentData(var contentImg: ImageView?,
                            var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Drawable?, var linkImg: Drawable?,
                            var docContent: String): ReadItem

