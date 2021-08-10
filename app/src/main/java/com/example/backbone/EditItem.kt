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
//onActivityCalled: 수정의 경우 대답 밑 버튼을 눌러서 추가된 것이면 false, 액티비티에 리스너가 있는 밑에 바에서 질문을 추가 버튼을 눌러서 추가된 것이면 true
data class EditloadQuestionData(
        override var id: String, var qTitle: String?, var aImg: Bitmap?,
        var linkLayout: View?, var linkTitle:String?, var linkContent: String?, var linkUri:String?, var linkIcon: Bitmap?,
        var aTxt: String?,var addAnswer: ImageButton?, var qImgAddBtn:ImageButton?, var qLinkAddBtn:ImageButton?, var Date:String?, var ColorChanged:Boolean?, var onActivityCalled:Boolean?):EditItem
data class EditloadContentData(override var id: Int, var contentImg: Bitmap?,
                               var linkInsertTxt: EditText?, var linkInsertBtn:Button?,
                               var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Bitmap?,
                               var docContent: String?, var qImgAddBtn:ImageButton?, var qLinkAddBtn:ImageButton?):EditItem
