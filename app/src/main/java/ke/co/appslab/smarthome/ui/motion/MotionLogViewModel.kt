package ke.co.appslab.smarthome.ui.motion

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.MotionLogState
import ke.co.appslab.smarthome.repositories.MotionLogRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class MotionLogViewModel : ViewModel() {
    private val motionLogStateMediatorLiveData = NonNullMediatorLiveData<MotionLogState>()
    private val motionLogRepo = MotionLogRepo()

    fun getMotionLogResponse(): LiveData<MotionLogState> = motionLogStateMediatorLiveData

    fun getMotionLogs(){
        val motionLogsLiveData = motionLogRepo.getMotionLogs()
        motionLogStateMediatorLiveData.addSource(
            motionLogsLiveData
        ) { motionLogStateMediatorLiveData ->
            when {
                this.motionLogStateMediatorLiveData.hasActiveObservers() -> this.motionLogStateMediatorLiveData.removeSource(
                    motionLogsLiveData
                )
            }
            this.motionLogStateMediatorLiveData.setValue(motionLogStateMediatorLiveData)
        }
    }

}