package ke.co.appslab.smartthings.utils

import android.os.Build

object BoardDefaults {
    private val DEVICE_RPI3 = "rpi3"
    private val DEVICE_IMX7D_PICO = "imx7d_pico"

    /**
     * Return the GPIO pin that the Button is connected on.
     */
    val gpioForButton: String
        get() {
            when (Build.DEVICE) {
                DEVICE_RPI3 -> return "BCM21"
                DEVICE_IMX7D_PICO -> return "GPIO2_IO05"
                else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
            }
        }
}