package ke.co.appslab.smartthings.ui.weather

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.datastates.PressureState
import ke.co.appslab.smartthings.datastates.TemperatureState
import ke.co.appslab.smartthings.utils.nonNull
import ke.co.appslab.smartthings.utils.observe
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherActivity : AppCompatActivity() {
    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProviders.of(this).get(WeatherViewModel::class.java)
    }
    private var weatherType: String? = null
    private val TAG = "WeatherLogsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val extraIntents = intent
        weatherType = extraIntents.getStringExtra("weatherType")

        fetchWeatherLogs()
        observeLiveData()
    }

    private fun observeLiveData() {
        weatherType?.let { weatherType ->
            when (weatherType) {
                "Temperature" -> {
                    weatherViewModel.getTemperatureLogResponse().nonNull().observe(this) {
                        showTemperatureReadings(it)
                    }
                }
                "Pressure" -> {
                    weatherViewModel.getPressureLogResponse().nonNull().observe(this) {
                        showPressureReadings(it)
                    }
                }
            }
        }

    }

    private fun showPressureReadings(it: PressureState) {
        hideDialog()
        when {
            it.pressureLogList != null -> {
                weatherTypeImg.setImageResource(R.drawable.ic_pressure)
                weatherTypeLabelTextView.text = getString(R.string.current_temperature, "Pressure")
                weatherReadingTextView.text = getString(R.string.pressure, it.pressureLogList.first().pressure)

            }
            else -> Log.d(TAG, it.databaseErrorString)
        }

    }

    private fun showTemperatureReadings(it: TemperatureState) {
        hideDialog()
        when {
            it.temperatureLogList != null -> {
                weatherTypeImg.setImageResource(R.drawable.ic_temperature_blue)
                weatherTypeLabelTextView.text = getString(R.string.current_temperature, "Temperature")
                val temperature = it.temperatureLogList
                weatherReadingTextView.text = getString(R.string.temperature, temperature.first().temperature)
            }
            else -> Log.d(TAG, it.databaseError)
        }
    }

    private fun fetchWeatherLogs() {
        showDialog()
        weatherViewModel.getPressureLogs()
        weatherViewModel.getTemperatureLogs()
    }

    private fun showDialog() {
        progressBar.visibility = View.VISIBLE
        weatherDetailsLinear.visibility = View.GONE
    }

    private fun hideDialog() {
        progressBar.visibility = View.GONE
        weatherDetailsLinear.visibility = View.VISIBLE
    }
}
