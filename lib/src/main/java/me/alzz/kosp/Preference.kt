package me.alzz.kosp

import android.content.SharedPreferences
import android.util.Base64
import com.orhanobut.hawk.Hawk
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 代理 pref 存储和读取
 * Created by JeremyHe on 2019-07-18.
 */
open class Preference<T>(
        protected val prefFileName: String,
        private val sp: SharedPreferences,
        internal var name: String,
        protected val default: T,
        private val encrypt: Boolean = false) : ReadWriteProperty<Any?, T> {

    internal var separator = "_"
    internal var postfixMode = false

    private val prefKey: String
        get() = if (name.isEmpty()) property.name else name

    internal lateinit var property: KProperty<*>

    constructor(prefFileName: String, sp: SharedPreferences, default: T) : this(prefFileName, sp, "", default)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        this.property = property
        if (encrypt) {
            val bytes = prefKey.toByteArray().map { it.plus(prefFileName.length).toByte() }.toByteArray()
            return Hawk.get(Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP))
                    ?: default
        }

        return getFromPref(prefKey)
    }

    private fun getFromPref(key: String): T {
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
        this.property = property
        if (encrypt) {
            val bytes = prefKey.toByteArray().map { it.plus(prefFileName.length).toByte() }.toByteArray()
            val key = Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP)
            Hawk.put(key, value)
            return
        }

        saveToPref(prefKey, value)
    }

    private fun saveToPref(key: String, value: T) {
        with(sp.edit()) {
            when (value) {
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                else -> throw UnsupportedOperationException("can't save this type into share preferences")
            }
            commit()
        }
    }

    operator fun get(key: String): T {
        val name = if (this.name.isEmpty()) property.name else this.name
        val prefName = if (postfixMode) {
            "$name$separator$key"
        } else {
            "$key$separator$name"
        }

        val pref by Preference(prefFileName, sp, prefName, default, encrypt)
        return pref
    }

    operator fun set(key: String, value: T) {
        val name = if (this.name.isEmpty()) property.name else this.name
        val prefName = if (postfixMode) {
            "$name$separator$key"
        } else {
            "$key$separator$name"
        }

        var pref by Preference(prefFileName, sp, prefName, default, encrypt)
        pref = value
    }
}