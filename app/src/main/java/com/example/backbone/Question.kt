package com.example.backbone

import java.sql.Blob

//질문 클래스
class Question {
    var WritingID: String = ""
    var ContentID: String = ""
    var QuestionID: String = ""
    var Content: String = ""

    //MyQuestionActivity에 띄우기 위해 필요한 해당 질문이 속한 글 제목
    var WritingTitle: String = ""

    var Date:String = ""
    //lateinit var Image: Blob

    //기본 생성자
    constructor()
    {

    }

    constructor(QuestionID:String, Content:String)
    {
        this.Content = Content
        this.QuestionID = QuestionID
    }
    constructor(WritingID:String, ContentID:String, QuestionID:String, Content:String)
    {
        this.ContentID = ContentID
        this.QuestionID = QuestionID
        this.Content = Content
        this.WritingID = WritingID
    }
    constructor(WritingID:String, ContentID:String,Content:String)
    {
        this.ContentID = ContentID
        this.Content = Content
        this.WritingID = WritingID
    }
}
