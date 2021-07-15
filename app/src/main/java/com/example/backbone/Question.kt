package com.example.backbone

import java.sql.Blob

//질문 클래스
class Question {
    var ContentID: String = ""
    var QuestionID: Int = -1
    var Content: String = ""
    //lateinit var Image: Blob

    //기본 생성자
    constructor()
    {

    }

    constructor(ContentID:String, QuestionID:Int, Content:String)
    {
        this.ContentID = ContentID
        this.QuestionID = QuestionID
        this.Content = Content
    }
}
