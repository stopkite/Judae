package com.example.backbone

import android.graphics.Bitmap
import java.sql.Blob

//글 내용 클래스
class Content {
    var WriteID: String = ""
    var ContentID:Int = -1
    var content:String = ""
    var Image: Bitmap? = null
    
    //검색 내용 띄울 때 필요한 해당 글 제목
    var WritingTitle:String = ""
    //기본 생성자
    constructor()
    {
        
    }
    
    constructor(WriteID:String, ContentID:Int, content:String)
    {
        this.WriteID = WriteID
        this.ContentID = ContentID
        this.content = content
    }

    constructor(WriteID:String, ContentID:Int, content:String, Image:Bitmap)
    {
        this.WriteID = WriteID
        this.ContentID = ContentID
        this.content = content
        this.Image = Image
    }
}