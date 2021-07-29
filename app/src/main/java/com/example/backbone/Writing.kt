package com.example.backbone

import android.graphics.drawable.Drawable
import android.view.View

//글 클래스
class Writing {
    var WriteID: Int = -1
    var content:String = ""
    var Title: String=""
    var Img: ByteArray? = null
    var linkLayout: View? = null
    var linkTitle:String = ""
    var linkUri:String = ""
    var linkIcon: ByteArray? = null
    var linkImg: ByteArray? = null
    var Date: String=""
    var Category:String=""
    var Question:Int = 0

    //기본 생성자 - 단일한 데이터만 필요할 때 사용할 예정
    constructor()
    {
        
    }
    
    //생성자
    constructor(WriteID:Int, content:String, Title: String, Date:String, Category: String)
    {
        this.WriteID = WriteID
        this.content = content
        this.Title = Title
        this.Date = Date
        this.Category = Category
    }
    
}