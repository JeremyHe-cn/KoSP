package me.alzz.kosp

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import me.alzz.kosp.ObservablePreference.Companion.preferenceMap
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KProperty

/**
 * 可观察变化的 pref 属性
 * Created by JeremyHe on 2019-07-18.
 */
class ObservablePreference<T>(
        prefFileName: String,
        sp: SharedPreferences,
        name: String,
        default: T,
        encrypt: Boolean = false) : Preference<T>(prefFileName, sp, name, default, encrypt) {

    internal val notify by lazy {
        val liveData = MutableLiveData<T>()
        liveData.postValue(getValue(null, property))
        liveData
    }

    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ObservablePreference<T> {
        property = prop
        if (default is ObservablePreference<*>) {
            default.property = property
        } else {
            val key = "$prefFileName#${prop.name}"
            preferenceMap[key] = this
        }

        return this
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        super.setValue(thisRef, property, value)
        notify.postValue(value)
    }

    companion object {
        internal val preferenceMap = mutableMapOf<String, ObservablePreference<*>>()
    }
}

fun <T> KProperty<T>.observe(owner: LifecycleOwner, block: (T)->Unit) {
    val prefFileName = ((this as? CallableReference)?.boundReceiver as? KoSharePrefs)?.prefName ?: return
    val key = "$prefFileName#$name"
    val data = preferenceMap[key]?.notify as? MutableLiveData<T> ?: return
    data.observe(owner, Observer {
        it ?: return@Observer
        block(it)
    })
}