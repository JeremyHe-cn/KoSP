package me.alzz.kosp

import android.content.Context
import android.content.SharedPreferences
import com.orhanobut.hawk.Hawk
import com.orhanobut.hawk.SharedPreferencesStorage
import me.alzz.kosp.hawk.KeyStoreEncryption

/**
 * 继承此类快速构建 SharePreferences
 * Created by jeremyhe on 2017/11/4.
 */
abstract class KoSharePrefs(context : Context, val prefName: String, useEncrypt: Boolean = false) {

    private val sp : SharedPreferences by lazy {
        val sp = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        if (useEncrypt) {
            Hawk.init(context)
                    .setEncryption(KeyStoreEncryption(context, prefName))
                    .setStorage(SharedPreferencesStorage(sp))
                    .build()
        }
        sp
    }

    fun int(default: Int = 0, name: String = "", encrypt: Boolean = false) = ObservablePreference(prefName, sp, name, default, encrypt)
    fun long(default: Long = 0, name: String = "", encrypt: Boolean = false) = ObservablePreference(prefName, sp, name, default, encrypt)
    fun float(default: Float = 0f, name: String = "", encrypt: Boolean = false) = ObservablePreference(prefName, sp, name, default, encrypt)
    fun string(default: String = "", name: String = "", encrypt: Boolean = false) = ObservablePreference(prefName, sp, name, default, encrypt)
    fun boolean(default: Boolean = false, name: String = "", encrypt: Boolean = false) = ObservablePreference(prefName, sp, name, default, encrypt)

    fun <T> preference(default: T, name: String = "", separator: String = "_", postfixMode: Boolean = false): Preference<ObservablePreference<T>> {
        val pref = ObservablePreference(prefName, sp, name, default)
        pref.separator = separator
        pref.postfixMode = postfixMode
        return Preference(prefName, sp, pref)
    }
}
