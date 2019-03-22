package ke.co.appslab.smarthome.models

data class DoorbellEntry(
    val timestamp: Long? = null,
    val image: String? = null,
    var documentId : String? = null,
    var answer: RingAnswer? = null

)


