package me.alzz.kosp.sample

import android.content.Context
import me.alzz.kosp.KoSharePrefs

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
    var isFirstTimeOpen by boolean()

    /**
     * custom key name
     * key : user_name
     * value -> String
     */
    var userName by string(name = "user_name")

    /**
     * dynamic key name
     * key : userId + '_avatarUrl'
     * value -> String
     */
    val avatarUrl by preference("none")

    /**
     * dynamic key name
     * key : userId + '_last_login_time'
     * value -> String
     */
    val lastLoginTime by preference(0L, "last_login_time")

    /**
     * custom separator and postfix mode
     * key: 'lastTime@' + userId
     */
    val lastTime by preference(0L, separator = "@", postfixMode = true)
}