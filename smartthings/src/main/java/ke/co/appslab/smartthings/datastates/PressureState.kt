package ke.co.appslab.smartthings.datastates

import ke.co.appslab.smartthings.models.PressureLog

data class PressureState(
    val pressureLogList: List<PressureLog>? = null,
    val databaseErrorString: String? = null
)