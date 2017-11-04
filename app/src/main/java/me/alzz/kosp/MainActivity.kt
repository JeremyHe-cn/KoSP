package me.alzz.kosp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val prefs : AppPrefs by lazy { AppPrefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contextTv.text = "isFirstTimeOpen = ${prefs.isFirstTimeOpen}\r\nuserName = ${prefs.userName}"

        prefs.isFirstTimeOpen = false
        prefs.userName = "Jeremy He"
    }
}
