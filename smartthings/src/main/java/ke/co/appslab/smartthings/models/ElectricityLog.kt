package ke.co.appslab.smartthings.models

data class ElectricityLog(
    var timeStampOn: Long? = null,
    var timestampOff: Long? = null,
    var isConnected: Boolean = false,
    var wasDisconnected: Int = 0
)