package ke.co.appslab.smarthome.datastates

import ke.co.appslab.smarthome.models.PressureLog

data class PressureState(
    val pressureLogList: List<PressureLog>? = null,
    val databaseErrorString: String? = null
)