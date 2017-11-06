package me.alzz.kosp

import android.content.Context

/**
 * Created by jeremyhe on 2017/11/4.
 */
class AppPrefs(context: Context) : KoSharePrefs(context) {
    /**
     * override PREFS_FILE_NAME to define the name of SharePreference
     */
    override val PREFS_FILE_NAME = "app.prefs"

    /**
     * default key name
     * key : isFirstTimeOpen
     * value -> Boolean
     */
    var isFirstTimeOpen : Boolean by Preference(true)

    /**
     * custom key name
     * key : user_name
     * value -> String
     */
    var userName : String by Preference("user_name", "")
}