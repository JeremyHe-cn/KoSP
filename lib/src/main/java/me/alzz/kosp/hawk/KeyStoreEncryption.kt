package me.alzz.kosp.hawk

import android.content.Context
import com.orhanobut.hawk.Encryption
import me.alzz.kosp.KeyStoreHelper

/**
 * Created by JeremyHe on 2019-07-07.
 */
class KeyStoreEncryption(private val context: Context, private val alias: String): Encryption {
    override fun init(): Boolean {
        KeyStoreHelper.createKeys(context, alias)
        return true
    }

    override fun encrypt(key: String, value: String): String {
        return KeyStoreHelper.encrypt(alias, value)
    }

    override fun decrypt(key: String, value: String): String {
        return KeyStoreHelper.decrypt(alias, value)
    }

}