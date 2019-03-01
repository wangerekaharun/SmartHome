package ke.co.appslab.smarthome.ui.doorbell

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.DoorBellState
import ke.co.appslab.smarthome.repositories.DoorbellRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class DoorbellViewModel : ViewModel() {
    private val doorEntriesMediatorLiveData = MediatorLiveData<DoorBellState>()
    private val doorbellRepo = DoorbellRepo()


    fun getDoorbellEntriesResponse(): LiveData<DoorBellState> = doorEntriesMediatorLiveData

    fun getDoorBellEntries() {
        val doorEntriesLiveData = doorbellRepo.getDoorBellEntries()
        doorEntriesMediatorLiveData.addSource(
            doorEntriesMediatorLiveData
        ) { doorEntriesMediatorLiveData ->
            when {
                this.doorEntriesMediatorLiveData.hasActiveObservers() -> this.doorEntriesMediatorLiveData.removeSource(
                    doorEntriesLiveData
                )
            }
            this.doorEntriesMediatorLiveData.setValue(doorEntriesMediatorLiveData)
        }
    }
}