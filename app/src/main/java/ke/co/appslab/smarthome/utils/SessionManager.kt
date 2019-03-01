package ke.co.appslab.smarthome.utils

import android.content.Context
import android.content.SharedPreferences
import ke.co.appslab.smarthome.utils.SharedPref.KEY_IS_LOGGEDIN
import ke.co.appslab.smarthome.utils.SharedPref.PREF_NAME

class SessionManager(val context: Context) {
    var sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setLogin(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGEDIN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGEDIN, false)
    }
}