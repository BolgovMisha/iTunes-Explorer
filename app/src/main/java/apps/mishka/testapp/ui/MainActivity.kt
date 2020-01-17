package apps.mishka.testapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import apps.mishka.testapp.R
import apps.mishka.testapp.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onBackPressed() {
        val searchFragment = supportFragmentManager.findFragmentByTag(SearchFragment.TAG)
        if(searchFragment != null){
            (searchFragment as SearchFragment).onBackPressed()
        }

        super.onBackPressed()
    }
}
