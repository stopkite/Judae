package com.example.backbone

import java.sql.Blob

//질문 클래스
class Question {
    var WritingID: String = ""
    var ContentID: String = ""
    var QuestionID: Int = -1
    var Content: String = ""
    var Img: ByteArray? = null
    var linkTitle:String = ""
    var linkUri:String = ""
    var linkIcon: ByteArray? = null
    var linkImg: ByteArray? = null

    //MyQuestionActivity에 띄우기 위해 필요한 해당 질문이 속한 글 제목
    var WritingTitle: String = ""
    //lateinit var Image: Blob

    //기본 생성자
    constructor()
    {

    }

    constructor(WritingID:String, ContentID:String, QuestionID:Int, Content:String)
    {
        this.ContentID = ContentID
        this.QuestionID = QuestionID
        this.Content = Content
        this.WritingID = WritingID
    }
}
