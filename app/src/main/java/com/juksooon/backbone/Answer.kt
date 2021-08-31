package com.juksooon.backbone

//대답 클래스
class Answer {
    var Question:String = ""
    var QuestionID: String = ""
    var AnswerID: Int = -1
    var Content: String = ""
    var Date:String = ""

    //이미지
    var Image: ByteArray? = null
    //링크
    var Link: String? = null

    //기본 생성자
    constructor()
    {

    }
    constructor(QuestionID:String, Content:String, Date: String, Link: String)
    {
        this.QuestionID = QuestionID
        this.AnswerID = AnswerID
        this.Date = Date
        this.Image = Image
        this.Link = Link
        this.Content = Content
    }
    constructor(QuestionID:String, Content: String?, Date: String?, Image:ByteArray?, Link: String?)
    {
        this.QuestionID = QuestionID
        this.AnswerID = AnswerID
        if (Date != null) {
            this.Date = Date
        }
        this.Image = Image
        if (Link != null) {
            this.Link = Link
        }
        if (Content != null) {
            this.Content = Content
        }
    }
    constructor(Question:String, AnswerID:Int, Content:String)
    {
        this.QuestionID = Question
        this.AnswerID = AnswerID
        this.Content = Content
    }
}