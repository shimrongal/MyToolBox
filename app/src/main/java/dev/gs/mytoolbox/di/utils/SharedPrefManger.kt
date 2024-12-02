package dev.gs.mytoolbox.di.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

object SharedPrefManger {

    private const val PREFERENCE_NAME = "ParentToolBoxPref"
    lateinit var sharedPrefManger: SharedPreferences

    fun init(context: Context) {
        sharedPrefManger = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    inline fun <reified T> getPreference(name: String, defaultValue: T): T {
        return when (T::class) {
            Boolean::class -> sharedPrefManger.getBoolean(
                name,
                defaultValue as? Boolean ?: false
            ) as T

            String::class -> sharedPrefManger.getString(name, defaultValue as? String) as T
            Int::class -> sharedPrefManger.getInt(name, defaultValue as? Int ?: 0) as T
            Long::class -> sharedPrefManger.getLong(name, defaultValue as? Long ?: 0L) as T
            Float::class -> sharedPrefManger.getFloat(name, defaultValue as? Float ?: 0F) as T
            else -> throw IllegalArgumentException("Unsupported type ${T::class.java}")

        }
    }

    @SuppressLint("CommitPrefEdits")
    inline fun <reified T> putPreference(name: String, value: T) {
        sharedPrefManger.edit().apply {
            when (T::class) {
                Boolean::class -> putBoolean(name, value as Boolean).apply()
                String::class -> putString(name, value as String).apply()
                Int::class -> putInt(name, value as Int).apply()
                Long::class -> putLong(name, value as Long).apply()
                Float::class -> putFloat(name, value as Float).apply()
                else -> throw IllegalArgumentException("Unspported type ${T::class.java}")
            }
        }
    }
}