# KoSP
[![](https://jitpack.io/v/me.alzz/kosp.svg)](https://jitpack.io/#me.alzz/kosp)  
An easy way to write share preference

## How to use
Add the follow to **root/build.gradle**
```gradle
allprojects {
    repositories {
        // other repository ...
        maven { url "https://jitpack.io" }
    }
}
```

Add dependency to **app/build.gradle**
```gradle
dependencies {
    // ...
    compile 'me.alzz:kosp:1.1.1'
}
```

Create a class which extend `KoSharePrefs`.
```kotlin
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
}
```

In `*Activity` or other class
```kotlin
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

        contextTv.text = content
    }
}
```

## Proguard
```proguard
-keepattributes *Annotation*
-keep class kotlin.** { *; }
-keep class org.jetbrains.** { *; }
-dontwarn kotlin.reflect.jvm.internal.**
```

## Thanks
- 《Kotlin for Android Developer》
