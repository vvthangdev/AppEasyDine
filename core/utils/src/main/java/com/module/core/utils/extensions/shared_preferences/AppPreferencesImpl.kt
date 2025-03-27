package com.module.core.utils.extensions.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import com.module.core.utils.extensions.constants.PreferenceKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppPreferences {

    private val mPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(PreferenceKey.APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun put(key: String, value: String) {
        mPrefs.edit().putString(key, value).apply()
    }

    override fun put(key: String, value: Int) {
        mPrefs.edit().putInt(key, value).apply()
    }

    override fun put(key: String, value: Long) {
        mPrefs.edit().putLong(key, value).apply()
    }

    override fun put(key: String, value: Double) {
        val longValue = java.lang.Double.doubleToLongBits(value)
        mPrefs.edit().putLong(key, longValue).apply()
    }

    override fun put(key: String, value: Float) {
        mPrefs.edit().putFloat(key, value).apply()
    }

    override fun put(key: String, value: Boolean) {
        mPrefs.edit().putBoolean(key, value).apply()
    }

    override fun get(key: String, defaultValue: String): String {
        return mPrefs.getString(key, defaultValue) ?: defaultValue
    }

    override fun get(key: String, defaultValue: Int): Int {
        return mPrefs.getInt(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Long): Long {
        return mPrefs.getLong(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Double): Double {
        val longValue = mPrefs.getLong(key, java.lang.Double.doubleToLongBits(defaultValue))
        return java.lang.Double.longBitsToDouble(longValue)
    }

    override fun get(key: String, defaultValue: Float): Float {
        return mPrefs.getFloat(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Boolean): Boolean {
        return mPrefs.getBoolean(key, defaultValue)
    }
}