package com.example.backbone

import android.graphics.Bitmap

//대답 클래스
class Answer {
    var QuestionID: String = ""
    var AnswerID: Int = -1
    var Content: String = ""
    var Date:String = ""

    //이미지
    var Image: ByteArray? = null
    //링크
    var Link: String = ""

    //기본 생성자
    constructor()
    {

    }
    constructor(Question:String, AnswerID:Int, Content:String)
    {
        this.QuestionID = Question
        this.AnswerID = AnswerID
        this.Content = Content
    }
}