package ke.co.appslab.smartthings.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.datastates.DoorbellState
import ke.co.appslab.smartthings.datastates.FirebaseState
import ke.co.appslab.smartthings.datastates.RingAnswerState
import ke.co.appslab.smartthings.repositories.DoorbellLogsRepo
import ke.co.appslab.smartthings.utils.NonNullMediatorLiveData

class DoorbellViewModel : ViewModel() {
    private val doorbellEntriesMediatorLiveData = NonNullMediatorLiveData<DoorbellState>()
    private val doorbellLogsRepo = DoorbellLogsRepo()
    private val ringAnswerMediatorLiveData = NonNullMediatorLiveData<RingAnswerState>()


    fun getDoorbellLogsResponse(): LiveData<DoorbellState> = doorbellEntriesMediatorLiveData

    fun getRingAnswerResponse(): LiveData<RingAnswerState> = ringAnswerMediatorLiveData

    fun uploadDoorbellImage(imageBytes: Bitmap, apiKey: String) {
        val doorbellLiveData = doorbellLogsRepo.uploadDoorbellImage(imageBytes, apiKey)
        doorbellEntriesMediatorLiveData.addSource(
            doorbellLiveData
        ) { doorbellEntriesMediatorLiveData ->
            when {
                this.doorbellEntriesMediatorLiveData.hasActiveObservers() -> this.doorbellEntriesMediatorLiveData.removeSource(
                    doorbellLiveData
                )
            }
            this.doorbellEntriesMediatorLiveData.setValue(doorbellEntriesMediatorLiveData)
        }
    }

    fun observeRingAnswerChanges(ringId: String) {
        val ringAnswerLiveData = doorbellLogsRepo.observeRingAnswerChanges(ringId)
        ringAnswerMediatorLiveData.addSource(
            ringAnswerLiveData
        ) { ringAnswerMediatorLiveData ->
            when {
                this.ringAnswerMediatorLiveData.hasActiveObservers() -> this.ringAnswerMediatorLiveData.removeSource(
                    ringAnswerLiveData
                )
            }
            this.ringAnswerMediatorLiveData.setValue(ringAnswerMediatorLiveData)
        }
    }
}