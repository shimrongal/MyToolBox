package dev.gs.mytoolbox

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import com.google.firebase.FirebaseApp
import dev.gs.mytoolbox.di.utils.SharedPrefManger

class MyToolBooApp : Application() {

    companion object {
        const val TAG = "MyToolBooApp"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "MyToolBooApp onCreate")
        SharedPrefManger.init(this)
        FirebaseApp.initializeApp(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        super.onLowMemory()
        Log.i(TAG, "MyToolBooApp onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> {
                // TODO: release unneeded app resources
                Log.d("Memory", "Running low on memory")
            }

            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                // TODO: Handle critical memory pressure - release all resources that are not currently in use.
                Log.e("Memory", "Critical memory level!")
            }

            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                //TODO: Release everything, The system is about to kill the app.
                Log.e("Memory", "System will likely kill the app.")
            }

            else -> {
                //TODO: Handle other levels
                Log.d("Memory", "Trim memory level: $level")
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.i(TAG, "MyToolBooApp onTerminate Good Bye")
    }

}