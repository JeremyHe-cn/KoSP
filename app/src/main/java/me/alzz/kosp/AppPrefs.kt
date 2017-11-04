package me.alzz.kosp

import android.content.Context

/**
 * Created by jeremyhe on 2017/11/4.
 */
class AppPrefs(context: Context) : KoPreferences(context) {
    override val PREFS_FILE_NAME = "app.prefs"

    var isFirstTimeOpen : Boolean by Preference(true)

    var userName : String by Preference("")
}