package dev.gs.mytoolbox

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dev.gs.mytoolbox.di.utils.SharedPrefManger
import dev.gs.mytoolbox.di.utils.SharedPrefManger.IS_USER_LOGGED_IN
import dev.gs.mytoolbox.ui.login.FragmentLogin
import dev.gs.mytoolbox.ui.mytoolbox.FragmentMyToolBox


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main_activity)
        Log.i(TAG, "onCreate")

        // Check if the user is logged in
        if (SharedPrefManger.getPreference(IS_USER_LOGGED_IN, false)) {
            supportFragmentManager.beginTransaction().replace(
                R.id.layoutMainActivity,
                FragmentMyToolBox.newInstance(),
                "FragmentMyToolBox"
            ).commit()

        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.layoutMainActivity, FragmentLogin.newInstance(), "LoginFragment")
                .commit()
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.i(TAG, "onTrimMemory memory level is: $level")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
}