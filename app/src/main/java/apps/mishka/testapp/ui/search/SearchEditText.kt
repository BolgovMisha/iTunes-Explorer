package apps.mishka.testapp.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.widget.EditText

class SearchEditText(context: Context, attributeSet: AttributeSet): EditText(context, attributeSet) {
    var onBackPressedListener: OnBackPressedListener? = null

//    Pass on back key click to fragment when keyboard is open
    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KEYCODE_BACK && onBackPressedListener != null){
            onBackPressedListener?.onBackPressed()
            return true
        }

        return super.onKeyPreIme(keyCode, event)
    }

    interface OnBackPressedListener{
        fun onBackPressed()
    }
}