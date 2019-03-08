package ke.co.appslab.smarthome.datastates

import ke.co.appslab.smarthome.models.MotionImageLog

data class MotionLogState(
    val response: String? = null,
    val motionLogList: List<MotionImageLog>? = null
)