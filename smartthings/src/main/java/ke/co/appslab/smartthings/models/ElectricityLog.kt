package ke.co.appslab.smartthings.models

data class ElectricityLog (
    val timeStampOn : Map<String,String>,
    val timestampOff : Map<String,String>
)