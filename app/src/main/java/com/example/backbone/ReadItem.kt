package com.example.backbone

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View

interface ReadItem

data class ReadQuestionData(
        var qTitle: String?, var aImg: Bitmap?,
        var linkLayout: View?, var linkTitle:String?, var linkUri:String?, var linkIcon: Drawable?, var linkImg: Drawable?,
        var aTxt: String?, var Date:String?, var ColorChanged:Boolean?): ReadItem

data class ReadContentData(var contentImg: Bitmap?,
                            var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Drawable?, var linkImg: Drawable?,
                            var docContent: String): ReadItem

