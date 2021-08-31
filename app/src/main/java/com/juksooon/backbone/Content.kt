package com.juksooon.backbone

//글 내용 클래스
class Content {
    var WriteID: String = ""
    var ContentID:Int = -1
    var content:String = ""
    var Image: ByteArray? = null
    var link: String = ""
    
    //검색 내용 띄울 때 필요한 해당 글 제목
    var WritingTitle:String = ""

    var Question: String = ""
    var QuestionID: Int = -1

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

    constructor(WriteID:String, ContentID:Int, content:String, Image:ByteArray?)
    {
        this.WriteID = WriteID
        this.ContentID = ContentID
        this.content = content
        this.Image = Image
    }

    constructor(WriteID:String, ContentID:Int, content:String, link:String)
    {
        this.WriteID = WriteID
        this.ContentID = ContentID
        this.content = content
        this.link = link
    }
    constructor(WriteID:String?, ContentID:Int?, content:String?, Image: ByteArray?, link:String?)
    {
        if (WriteID != null) {
            this.WriteID = WriteID
        }
        if (ContentID != null) {
            this.ContentID = ContentID
        }
        if (content != null) {
            this.content = content
        }
        this.Image = Image
        if (link != null) {
            this.link = link
        }
    }
    constructor(WriteID:String, content:String?, Image: ByteArray?, link:String?)
    {
        this.WriteID = WriteID
        if (content != null) {
            this.content = content
        }
        this.Image = Image
        if (link != null) {
            this.link = link
        }
    }
}