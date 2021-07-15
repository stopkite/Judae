package com.example.backbone

//대답 클래스
class Answer {
    var Question: String = ""
    var AnswerID: Int = -1
    var Content: String = ""

    //기본 생성자
    constructor()
    {

    }

    constructor(Question:String, AnswerID:Int, Content:String)
    {
        this.Question = Question
        this.AnswerID = AnswerID
        this.Content = Content
    }
}