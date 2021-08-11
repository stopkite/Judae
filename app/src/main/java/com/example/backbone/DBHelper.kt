package com.example.backbone

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import android.graphics.Bitmap
import android.util.Log
import java.sql.Types.NULL

//sql문으로 DB 연결시켜주는 클래스

//Backbone.db 파일을 찾도록 하고 없으면 새로 생성시켜주기.
class DBHelper(context: Context): SQLiteOpenHelper(context, "Backbone.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
    //추후 수정 예정
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

    //홈 화면
    //HomeActivity
    //cateName인자의 값에 따라 검색하는 sql 내용이 달라짐
    //글 객체에 제목, 카테고리 이름, DATE, 해당 글에 저장된 Question갯수 출력
    fun getCateWriting(cateName: String): Array<Writing>
    {
        //db읽어올 준비
        var db = this.readableDatabase

        var cursor: Cursor
        var cursor2: Cursor

        var anyArray = arrayOf<Writing>()

        if(cateName.equals("전체")) {
            //매개변수로 받아온 내용 없으면 전체 카테고리로 취급
            //글 목록 테이블에서 date 순서(최근순)으로 받아오는 sql문
            cursor = db.rawQuery("select * from Writing order by date DESC;", null)
        }else{
            //매개변수로 받아온 내용을 카테고리로 가진 Writing 객체 불러오기
            //글 목록 테이블에서 date 순서(최근순)으로 받아오는 sql문
            cursor = db.rawQuery("select * from Writing WHERE Category = '"+cateName+"' order by date DESC;", null)
        }

        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            //빈 객체 생성
            var writing:Writing = Writing()

            writing.WriteID = cursor.getInt(0)
            writing.Title = cursor.getString(1)
            writing. Date=  cursor.getString(2)
            writing. Category =  cursor.getString(3)

            cursor2 =db.rawQuery("SELECT COUNT(*) FROM Question WHERE WritingID = '${writing.WriteID}';", null)
            while(cursor2.moveToNext())
            {
                writing.Question = cursor2.getInt(0)
            }
            anyArray+=writing

        }

        return anyArray

        // 디비 닫기
        db.close()
    }
    
    // 잠금 화면
    // 사용자에게 비밀번호 관리하는 DB 있는지 Boolean으로 반환하는 함수
    //User 테이블이 존재하면 true반환 없으면 false 반환
    fun PWisExist()
    {
        var db= this.readableDatabase

        var cursor: Cursor = db.rawQuery("SELECT EXISTS( SELECT 1 FROM User)As flag;", null)

        // 디비 닫기
        db.close()
    }
    
    //잠금 화면
    //사용자 User 테이블에 저장되어있는 사용자 디바이스 비밀번호 정보 불러오기
    fun getUserPassWord(): String
    {
        var db = this.readableDatabase

        var cursor: Cursor = db.rawQuery("SELECT*FROM USER;", null)

        cursor.moveToFirst()

        var Userpw: String = cursor.getString(0)

        return Userpw
    }

    //카테고리 화면
    //BottomFragmentList.kt
    //WritingActivity.kt
    //카테고리 내용 받아오기
    fun getCategory(): ArrayList<String>
    {
        //db읽어올 준비
        var db = this.readableDatabase

        var anyArray = ArrayList<String>()

        var cursor: Cursor = db.rawQuery("SELECT*FROM Category;", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            var category:String = cursor.getString(0)
            anyArray.add(category)

        }
        return anyArray

        // 디비 닫기
        db.close()
    }

    //카테고리 화면
    //BottomFragmentAdd.kt
    //BottomFragmentEdit.kt
    //카테고리 중복 내용 있는지 확인하기
    fun isExistCategory(cateName: String): Int {
        var db= this.readableDatabase

        var cursor: Cursor = db.rawQuery("SELECT EXISTS( SELECT*FROM Category where CategoryName = '"+cateName+"')As flag;", null)

        cursor.moveToFirst()

        return cursor.getInt(0)

        // 디비 닫기
        db.close()
    }

    //카테고리 화면
    //BottomFragmentAdd.kt
    //카테고리 추가하기
    fun addCategory(category: String)
    {
        var db = this.writableDatabase
        db.execSQL("INSERT INTO Category VALUES ('" + category + "');")

        db.close()
    }

    //카테고리 화면
    //BottomFragmentEdit.kt
    //카테고리 수정하기
    fun editCategory(Before:String, After:String)
    {
        var db = this.writableDatabase

        //카테고리 테이블에서 수정
        db.execSQL("UPDATE Category SET CategoryName = '"+After+"' WHERE CategoryName = '"+Before+"';")

        //글 테이블에서 수정
        db.execSQL("UPDATE Writing SET Category = '"+After+"' WHERE Category = '"+Before+"';")

        db.close()
    }

    //카테고리 화면
    //BottomFragmentEdit.kt
    //카테고리 삭제하기
    fun deleteCategory(cateName: String)
    {
        var db = this.writableDatabase
        db.execSQL("DELETE FROM Category WHERE CategoryName = '"+cateName+"';")

        db.close()
    }

    //질문 리스트 화면
    //MyQuestionActivity.kt
    fun getQuestion(): ArrayList<Question>
    {
        //db읽어올 준비
        var db = this.readableDatabase

        var anyArray = ArrayList<Question>()

        var cursor: Cursor = db.rawQuery("SELECT*FROM Question;", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            //클래스 생성에 필요한 내용 받아오기
            var WritingID:String = cursor.getString(0)
            var ContentID:String = cursor.getString(1)
            var QuestionID: Int = cursor.getInt(2)
            var Content:String = cursor.getString(3)
            var q:Question = Question(WritingID, ContentID, QuestionID.toString(), Content)

            var cursor2:Cursor =db.rawQuery("SELECT*FROM Writing WHERE WriteID = ${q.WritingID};", null)
            while(cursor2.moveToNext())
            {
                q.WritingTitle = cursor2.getString(2)
            }

            anyArray.add(q)
        }

        return anyArray

        // 디비 닫기
        db.close()
    }

    //비밀번호 생성
    //LockedScreenActivity
    fun createPassword(PassWord: String)
    {
        var db = this.writableDatabase

        try{
            //해당 테이블이 존재한다면?? catch문 들어가지 않고 가기
            PWisExist()
        }catch (e: Exception)
        {
            //해당 테이블이 존재하지 않는다면?? -> 해당 테이블 새로 생성하기.
            db.execSQL("CREATE TABLE User (PassWord TEXT);")
        }

        db.execSQL("INSERT INTO User VALUES ('" + PassWord + "');")
        db.close()
    }

    //비밀번호 삭제
    //LockedScreenActivity
    fun removePassword()
    {
        var db = this.writableDatabase

        //패스워드 정보가 담긴 User 테이블을 삭제하기
        db.execSQL("DROP TABLE User;")
        db.close()
    }

    //비밀번호 변경
    //LokedScreenOnceActivity
    fun updatePassword(last:String, new:String)
    {
        var db = this.writableDatabase

        //유저 비밀번호 테이블에서 수정
        db.execSQL("UPDATE User SET PassWord = '"+new+"' WHERE PassWord = '"+last+"';")

        db.close()
    }

    //질문 검색
    //SearchActivity
    fun searchQuestion(key: String): ArrayList<Question>
    {
        //db읽어올 준비
        var db = this.readableDatabase

        // 질문 리스트를 담기 위한 배열 생성
        var qList = ArrayList<Question>()

        var cursor: Cursor = db.rawQuery( "SELECT*FROM Question WHERE Content like '%"+key+"%'", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            //클래스 생성에 필요한 내용 받아오기 - 받아온 검색 값을 객체로 받아오기
            var WritingID: String = cursor.getString(0)
            var ContentID: String = cursor.getString(1)
            var QuestionID: Int = cursor.getInt(2)
            var Content: String = cursor.getString(3)
            var q: Question = Question(WritingID, ContentID, QuestionID.toString(), Content)
            
            // 검색한 질문 객체에 해당 되는 글의 제목 받아오기
            var cursor2:Cursor =db.rawQuery("SELECT*FROM Writing WHERE WriteID = ${q.WritingID};", null)
            while(cursor2.moveToNext())
            {
                q.WritingTitle = cursor2.getString(2)
            }

            qList.add(q)
        }

        return qList

        db.close()
    }

    //글 검색
    //SearchActivity
    fun searchWriting(key: String): ArrayList<Writing>
    {
        //db읽어올 준비
        var db = this.readableDatabase


        var wIDList = ArrayList<String>()

        var cursor: Cursor = db.rawQuery( "SELECT*FROM Content WHERE Content like '%"+key+"%';", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            //클래스 생성에 필요한 내용 받아오기 - 받아온 검색 값을 객체로 받아오기
            var WriteID: String = cursor.getString(0)
            wIDList.add(WriteID)
        }

        var cursor2: Cursor = db.rawQuery( "SELECT*FROM Writing WHERE Title like '%"+key+"%';", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor2.moveToNext()) {
                //클래스 생성에 필요한 내용 받아오기 - 받아온 검색 값을 객체로 받아오기
                var WriteID: String = cursor2.getString(0)
                wIDList.add(WriteID)
        }

        wIDList.distinct()

        // 글 리스트를 담기 위한 배열 생성
        var wList = ArrayList<Writing>()

        for (i in 0..(wIDList.distinct().size - 1))
        {
            // 검색한 질문 객체에 해당 되는 글의 제목 받아오기
            var cursor3:Cursor =db.rawQuery("SELECT*FROM Writing WHERE WriteID = "+wIDList.distinct()[i].toString()+";", null)
            while(cursor3.moveToNext())
            {
                //빈 객체 생성
                var writing:Writing = Writing()

                writing.WriteID = cursor3.getInt(0)
                writing.Title = cursor3.getString(1)
                writing.Date =  cursor3.getString(2)
                writing.Category =  cursor3.getString(3)

                wList.add(writing)
            }
        }

        return wList

        db.close()

    }

    //ReadingActivity
    //글읽기 화면
    //글의 ID를 받아와 해당하는 글의 Writing 정보와 질문 내용을 받아오기.
    fun getWriting(writeID: String): ArrayList<Content>
    {
        //db읽어올 준비
        var db = this.readableDatabase

        var anyArray = ArrayList<Content>()
        //매개변수로 받아온 글 ID를 가진 내용 부분 다 불러오기
        var cursor: Cursor = db.rawQuery("select * from Content WHERE WriteID = '$writeID';", null)

        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {

            //빈 객체 생성
            var content:Content = Content()

            if(cursor!=null)
            {
                content.WriteID = cursor.getString(cursor.getColumnIndex("WriteID"))
                content.ContentID = cursor.getInt(cursor.getColumnIndex("ContentID"))
                content.content=  cursor.getString(cursor.getColumnIndex("Content"))

                Log.d("태그", "디비 cursor: ${content.content}")

                if(cursor.getBlob(cursor.getColumnIndex("Image")) != null)
                {
                    content.Image = cursor.getBlob(cursor.getColumnIndex("Image"))
                }else{
                    content.Image = null
                }
                if(cursor.getString(cursor.getColumnIndex("Link")) == null)
                {
                    content.link = ""
                }else{
                    content.link = cursor.getString(cursor.getColumnIndex("Link"))
                }
                // 검색한 질문 객체에 해당 되는 글의 제목 받아오기
                var cursor2:Cursor =db.rawQuery("SELECT*FROM Writing WHERE WriteID = ${writeID};", null)
                while(cursor2.moveToNext())
                {
                    content.WritingTitle = cursor2.getString(1)
                    //해당 content에 해당되는 질문 값을 받아오기.

                }
                anyArray.add(content)
            }
        }

        cursor.close()
        // 디비 닫기
        db.close()

        return anyArray


    }
    
    //ReadingActivity
    //글읽기 화면
    //Content에 속한 QuestionID를 각각 받아오기
    fun getQuestionID(writeID: String, contentID: String):ArrayList<Question>
    {
        var Question:String
        var QuestionID: String
        //db읽어올 준비
        var db = this.readableDatabase

        var cursor: Cursor

        var anyArray = ArrayList<Question>()

        var cursor3: Cursor = db.rawQuery( "SELECT*FROM Question WHERE WritingID = '"+writeID+"' AND ContentID = '"+contentID+"';", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor3.moveToNext()) {
            //클래스 생성에 필요한 내용 받아오기 - 받아온 검색 값을 객체로 받아오기
            Question =  cursor3.getString(3).toString()
            QuestionID = cursor3.getInt(2).toString()

            anyArray.add(Question(QuestionID, Question))
        }
        // 디비 닫기
        db.close()
        return anyArray

    }

    //ReadingActivity
    //글읽기 화면
    //Content에 속한 QuestionID를 각각 받아와서 해당하는 Answer를 찾아 객체를 만들어 보내기.
    fun getAnswer(QuestionID: String): ArrayList<Answer>
    {
        var Question:String
        var Content: String = ""
        var Date: String
        var Image: ByteArray? = null
        var Link: String
        //db읽어올 준비
        var db = this.readableDatabase

        var cursor: Cursor

        var anyArray = ArrayList<Answer>()


        //해당 질문 ID를 가진 답변들 들고 오기!
            cursor = db.rawQuery("select*from Answer WHERE QuestionID = '"+QuestionID+"';", null)

            //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
            while (cursor.moveToNext()) {
                Content = cursor.getString(1)
                Date=  cursor.getString(2)
                if(cursor.getBlob(3) != null)
                {
                    Image = cursor.getBlob(3)
                }
                Link = ""
                if(cursor.getString(4) != null)
                {
                    Link =  cursor.getString(4)
                }
                anyArray.add(Answer(QuestionID, Content, Date, Image, Link))
            }

        cursor.close()
        // 디비 닫기
        db.close()
        return anyArray

    }

    //WritingActivity
    //글 제목, 카테고리, 날짜를 저장하는 기능
    fun InsertWriting(writing: Writing)
    {
        var db = this.writableDatabase
        db.execSQL("INSERT INTO Writing (Title, Date, Category) values ('" + writing.Title + "', '"+writing.Date+"', '"+writing.Category+"');")

        db.close()
    }

    fun getCurrentWriteID():Int
    {
        var db = this.readableDatabase


        var cursor: Cursor = db.rawQuery("select * from Writing;", null)
        cursor.moveToLast()
        var WriteID = cursor.getInt(0)

        cursor.close()
        // 디비 닫기
        db.close()
        return WriteID
    }

    //WritingActivity
    //내용 제목, 카테고리, 날짜를 저장하는 기능
    fun InsertContent(content: Content)
    {
        var db = this.writableDatabase
        if(content.Image != null)
        {
            Log.d("태그", "사진 저장")
            var p: SQLiteStatement = db.compileStatement("INSERT INTO Content (WriteID, Content, Image, Link) VALUES (?,?, ?,?);")

            p.bindString(1, content.WriteID)
            p.bindString(2, content.content)
            p.bindBlob(3, content.Image)
            p.bindString(4, content.link)
            p.execute()
        }else{
            db.execSQL("INSERT INTO Content (WriteID, Content, Image, Link) VALUES ('" + content.WriteID + "', '"+content.content+"', NULL, '"+content.link+"');")
        }


        db.close()
    }

    fun getCurrentContentID():Int
    {
        var db = this.readableDatabase

        var cursor: Cursor = db.rawQuery("SELECT * FROM Content;", null)
        cursor.moveToLast()
        var ContentID = cursor.getInt(cursor.getColumnIndex("ContentID"))

        cursor.close()
        // 디비 닫기
        db.close()
        return ContentID
    }

    //WritingActivity
    //내용 제목, 카테고리, 날짜를 저장하는 기능
    fun InsertQuestion(question: Question)
    {
        var db = this.writableDatabase

            var p: SQLiteStatement = db.compileStatement("INSERT INTO Question (WritingID, ContentID, Content) VALUES (?,?,?);")

            p.bindString(1, question.WritingID)
            p.bindString(2, question.ContentID)
            p.bindString(3, question.Content)
            p.execute()


        db.close()
    }

    fun getCurrentQuestionID():Int
    {
        var db = this.readableDatabase

        var cursor: Cursor = db.rawQuery("select * from Question;", null)
        cursor.moveToLast()
        var QuestionID = cursor.getInt(2)

        cursor.close()
        // 디비 닫기
        db.close()

        return QuestionID
    }

    fun InsertAnswer(answer: Answer)
    {
        var db = this.writableDatabase
        if(answer.Image != null) {
            if(answer.Link == null)
            {
                var p: SQLiteStatement = db.compileStatement("INSERT INTO Answer (QuestionID, Content, Date, Image, Link) VALUES (?,?, ?, ?, NULL);")
                p.bindString(1, answer.QuestionID)
                p.bindString(2, answer.Content)
                p.bindString(3, answer.Date)
                p.bindBlob(4, answer.Image)
                p.execute()
            }else{
                Log.d("태그:", "InsertAnswer ${answer.Link}")
                var p: SQLiteStatement = db.compileStatement("INSERT INTO Answer (QuestionID, Content, Date, Image, Link) VALUES (?,?, ?, ?, ?);")
                p.bindString(1, answer.QuestionID)
                p.bindString(2, answer.Content)
                p.bindString(3, answer.Date)
                p.bindBlob(4, answer.Image)
                p.bindString(5, answer.Link)

                p.execute()
            }
        }
        else{
            Log.d("태그:", "InsertAnswer else구문")
            db.execSQL("INSERT INTO Answer (QuestionID, Content, Date, Image, Link) VALUES ('"+answer.QuestionID+"','" + answer.Content + "', '" + answer.Date + "',NULL, '" + answer.Link + "');")
        }

        db.close()
    }



     //WritingActivity
    //이미지를 올리는 DB
    //table: 어떤 테이블에 저자할 것인지? content, answer 중?
    //id: content/answer 테이블의 어느 로우에 저장할지? -> 해당하는 id를 입력하면 됨.
    /*
        fun drawImage(table: String, id: String, image:Image)
    {
        val values = ContentValues()
        values.put("image", image.image)
        //실험 중
        var db = this.writableDatabase
        db.insert("Image", null, values)
        //var p: SQLiteStatement = db.compileStatement("INSERT INTO Image VALUES (?);")
        //p.bindBlob(1, image)
        db.close()
    }


    //ReadingActivity
    //이미지를 받아오는 DB
    fun showImage(table: String, id: String):Image
    {
        //실험 중
        var db = this.readableDatabase
        var cursor = db.rawQuery("select*from Image;", null)

        cursor.moveToFirst()
        var image:ByteArray = cursor.getBlob(0)


        var Memo = Image(image)
        db.close()
        return Memo
    }
     */

}
