package ke.co.appslab.smarthome.ui.electricitymonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.models.InternetStatusLog
import ke.co.appslab.smarthome.repositories.InternetMonitorRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class InternetMonitorViewModel : ViewModel() {
    private val electricityMonitorRepo = InternetMonitorRepo()
    private val internetInfoMediatorLiveData = NonNullMediatorLiveData<Boolean>()
    private val internetLogMediatorLiveData = NonNullMediatorLiveData<InternetStatusLog>()


    fun getInternetInfoResponse(): LiveData<Boolean> = internetInfoMediatorLiveData

    fun getInternetMonitorLogsResponse(): LiveData<InternetStatusLog> = internetLogMediatorLiveData


    fun loadInternetInfo() {
        val internetInfoLiveData = electricityMonitorRepo.loadInternetInfo()
        internetInfoMediatorLiveData.addSource(
            internetInfoLiveData
        ) { internetInfoMediatorLiveData ->
            when {
                this.internetInfoMediatorLiveData.hasActiveObservers() -> this.internetInfoMediatorLiveData.removeSource(
                    internetInfoLiveData
                )
            }
            this.internetInfoMediatorLiveData.setValue(internetInfoMediatorLiveData)
        }
    }

    fun fetchInternetMonitorLogs() {
        val internetLogsLiveData = electricityMonitorRepo.fetchInternetMonitorLogs()
        internetLogMediatorLiveData.addSource(
            internetLogsLiveData
        ) { internetLogMediatorLiveData ->
            when {
                this.internetLogMediatorLiveData.hasActiveObservers() -> this.internetLogMediatorLiveData.removeSource(
                    internetLogsLiveData
                )
            }
            this.internetLogMediatorLiveData.setValue(internetLogMediatorLiveData)
        }

    }
}