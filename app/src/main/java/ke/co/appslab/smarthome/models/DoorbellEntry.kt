package ke.co.appslab.smarthome.models

data class DoorbellEntry(
    val timestamp: Long? = null,
    val image: String? = null,
    val documentId : String? = null,
    var answer: RingAnswer? = null

)


