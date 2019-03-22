package ke.co.appslab.smarthome.ui.doorbell

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.AnswerRingState
import ke.co.appslab.smarthome.datastates.DoorBellState
import ke.co.appslab.smarthome.repositories.AnswerRingRepo
import ke.co.appslab.smarthome.repositories.DoorbellRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class DoorbellViewModel : ViewModel() {
    private val doorEntriesMediatorLiveData = NonNullMediatorLiveData<DoorBellState>()
    private val doorbellRepo = DoorbellRepo()
    private val answerRingRepo = AnswerRingRepo()
    private val answerRingMediatorLiveData = NonNullMediatorLiveData<AnswerRingState>()


    fun getDoorbellEntriesResponse(): LiveData<DoorBellState> = doorEntriesMediatorLiveData

    fun getAnswerRingResponse(): LiveData<AnswerRingState> = answerRingMediatorLiveData

    fun getDoorBellEntries() {
        val doorEntriesLiveData = doorbellRepo.getDoorBellEntries()
        doorEntriesMediatorLiveData.addSource(
            doorEntriesLiveData
        ) { doorEntriesMediatorLiveData ->
            when {
                this.doorEntriesMediatorLiveData.hasActiveObservers() -> this.doorEntriesMediatorLiveData.removeSource(
                    doorEntriesLiveData
                )
            }
            this.doorEntriesMediatorLiveData.setValue(doorEntriesMediatorLiveData)
        }
    }

    fun answerRing(ringId: String, disposition: Boolean, userId: String){
        val answerRingLiveData = answerRingRepo.answerRing(ringId, disposition, userId)
        answerRingMediatorLiveData.addSource(
            answerRingLiveData
        ) { answerRingMediatorLiveData ->
            when {
                this.answerRingMediatorLiveData.hasActiveObservers() -> this.answerRingMediatorLiveData.removeSource(
                    answerRingLiveData
                )
            }
            this.answerRingMediatorLiveData.setValue(answerRingMediatorLiveData)
        }
    }
}