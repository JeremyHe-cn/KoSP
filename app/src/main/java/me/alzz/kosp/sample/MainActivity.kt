package me.alzz.kosp.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val prefs : AppPrefs by lazy { AppPrefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var content = "isFirstTimeOpen = ${prefs.isFirstTimeOpen}\r\nuserName = ${prefs.userName}"

        prefs.isFirstTimeOpen = false
        prefs.userName = "Jeremy He"

        prefs.avatarUrl["user1"] = "http://..../xx.jpg"
        val url = prefs.avatarUrl["user1"]
        content += "\r\navatar url of user1: $url"
        content += "\r\navatar url of user2: ${prefs.avatarUrl["user2"]}"

        content += "\r\nlast login time: ${prefs.lastLoginTime["user1"]}"
        prefs.lastLoginTime["user1"] = System.currentTimeMillis()

        prefs.lastTime["user1"] = System.currentTimeMillis()

        contextTv.text = content
    }
}
