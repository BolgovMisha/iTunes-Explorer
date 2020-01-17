package apps.mishka.testapp.util

import android.content.Context
import android.net.ConnectivityManager

class ConnectionStateUtil(val context: Context) {
    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}
