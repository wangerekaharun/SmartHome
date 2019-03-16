package ke.co.appslab.smartthings.models

data class ElectricityLog(
    var timeStampOn: Map<String, String>? = null,
    var timestampOff: Map<String, String>? = null
)