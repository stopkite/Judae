package com.example.backbone

import android.graphics.Bitmap
import java.net.URL

class Link {
    var LinkID: Int = 0
    var LinkContent:String = ""
    var LinkUrl:String = ""
    var LinkFavicon: URL? = null
    var LinkImg: URL? = null
    var LinkTitle:String = ""

    constructor()
    {

    }
    constructor(LinkUrl:String, LinkTitle:String, LinkContent:String, LinkFavicon:URL, LinkImg:URL)
    {
        this.LinkUrl = LinkUrl
        this.LinkTitle = LinkTitle
        this.LinkContent = LinkContent
        this.LinkFavicon = LinkFavicon
        this.LinkImg = LinkImg
    }
}