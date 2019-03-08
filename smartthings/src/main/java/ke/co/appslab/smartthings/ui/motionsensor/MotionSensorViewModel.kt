package ke.co.appslab.smartthings.ui.motionsensor

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.datastates.FirebaseState
import ke.co.appslab.smartthings.repositories.MotionSensorRepo
import ke.co.appslab.smartthings.utils.NonNullMediatorLiveData

class MotionSensorViewModel : ViewModel() {
    private val motionMediatorLiveData = MediatorLiveData<FirebaseState>()
    private val motionSensorRepo = MotionSensorRepo()


    fun getMotionSensorResponse(): LiveData<FirebaseState> = motionMediatorLiveData

    fun uploadMotionImage(imageBytes: Bitmap) {
        val motionLiveData = motionSensorRepo.uploadMotionImage(imageBytes)
        motionMediatorLiveData.addSource(
            motionLiveData
        ) { motionMediatorLiveData ->
            when {
                this.motionMediatorLiveData.hasActiveObservers() -> this.motionMediatorLiveData.removeSource(
                    motionLiveData
                )
            }
            this.motionMediatorLiveData.setValue(motionMediatorLiveData)
        }
    }

}