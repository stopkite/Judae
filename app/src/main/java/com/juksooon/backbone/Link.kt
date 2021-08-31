package com.juksooon.backbone

import android.graphics.Bitmap

class Link {
    var LinkID: Int = 0
    var LinkContent:String = ""
    var LinkUrl:String = ""
    var LinkFavicon: Bitmap? = null
    var LinkTitle:String = ""

    constructor()
    {

    }
    constructor(LinkUrl:String, LinkTitle:String, LinkContent:String, LinkFavicon:Bitmap)
    {
        this.LinkUrl = LinkUrl
        this.LinkTitle = LinkTitle
        this.LinkContent = LinkContent
        this.LinkFavicon = LinkFavicon
    }
}