package ke.co.appslab.smartthings.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.ui.camera.DoorbellActivity
import ke.co.appslab.smartthings.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_welcome.*


class WelcomeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        getStartedRel.setOnClickListener {
            //go to dashboard
            val dashboardIntent = Intent(this, DashboardActivity::class.java)
            startActivity(dashboardIntent)
        }
    }
}
