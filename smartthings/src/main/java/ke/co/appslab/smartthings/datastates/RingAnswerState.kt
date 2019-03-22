package ke.co.appslab.smartthings.datastates

import ke.co.appslab.smartthings.models.DoorbelLogs

data class RingAnswerState(
    val doorbelLogs: DoorbelLogs? = null,
    val responseString: String?= null
)


