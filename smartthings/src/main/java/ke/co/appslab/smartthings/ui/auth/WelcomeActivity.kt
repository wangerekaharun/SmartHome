package ke.co.appslab.smartthings.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import ke.co.appslab.smartthings.R
import kotlinx.android.synthetic.main.activity_welcome.*


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
