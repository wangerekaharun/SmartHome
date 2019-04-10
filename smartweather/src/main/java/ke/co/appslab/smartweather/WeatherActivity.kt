package ke.co.appslab.smartweather

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.things.contrib.driver.apa102.Apa102
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import ke.co.appslab.smartweather.models.Pressure
import ke.co.appslab.smartweather.models.Temperature
import ke.co.appslab.smartweather.ui.weather.WeatherViewModel
import ke.co.appslab.smartweather.utils.RainbowUtil
import ke.co.appslab.smartweather.utils.nonNull
import ke.co.appslab.smartweather.utils.observe
import java.io.IOException
import java.util.*


class WeatherActivity : AppCompatActivity() {
    private val TAG = "WeatherActivity"
    lateinit var mEnvironmentalSensorDriver: Bmx280SensorDriver
    private val mSensorManager: SensorManager by lazy { getSystemService(SensorManager::class.java) }
    private val LEDSTRIP_BRIGHTNESS = 1
    lateinit var mDisplay: AlphanumericDisplay
    private lateinit var mLedstrip: Apa102
    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProviders.of(this).get(WeatherViewModel::class.java)
    }
    private val delayUpdate = 300000
    private var lastTemperatureTime = 0L
    private var lastPressureTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSensors()
        observerLiveData()
    }

    private fun initializeSensors() {
        try {
            mEnvironmentalSensorDriver = RainbowHat.createSensorDriver()
            mSensorManager.registerDynamicSensorCallback(mDynamicSensorCallback)
            mEnvironmentalSensorDriver.registerTemperatureSensor()
            mEnvironmentalSensorDriver.registerPressureSensor()
            Log.d(TAG, "Initialized I2C BMP280")
        } catch (e: IOException) {
            throw RuntimeException("Error initializing BMP280", e)
        }

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
    }

    private fun observerLiveData() {
        weatherViewModel.getSendTemperatureResponse().nonNull().observe(this) {
            Log.d(TAG, it.responseString)
        }
        weatherViewModel.getSendPressureResponse().nonNull().observe(this) {
            Log.d(TAG, it.responseString)
        }
    }

    // Callback used when we register the BMP280 sensor driver with the system's SensorManager.
    private val mDynamicSensorCallback = object : SensorManager.DynamicSensorCallback() {
        override fun onDynamicSensorConnected(sensor: Sensor) {
            if (sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                // Our sensor is connected. Start receiving temperature data.
                mSensorManager.registerListener(
                    temperatureListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } else if (sensor.type == Sensor.TYPE_PRESSURE) {
                // Our sensor is connected. Start receiving pressure data.
                mSensorManager.registerListener(
                    pressureListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }

    }

    //Callback when SensorManager delivers new data.
    private val pressureListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val value = event.values[0]
            when {
                event.sensor.type == Sensor.TYPE_PRESSURE -> when {
                    System.currentTimeMillis() > lastPressureTime + delayUpdate -> {
                        lastPressureTime = System.currentTimeMillis()
                        updateBarometerDisplay(value)
                        sendPressureValue(value, lastPressureTime)
                    }

                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.d(TAG, "accuracy changed: $accuracy")
        }
    }

    private fun sendPressureValue(value: Float, lastPressureTime: Long) {
        val pressure = Pressure(
            pressure = value,
            time = lastPressureTime
        )
        weatherViewModel.sendPressureData(pressure)
    }

    //Callback when SensorManager delivers new data.
    private val temperatureListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val value = event.values[0]
            when {
                event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE -> when {
                    System.currentTimeMillis() > lastTemperatureTime + delayUpdate -> {
                        lastTemperatureTime = System.currentTimeMillis()
                        updateDisplay(value)
                        sendTemperatureValue(value, lastTemperatureTime)
                    }
                }
            }

        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.d(TAG, "accuracy changed: $accuracy")
        }
    }

    private fun sendTemperatureValue(value: Float, lastTemperatureTime: Long) {
        val temperature = Temperature(
            temperature = value,
            time = lastTemperatureTime
        )
        weatherViewModel.sendTemperatureData(temperature)

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

        //unregister session manager
        mSensorManager.unregisterListener(pressureListener)
        mSensorManager.unregisterListener(temperatureListener)
    }

    private fun updateDisplay(temperature: Float) {
        try {
            mDisplay.display(temperature.toDouble())
        } catch (e: IOException) {
            Log.e(TAG, "Error updating display", e)
        }
    }

    private fun updateBarometerDisplay(pressure: Float) {
        try {
            val colors = RainbowUtil.getWeatherStripColors(pressure)
            mLedstrip.write(colors)
        } catch (e: IOException) {
            Log.e(TAG, "Error updating ledstrip", e)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            mEnvironmentalSensorDriver.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing sensors", e)
        }

        try {
            mDisplay.clear();
            mDisplay.setEnabled(false);
            mDisplay.close();
        } catch (e: IOException) {
            Log.e(TAG, "Error disabling display", e);
        }

        try {
            mLedstrip.write(IntArray(7))
            mLedstrip.brightness = 0
            mLedstrip.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error disabling ledstrip", e)
        }

    }
}
