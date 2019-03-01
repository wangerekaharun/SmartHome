package ke.co.appslab.smarthome.datastates

import ke.co.appslab.smarthome.models.DoorbellEntry

data class DoorBellState(
    val responseString: String? = null,
    val entriesList: List<DoorbellEntry>? = null
)