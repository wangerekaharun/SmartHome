package ke.co.appslab.smartthings.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.ui.camera.DoorbellActivity
import ke.co.appslab.smartthings.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_welcome.*
import android.net.wifi.WifiInfo
import android.content.Context.WIFI_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.net.wifi.WifiManager
import android.util.Log


class WelcomeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        getStartedRel.setOnClickListener {
            //go to things connection activity
            val thingsConnectionIntent = Intent(this, ThingsConnectionActivity::class.java)
            startActivity(thingsConnectionIntent)
        }
    }
}
