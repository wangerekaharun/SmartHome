package ke.co.appslab.smartthings.models

data class ImagesDoorbell(
    val timestamp: String,
    val image: String,
    val annotations: Map<String, Float>
)