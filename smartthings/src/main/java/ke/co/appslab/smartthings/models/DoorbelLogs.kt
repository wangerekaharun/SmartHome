package ke.co.appslab.smartthings.models

data class DoorbelLogs (
    val timestamp: Long? = null,
    val image: String? = null,
    val documentId : String? = null,
    var answer: RingAnswer? = null
)