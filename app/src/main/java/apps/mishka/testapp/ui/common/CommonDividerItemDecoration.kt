package apps.mishka.testapp.ui.common

import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import apps.mishka.testapp.R

class CommonDividerItemDecoration {
    companion object {
        fun getInstance(context: Context): DividerItemDecoration {
            val decoration = DividerItemDecoration(context, LinearLayout.VERTICAL)

            val inset = 30
            val divider = context.resources.getDrawable(R.drawable.divider)
            val insetDrawable = InsetDrawable(divider, inset, 0, inset, 0)
            decoration.setDrawable(insetDrawable)

            return decoration
        }


    }
}