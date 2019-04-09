package ke.co.appslab.smartthings.ui.motionsensor

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.ui.camera.DoorbellCamera
import ke.co.appslab.smartthings.utils.MotionSensor
import ke.co.appslab.smartthings.utils.nonNull
import ke.co.appslab.smartthings.utils.observe
import kotlinx.android.synthetic.main.activity_motion_sensor.*


class MotionSensorActivity : AppCompatActivity(), MotionSensor.MotionListener {
    private lateinit var ledMotionIndicatorGpio: Gpio
    private lateinit var ledArmedIndicatorGpio: Gpio
    private lateinit var camera: DoorbellCamera
    private lateinit var motionViewModel: MotionSensorViewModel
    private lateinit var motionSensor: MotionSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motion_sensor)

        setupViewModel()
        setupCamera()
        setupActuators()
        setupSensors()

        observeLiveData()
    }

    private fun setupViewModel() {
        motionViewModel = ViewModelProviders.of(this).get(MotionSensorViewModel::class.java)
    }

    private fun setupSensors() {
        motionSensor = MotionSensor(this, MOTION_SENSOR_GPIO_PIN)
        lifecycle.addObserver(motionSensor)
    }


    private fun setupActuators() {
        val peripheralManagerService = PeripheralManager.getInstance()
        ledMotionIndicatorGpio = peripheralManagerService.openGpio(LED_GPIO_PIN)
        ledMotionIndicatorGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        ledArmedIndicatorGpio = peripheralManagerService.openGpio(LED_ARMED_INDICATOR_PIN)
        ledArmedIndicatorGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
    }

    override fun onDestroy() {
        super.onDestroy()
        ledArmedIndicatorGpio.close()
        ledMotionIndicatorGpio.close()
        camera.close()
    }

    private fun observeLiveData() {
        motionViewModel.getMotionSensorResponse().observe(this, Observer {
            Log.d("ImageBitmap", it.responseString)
        })
    }

    private fun setupCamera() {
        camera = DoorbellCamera.getInstance()
        camera.initializeCamera(this, Handler(), imageAvailableListener)
    }

    private val imageAvailableListener = object : DoorbellCamera.ImageCapturedListener {
        override fun onImageCaptured(bitmap: Bitmap) {
            viewMotionImage.setImageBitmap(bitmap)
            motionViewModel.uploadMotionImage(bitmap)
        }
    }

    override fun onMotionDetected() {
        Log.d(ACT_TAG, "onMotionDetected")

        ledMotionIndicatorGpio.value = true

        camera.takePicture()
    }

    override fun onMotionStopped() {
        Log.d(ACT_TAG, "onMotionStopped")
        ledMotionIndicatorGpio.value = false
    }


    companion object {
        val LED_ARMED_INDICATOR_PIN: String = "GPIO6_IO15"
        val ACT_TAG: String = "MotionSensorctivity"
        val LED_GPIO_PIN = "GPIO6_IO14"
        val MOTION_SENSOR_GPIO_PIN = "GPIO2_IO03"
    }
}
