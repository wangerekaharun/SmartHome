package ke.co.appslab.smartthings.models

data class ImagesDoorbell(
    val timestamp: Long?,
    val image: String,
    val annotations: Map<String, Float>? = null
)