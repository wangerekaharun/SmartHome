package ke.co.appslab.smartthings

import android.app.Activity
import android.app.IntentService
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.things.contrib.driver.apa102.Apa102
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import ke.co.appslab.smartthings.models.Weather
import ke.co.appslab.smartthings.ui.auth.ThingsConnectionActivity
import ke.co.appslab.smartthings.ui.weather.WeatherViewModel
import ke.co.appslab.smartthings.utils.RainbowUtil
import ke.co.appslab.smartthings.utils.nonNull
import ke.co.appslab.smartthings.utils.observe
import kotlinx.android.synthetic.main.activity_home.*
import java.io.IOException
import java.util.*

class HomeActivity : FragmentActivity() {
    private val TAG = "HomeActivity"
    lateinit var mEnvironmentalSensorDriver: Bmx280SensorDriver
    private val mSensorManager: SensorManager by lazy { getSystemService(SensorManager::class.java) }
    private val LEDSTRIP_BRIGHTNESS = 1
    lateinit var mDisplay: AlphanumericDisplay
    private lateinit var mLedstrip: Apa102
    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProviders.of(this).get(WeatherViewModel::class.java)
    }
    private var isWeatherSent = false
    private val delayUpdate = 900
    private var lastTemperatureTime = 0L
    private var lastPressureTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize 14-segment display
        try {
            mDisplay = RainbowHat.openDisplay().apply {
                setEnabled(true)
            }
            Log.d(TAG, "Initialized I2C Display")
        } catch (e: IOException) {
            throw RuntimeException("Error initializing display", e)
        }
        // Initialize LED strip
        try {
            mLedstrip = RainbowHat.openLedStrip().apply {
                brightness = LEDSTRIP_BRIGHTNESS
                val colors = IntArray(7)
                Arrays.fill(colors, Color.RED)
                write(colors)

            }
            Log.d(TAG, "Initialized SPI LED strip")
        } catch (e: IOException) {
            throw RuntimeException("Error initializing LED strip", e)
        }
        // Initialize temperature/pressure sensors
        try {
            mEnvironmentalSensorDriver = RainbowHat.createSensorDriver()
            mEnvironmentalSensorDriver.registerTemperatureSensor()
            mEnvironmentalSensorDriver.registerPressureSensor()

            Log.d(TAG, "Initialized I2C BMP280")
        } catch (e: IOException) {
            throw RuntimeException("Error initializing BMP280", e)
        }
        imageView.setOnClickListener {
            val connectionIntent = Intent(this,ThingsConnectionActivity::class.java)
            startActivity(connectionIntent)
        }
        observerLiveData()


    }

    private fun observerLiveData() {
        weatherViewModel.getSendWeatherResponse().nonNull().observe(this) {
            when {
                it.responseString == "Weather updated" -> {
                    Log.d(TAG, "true")
                }
                else -> {
                    Log.d(TAG, it.responseString)

                }
            }

        }
    }

    // Callback when SensorManager delivers new data.
    private val mSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val value = event.values[0]
            when {
                event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE -> when {
                    System.currentTimeMillis() > lastTemperatureTime + delayUpdate -> {
                        Log.d(TAG, "Temperature Sensor changed: $value and time is ${lastTemperatureTime + delayUpdate}")
                        lastTemperatureTime = System.currentTimeMillis()
                        updateTemperatureDisplay(value)
                        sendValue(value)
                    }
                }
            }
            when {
                event.sensor.type == Sensor.TYPE_PRESSURE -> when {
                    System.currentTimeMillis() > lastPressureTime + delayUpdate -> {
                        Log.d(TAG, "Pressure Sensor changed $value and time is ${lastPressureTime + delayUpdate}")
                        updateBarometerDisplay(value)
                    }

                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.d(TAG, "accuracy changed: $accuracy")
        }
    }

    private fun sendValue(value: Float) {
        val weather = Weather(
            temperature = value,
            pressure = value
        )
//        weatherViewModel.sendWeatherData(weather)

    }

    override fun onStop() {
        super.onStop()
        try {
            mDisplay.apply {
                clear()
                setEnabled(false)
                close()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error closing display", e)
        }

        try {
            mLedstrip.brightness = 0
            mLedstrip.write(IntArray(7))
            mLedstrip.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing LED strip", e)
        }
        //unregister session manager
        mSensorManager.unregisterListener(mSensorEventListener)
    }

    private fun updateTemperatureDisplay(temperature: Float) {
        try {
            mDisplay.display(temperature.toDouble())
        } catch (e: IOException) {
            Log.e(TAG, "Error updating display", e)
        }
    }

    /**
     * Update LED strip based on the latest pressure value.
     *
     * @param pressure Latest pressure value.
     */
    private fun updateBarometerDisplay(pressure: Float) {
        try {
            val colors = RainbowUtil.getWeatherStripColors(pressure)
            mLedstrip.write(colors)
        } catch (e: IOException) {
            Log.e(TAG, "Error updating ledstrip", e)
        }
    }

    override fun onStart() {
        super.onStart()
        // Register the BMP280 temperature sensor
        val temperature = mSensorManager
            .getDynamicSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE)[0]
        mSensorManager.registerListener(
            mSensorEventListener, temperature,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        // Register the BMP280 pressure sensor
        val pressure = mSensorManager
            .getDynamicSensorList(Sensor.TYPE_PRESSURE)[0]
        mSensorManager.registerListener(
            mSensorEventListener, pressure,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mEnvironmentalSensorDriver.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing sensors", e)
        }
    }
}
