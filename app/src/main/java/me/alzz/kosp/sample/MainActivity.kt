package me.alzz.kosp.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import me.alzz.kosp.observe

class MainActivity : AppCompatActivity() {

    private val prefs by lazy { AppPrefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs::userName.observe(this ) {
            Log.d("alvminvm", "userName change: $it")
        }

        var content = "isFirstTimeOpen = ${prefs.isFirstTimeOpen}\r\nuserName = ${prefs.userName}"

        prefs.isFirstTimeOpen = true
        prefs.userName = "Jeremy He"

        prefs.avatarUrl["user1"] = "http://..../xx.jpg"
        val url = prefs.avatarUrl["user1"]
        content += "\r\navatar url of user1: $url"
        content += "\r\navatar url of user2: ${prefs.avatarUrl["user2"]}"

        content += "\r\nlast login time: ${prefs.lastLoginTime["user1"]}"
        prefs.lastLoginTime["user1"] = System.currentTimeMillis()

        prefs.lastTime["user1"] = System.currentTimeMillis()

//        prefs.encrypt = System.currentTimeMillis()
        content += "\r\nencrypt = ${prefs.encrypt}"

        contextTv.text = content
    }
}
