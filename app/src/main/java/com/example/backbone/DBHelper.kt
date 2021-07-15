package com.example.backbone

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.ResultSet

//sql문으로 DB 연결시켜주는 클래스

//Backbone.db 파일을 찾도록 하고 없으면 새로 생성시켜주기.
class DBHelper(context: Context): SQLiteOpenHelper(context, "Backbone.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        /*
                Log.d("태그", "실행되냐")
     //대답 테이블
        db!!.execSQL("CREATE TABLE Answer (QustionID TEXT NOT NULL,AnswerID INTEGER NOT NULL,Content TEXT,PRIMARY KEY(AnswerID));")

        //글 테이블
        db!!.execSQL("CREATE TABLE \"Writing\" (\n" +
                "\t\"WriteID\"\tINTEGER,\n" +
                "\t\"Content\"\tTEXT,\n" +
                "\t\"Title\"\tTEXT NOT NULL,\n" +
                "\t\"Date\"\tTEXT NOT NULL,\n" +
                "\t\"Category\"\tTEXT NOT NULL,\n" +
                "\tPRIMARY KEY(\"WriteID\" AUTOINCREMENT)\n" +
                ")")

        //글 테이블
        db!!.execSQL("CREATE TABLE \"Question\" (\n" +
                "                \"WritingID\"\tTEXT,\n" +
                "                \"ContentID\"\tTEXT NOT NULL,\n" +
                "                \"QuestionID\"\tINTEGER NOT NULL UNIQUE,\n" +
                "                \"Content\"\tTEXT NOT NULL,\n" +
                "                \"Image\"\tBLOB,\n" +
                "                PRIMARY KEY(\"QuestionID\" AUTOINCREMENT)\n" +
                "        );")


         */


    }

    //버전을 업그레이드 하면 실행 -> 기존에 있던 테이블을 삭제한 후 실행.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //DB 삭제 후 다시 생성
        db!!.execSQL("DROP TABLE IF EXISTS Backbone")
        onCreate(db)
    }

    fun getCount():Int
    {
        var count:Int
        var db = this.readableDatabase

        var cursor2: Cursor

        cursor2 =db.rawQuery("SELECT COUNT(*) FROM Writing;", null)

        cursor2.moveToFirst()
        count = cursor2.getInt(0)

        return count
        db.close()
    }

    //홈 화면
    //글 객체에 제목, 카테고리 이름, DATE, 해당 글에 저장된 Question갯수 출력
    fun getWriting(): Array<Writing>
    {
        Log.d("태그", "getWriting 실행되냐")
        //db읽어올 준비
        var db = this.readableDatabase

        var cursor2: Cursor

        var anyArray = arrayOf<Writing>()



        var cursor: Cursor = db.rawQuery("SELECT*FROM Writing;", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            //빈 객체 생성
            var writing:Writing = Writing()

            writing.WriteID = cursor.getInt(0)
            writing.content = cursor.getString(1)
            writing.Title =  cursor.getString(2)
            writing.Date =  cursor.getString(3)
            writing.Category =  cursor.getString(4)

            cursor2 =db.rawQuery("SELECT COUNT(*) FROM Question WHERE WritingID = '${writing.WriteID}';", null)
            while(cursor2.moveToNext())
            {
                writing.Question = cursor2.getInt(0)
            }

            Log.d("태그 글 객체 제목", "${writing.Title}")
            anyArray+=writing

        }

        for(i in 0..(anyArray.size-1))
        {
            Log.d("홈엑티비티로 넘기는 값", "제목 ${anyArray[i].Title}")

        }

        return anyArray

        // 디비 닫기
        db.close()
    }

}