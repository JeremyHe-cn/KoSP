package me.alzz.kosp

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 继续此类快速构建 SharePreferences
 * Created by jeremyhe on 2017/11/4.
 */
abstract class KoSharePrefs(context : Context) {

    private val sp : SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    protected abstract val PREFS_FILE_NAME: String

    protected inner class Preference<T>(val name : String, private val default : T) :
            ReadWriteProperty<Any, T> {

        constructor(default: T) : this("", default)

        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            val key = if (name.isEmpty()) property.name else name
            with(sp) {
                val res : Any = when (default) {
                    is Int -> getInt(key, default)
                    is Long -> getLong(key, default)
                    is Float -> getFloat(key, default)
                    is Boolean -> getBoolean(key, default)
                    is String -> getString(key, default)
                    else -> throw UnsupportedOperationException("can't save this type into share preferences")
                }

                @Suppress("UNCHECKED_CAST")
                return res as T
            }
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            val key = if (name.isEmpty()) property.name else name
            with(sp.edit()) {
                when (value) {
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    is String -> putString(key, value)
                    else -> throw UnsupportedOperationException("can't save this type into share preferences")
                }
                apply()
            }
        }

    }
}