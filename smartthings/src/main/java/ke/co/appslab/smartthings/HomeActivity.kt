package ke.co.appslab.smartthings

import android.app.Activity
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import com.google.android.things.contrib.driver.apa102.Apa102
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import java.io.IOException
import java.util.*

class HomeActivity : Activity() {
    private val TAG = HomeActivity::class.simpleName
    private var mEnvironmentalSensorDriver: Bmx280SensorDriver? = null
    private var mSensorManager: SensorManager? = null
    private val LEDSTRIP_BRIGHTNESS = 1
    lateinit var mDisplay: AlphanumericDisplay
    private lateinit var mLedstrip: Apa102


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mSensorManager = getSystemService(SensorManager::class.java)
        // Initialize 14-segment display
        try {
            mDisplay = RainbowHat.openDisplay().apply {
                setEnabled(true)
                display("Weather")

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
            mEnvironmentalSensorDriver = RainbowHat.createSensorDriver().apply {
                registerTemperatureSensor()
                registerPressureSensor()
            }
            Log.d(TAG, "Initialized I2C BMP280")
        } catch (e: IOException) {
            throw RuntimeException("Error initializing BMP280", e)
        }


    }

    // Callback when SensorManager delivers new data.
    private val mSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val value = event.values[0]

            if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                updateTemperatureDisplay(value)
            }
            if (event.sensor.type == Sensor.TYPE_PRESSURE) {
                updateBarometerDisplay(value)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.d(TAG, "accuracy changed: $accuracy")
        }
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
            mLedstrip.setBrightness(0)
            mLedstrip.write(IntArray(7))
            mLedstrip.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing LED strip", e)
        }
        //unregister session manager
        mSensorManager.unregisterListener(mSensorEventListener)
    }

    private fun updateTemperatureDisplay(temperature: Float) {
        //TODO: Add code to write a value to the segment display
        if (mDisplay != null) {
            try {
                mDisplay.display(temperature.toDouble())
            } catch (e: IOException) {
                Log.e(TAG, "Error updating display", e)
            }

        }
    }

    /**
     * Update LED strip based on the latest pressure value.
     *
     * @param pressure Latest pressure value.
     */
    private fun updateBarometerDisplay(pressure: Float) {
        //TODO: Add code to send color data to the LED strip
        if (mLedstrip != null) {
            try {
                val colors = RainbowUtil.getWeatherStripColors(pressure)
                mLedstrip.write(colors)
            } catch (e: IOException) {
                Log.e(TAG, "Error updating ledstrip", e)
            }

        }
    }

}
