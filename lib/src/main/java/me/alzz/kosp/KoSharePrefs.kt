package me.alzz.kosp

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 继承此类快速构建 SharePreferences
 * Created by jeremyhe on 2017/11/4.
 */
abstract class KoSharePrefs(context : Context) {

    private val sp : SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    protected abstract val PREFS_FILE_NAME: String

    protected fun int(default: Int = 0, name: String = "") = Preference(name, default)
    protected fun long(default: Long = 0, name: String = "") = Preference(name, default)
    protected fun float(default: Float = 0f, name: String = "") = Preference(name, default)
    protected fun string(default: String = "", name: String = "") = Preference(name, default)
    protected fun boolean(default: Boolean = false, name: String = "") = Preference(name, default)
    protected fun <T> preference(default: T, name: String = "", separator: String = "_", postfixMode: Boolean = false): Preference<Preference<T>> {
        val pref = Preference(name, default)
        pref.separator = separator
        pref.postfixMode = postfixMode
        return Preference(pref)
    }

    inner class Preference<T>(internal var name : String, private val default : T) :
            ReadWriteProperty<Any?, T> {

        internal var separator = "_"
        internal var postfixMode = false

        constructor(default: T) : this("", default)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val key = if (name.isEmpty()) property.name else name
            with(sp) {
                val res: Any = when (default) {
                    is Int -> getInt(key, default)
                    is Long -> getLong(key, default)
                    is Float -> getFloat(key, default)
                    is Boolean -> getBoolean(key, default)
                    is String -> getString(key, default)
                    is Preference<*> -> {
                        if (default.name.isEmpty()) {
                            default.name = name
                        }

                        default.property = property
                        return default
                    }
                    else -> throw UnsupportedOperationException("can't save this type into share preferences")
                }

                @Suppress("UNCHECKED_CAST")
                return res as T
            }
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
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

        private lateinit var property: KProperty<*>

        operator fun get(key: String): T {
            val name = if (this.name.isEmpty()) property.name else this.name
            val prefName = if (postfixMode) {
                "$name$separator$key"
            } else {
                "$key$separator$name"
            }

            val pref by Preference(prefName, default)
            return pref
        }

        operator fun set(key: String, value: T) {
            val name = if (this.name.isEmpty()) property.name else this.name
            val prefName = if (postfixMode) {
                "$name$separator$key"
            } else {
                "$key$separator$name"
            }

            var pref by Preference(prefName, default)
            pref = value
        }
    }
}