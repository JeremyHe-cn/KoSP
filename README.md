# KoSP
[![](https://jitpack.io/v/me.alzz/kosp.svg)](https://jitpack.io/#me.alzz/kosp)
An easy way to write share preference

## How to use
Add the follow to root/build.gradle
```
allprojects {
    repositories {
        // other repository ...
        maven { url "https://jitpack.io" }
    }
}
```

Add dependency to app/build.gradle
```
dependencies {
    // ...
    compile 'me.alzz:kosp:1.0.0'
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
    var isFirstTimeOpen : Boolean by Preference(true)

    /**
     * custom key name
     * key : user_name
     * value -> String
     */
    var userName : String by Preference("user_name", "")
}
```

In `*Activity` or other class
```kotlin
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
```

## Proguard
```
-keepclassmember class * extend me.alzz.kosp.KoSharePrefs {
    *
}
```

## Thanks
- 《Kotlin for Android Developer》