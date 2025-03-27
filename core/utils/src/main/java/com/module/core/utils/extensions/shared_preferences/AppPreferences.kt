package com.module.core.utils.extensions.shared_preferences

import javax.inject.Singleton

@Singleton
interface AppPreferences {

    fun put(key: String, value: String)

    fun get(key: String, defaultValue: String): String

    fun put(key: String, value: Int)

    fun get(key: String, defaultValue: Int): Int

    fun put(key: String, value: Long)

    fun get(key: String, defaultValue: Long): Long

    fun put(key: String, value: Double)

    fun get(key: String, defaultValue: Double): Double

    fun put(key: String, value: Float)

    fun get(key: String, defaultValue: Float): Float

    fun put(key: String, value: Boolean)

    fun get(key: String, defaultValue: Boolean): Boolean
}