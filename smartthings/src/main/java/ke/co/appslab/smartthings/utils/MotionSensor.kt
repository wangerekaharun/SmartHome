package ke.co.appslab.smartthings.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager

class MotionSensor(
    private val motionListener: MotionListener,
    motionSensorPinNumber: String
) : LifecycleObserver {

    private val motionSensorGpioPin: Gpio = PeripheralManager.getInstance().openGpio(motionSensorPinNumber)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        //Receive data from the sensor - DIRECTION_IN
        motionSensorGpioPin.setDirection(Gpio.DIRECTION_IN)
        //High voltage means movement has been detected
        motionSensorGpioPin.setActiveType(Gpio.ACTIVE_HIGH)
        //The trigger we want to receive both low and high triggers so EDGE_BOTH
        motionSensorGpioPin.setEdgeTriggerType(Gpio.EDGE_BOTH)
        motionSensorGpioPin.registerGpioCallback(object : GpioCallback {
            override fun onGpioEdge(gpio: Gpio?): Boolean {
                gpio?.let {
                    if (gpio.value) {
                        motionListener.onMotionDetected()
                    } else {
                        motionListener.onMotionStopped()
                    }
                }
                return true
            }
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {
        motionSensorGpioPin.close()
    }

    interface MotionListener {
        fun onMotionDetected()
        fun onMotionStopped()
    }

}