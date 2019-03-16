package ke.co.appslab.smartthings.ui.electicitymonitor

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import ke.co.appslab.smartthings.R

class ElectricityMonitorActivity : AppCompatActivity() {
    private val electricityLogViewModel: ElectricityLogViewModel by lazy { ViewModelProviders.of(this).get(ElectricityLogViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electricity_monitor)

        monitorElectricity()
    }

    private fun monitorElectricity() {
        electricityLogViewModel.monitorElectricity()
    }
}
