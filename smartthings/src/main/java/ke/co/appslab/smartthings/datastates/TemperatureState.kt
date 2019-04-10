package ke.co.appslab.smartthings.datastates

import ke.co.appslab.smartthings.models.TemperatureLog

data class TemperatureState(
    val temperatureLogList: List<TemperatureLog>? = null,
    val databaseError: String? = null
)