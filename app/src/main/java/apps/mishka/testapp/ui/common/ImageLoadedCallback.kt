package apps.mishka.testapp.ui.common

import android.widget.ImageView
import com.squareup.picasso.Callback
import java.lang.Exception

class ImageLoadedCallback (private val imageView: ImageView): Callback {
    override fun onSuccess() {
        imageView.animate().scaleX(0.8f).scaleY(0.9f).setDuration(0).start()
        imageView.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
    }

    override fun onError(e: Exception?) {
    }
}