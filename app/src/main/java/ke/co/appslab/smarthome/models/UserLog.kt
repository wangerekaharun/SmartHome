package ke.co.appslab.smarthome.models

data class UserLog(
    val firebaseToken: String? = null,
    val notificationsAllowed: Boolean = true,
    val homeNickname: String? = null
)