package ke.co.appslab.smarthome.datastates

import ke.co.appslab.smarthome.models.Weather

data class WeatherDataState (
    val weather: List<Weather>? = null,
    val responseString: String? = null
)