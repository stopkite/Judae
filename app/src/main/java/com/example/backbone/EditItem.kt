package com.example.backbone

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton

interface EditItem {
    abstract val id: Any
}

data class EditQuestionData(
    override var id: Int, var qTitle: String?, var aImg: Bitmap?,
    var linkInsertTxt: String?, var linkInsertBtn: Button?,
    var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Bitmap?,
    var aTxt: String?, var addAnswer: ImageButton?, var qImgAddBtn: ImageButton?, var qLinkAddBtn: ImageButton?, var Date:String?): EditItem

data class EditContentData(
    override var id: Int, var contentImg: Bitmap?,
    var linkInsertTxt: EditText?, var linkInsertBtn: Button?,
    var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Bitmap?,
    var docContent:String?, var qImgAddBtn: ImageButton?, var qLinkAddBtn: ImageButton?): EditItem


data class saveQuestionData2(override var id: Int, var qTitle: String?, var aImg: Bitmap?,
                            var linkInsertTxt: EditText?, var linkInsertBtn: Button?,
                            var linkLayout: View?, var linkTitle:String?, var linkUri:String?, var linkIcon: Bitmap?,
                            var aTxt: String?, var addAnswer: ImageButton?, var qImgAddBtn:ImageButton?, var qLinkAddBtn:ImageButton?):EditItem

data class saveContentData2(override var id: Int, var contentImg: Bitmap?,
                           var linkInsertTxt:String?, var linkInsertBtn:Button?,
                           var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Bitmap?,
                           var docContent:String?, var qImgAddBtn:ImageButton?, var qLinkAddBtn:ImageButton?): EditItem




//onActivityCalled: 수정의 경우 대답 밑 버튼을 눌러서 추가된 것이면 false, 액티비티에 리스너가 있는 밑에 바에서 질문을 추가 버튼을 눌러서 추가된 것이면 true
data class loadQuestionData2(
    override var id: String, var qTitle: String?, var aImg: ByteArray?,
    var linkLayout: View?, var linkTitle:String?, var linkContent: String?, var linkUri:String?, var linkIcon: Bitmap?, var linkImg: Drawable?,
    var aTxt: String?, var Date:String?, var ColorChanged:Boolean?, var onActivityCalled:Boolean?):EditItem
data class loadContentData2(override var id: Int, var contentImg: ByteArray?,
                           var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Bitmap?, var linkImg: Drawable?,
                           var docContent: String?):EditItem