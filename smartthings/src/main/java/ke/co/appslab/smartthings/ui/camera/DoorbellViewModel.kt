package ke.co.appslab.smartthings.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.datastates.FirebaseState
import ke.co.appslab.smartthings.repositories.DoorbellLogsRepo
import ke.co.appslab.smartthings.utils.NonNullMediatorLiveData

class DoorbellViewModel : ViewModel() {
    private val doorbellEntriesMediatorLiveData = NonNullMediatorLiveData<FirebaseState>()
    private val doorbellLogsRepo = DoorbellLogsRepo()


    fun getDoorbellLogsResponse(): LiveData<FirebaseState> = doorbellEntriesMediatorLiveData

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
}