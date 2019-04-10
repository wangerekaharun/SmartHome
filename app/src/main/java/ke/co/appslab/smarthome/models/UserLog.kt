package ke.co.appslab.smarthome.models

data class UserLog(
    val firebaseToken: String? = null,
    val allowNotifications: Boolean = true,
    val accessSystemRemotely: Boolean = true,
    val armSystem: Boolean = true,
    val homeNickname: String? = null
)