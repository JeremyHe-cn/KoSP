package me.alzz.kosp

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.orhanobut.hawk.Hawk
import com.orhanobut.hawk.SharedPreferencesStorage
import me.alzz.kosp.hawk.KeyStoreEncryption
import kotlin.jvm.internal.CallableReference
import kotlin.jvm.internal.MutablePropertyReference0
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 继承此类快速构建 SharePreferences
 * Created by jeremyhe on 2017/11/4.
 */
abstract class KoSharePrefs(context : Context, useEncrypt: Boolean = false) {

    companion object {
        internal val preferenceMap = mutableMapOf<String, Preference<*>>()
    }

    private val sp : SharedPreferences by lazy {
        val sp = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        if (useEncrypt) {
            Hawk.init(context)
                    .setEncryption(KeyStoreEncryption(context, PREFS_FILE_NAME))
                    .setStorage(SharedPreferencesStorage(sp))
                    .build()
        }
        sp
    }

    abstract val PREFS_FILE_NAME: String

    protected fun int(default: Int = 0, name: String = "", encrypt: Boolean = false) = Preference(name, default, encrypt)
    protected fun long(default: Long = 0, name: String = "", encrypt: Boolean = false) = Preference(name, default, encrypt)
    protected fun float(default: Float = 0f, name: String = "", encrypt: Boolean = false) = Preference(name, default, encrypt)
    protected fun string(default: String = "", name: String = "", encrypt: Boolean = false) = Preference(name, default, encrypt)
    protected fun boolean(default: Boolean = false, name: String = "", encrypt: Boolean = false) = Preference(name, default, encrypt)

    protected fun <T> preference(default: T, name: String = "", separator: String = "_", postfixMode: Boolean = false): Preference<Preference<T>> {
        val pref = Preference(name, default)
        pref.separator = separator
        pref.postfixMode = postfixMode
        return Preference(pref)
    }

    inner class Preference<T>(internal var name : String, private val default : T, private val encrypt: Boolean = false) :
            ReadWriteProperty<Any?, T> {

        internal var separator = "_"
        internal var postfixMode = false

        private val prefKey: String
            get() = if (name.isEmpty()) property.name else name

        private lateinit var property: KProperty<*>

        internal val notify by lazy {
            val liveData = MutableLiveData<T>()
            liveData.postValue(getValue(null, property))
            liveData
        }

        constructor(default: T) : this("", default)

        operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): Preference<T> {
            property = prop
            if (default is Preference<*>) {
                default.property = property
            } else {
                val key = "$PREFS_FILE_NAME#${prop.name}"
                preferenceMap[key] = this
            }

            return this
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            if (encrypt) {
                val bytes = prefKey.toByteArray().map { it.plus(PREFS_FILE_NAME.length).toByte() }.toByteArray()
                return Hawk.get(Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP)) ?: default
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
            if (encrypt) {
                val bytes = prefKey.toByteArray().map { it.plus(PREFS_FILE_NAME.length).toByte() }.toByteArray()
                Hawk.put(Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP), value)
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
                apply()
            }

            notify.postValue(value)
        }

        operator fun get(key: String): T {
            val name = if (this.name.isEmpty()) property.name else this.name
            val prefName = if (postfixMode) {
                "$name$separator$key"
            } else {
                "$key$separator$name"
            }

            val pref by Preference(prefName, default, encrypt)
            return pref
        }

        operator fun set(key: String, value: T) {
            val name = if (this.name.isEmpty()) property.name else this.name
            val prefName = if (postfixMode) {
                "$name$separator$key"
            } else {
                "$key$separator$name"
            }

            var pref by Preference(prefName, default, encrypt)
            pref = value
        }
    }
}

fun <T> KProperty<T>.observe(owner: LifecycleOwner, block: (T)->Unit) {
    val prefFileName = ((this as? CallableReference)?.boundReceiver as? KoSharePrefs)?.PREFS_FILE_NAME ?: return
    val key = "$prefFileName#$name"
    val data = KoSharePrefs.preferenceMap[key]?.notify as? MutableLiveData<T> ?: return
    data.observe(owner, Observer {
        it ?: return@Observer
        block(it)
    })
}