package ke.co.appslab.smartthings.ui.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.ui.camera.DoorbellActivity
import ke.co.appslab.smartthings.ui.internetmonitor.InternetMonitorActivity
import ke.co.appslab.smartthings.ui.motionsensor.MotionSensorActivity
import ke.co.appslab.smartthings.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setUpNavigation()
    }

    private fun setUpNavigation() {
        dashboardCardView.setOnClickListener {
            val motionSensorIntent = Intent(this, MotionSensorActivity::class.java)
            startActivity(motionSensorIntent)
        }
        doorbellCardView.setOnClickListener {
            val doorbellIntent = Intent(this, DoorbellActivity::class.java)
            startActivity(doorbellIntent)
        }
        internetCardView.setOnClickListener {
            val electricityMonitorIntent = Intent(this, InternetMonitorActivity::class.java)
            startActivity(electricityMonitorIntent)
        }
        temperatureCardView.setOnClickListener {
            val weatherIntent = Intent(this, WeatherActivity::class.java)
            weatherIntent.putExtra("weatherType", "Temperature")
            startActivity(weatherIntent)
        }
        pressureCardView.setOnClickListener {
            val weatherIntent = Intent(this, WeatherActivity::class.java)
            weatherIntent.putExtra("weatherType", "Pressure")
            startActivity(weatherIntent)
        }
    }
}
