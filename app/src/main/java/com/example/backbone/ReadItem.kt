package com.example.backbone

import android.graphics.drawable.Drawable
import android.view.View

interface ReadItem

data class ReadQuestionData(
        var qTitle: String?, var aImg: ByteArray?,
        var linkLayout: View?, var linkTitle:String?, var linkUri:String?, var linkIcon: Drawable?, var linkImg: Drawable?,
        var aTxt: String?): ReadItem

data class ReadContentData(var contentImg: ByteArray?,
                            var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Drawable?, var linkImg: Drawable?,
                            var docContent: String): ReadItem

