package ke.co.appslab.smartthings.utils

import android.graphics.Color

/**
 * Helper methods for computing outputs on the Rainbow HAT
 */
object RainbowUtil {
    /* Barometer Range Constants */
    private val BAROMETER_RANGE_LOW = 965f
    private val BAROMETER_RANGE_HIGH = 1035f

    /* LED Strip Color Constants*/
    private var sRainbowColors: IntArray? = null

    init {
        sRainbowColors = IntArray(7)
        for (i in sRainbowColors!!.indices) {
            val hsv = floatArrayOf(i * 360f / sRainbowColors!!.size, 1.0f, 1.0f)
            sRainbowColors!![i] = Color.HSVToColor(255, hsv)
        }
    }

    /**
     * Return an array of colors for the LED strip based on the given pressure.
     * @param pressure Pressure reading to compare.
     * @return Array of colors to set on the LED strip.
     */
    fun getWeatherStripColors(pressure: Float): IntArray {
        val t = (pressure - BAROMETER_RANGE_LOW) / (BAROMETER_RANGE_HIGH - BAROMETER_RANGE_LOW)
        var n = Math.ceil((sRainbowColors!!.size * t).toDouble()).toInt()
        n = Math.max(0, Math.min(n, sRainbowColors!!.size))

        val colors = IntArray(sRainbowColors!!.size)
        for (i in 0 until n) {
            val ri = sRainbowColors!!.size - 1 - i
            colors[ri] = sRainbowColors!![ri]
        }

        return colors
    }
}