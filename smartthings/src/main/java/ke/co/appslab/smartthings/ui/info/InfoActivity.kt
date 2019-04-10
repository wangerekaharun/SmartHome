package ke.co.appslab.smartthings.ui.info

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.utils.SharedPref
import ke.co.appslab.smartthings.utils.SharedPref.DEVICE_NAME
import ke.co.appslab.smartthings.utils.SharedPref.EMAIL
import ke.co.appslab.smartthings.utils.SharedPref.FULL_NAME
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(SharedPref.PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        setUpvViews()
    }

    private fun setUpvViews() {
        val fullName = sharedPreferences.getString(FULL_NAME,null)
        val deviceName = sharedPreferences.getString(DEVICE_NAME,null)
        val email = sharedPreferences.getString(EMAIL,null)

        emailTextView.text = email
        deviceNameTextView.text = deviceName
        fullNameTextView.text = fullName
    }
}
