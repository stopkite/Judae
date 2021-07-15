package com.example.backbone

//글 내용 클래스
class Content {
    var WriteID: String = ""
    var ContentID:Int = -1
    var content:String = ""
    
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
}