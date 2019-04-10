package ke.co.appslab.smarthome.datastates

import ke.co.appslab.smarthome.models.TemperatureLog

data class TemperatureState(
    val temperatureLogList: List<TemperatureLog>? = null,
    val databaseError: String? = null
)