package ke.co.appslab.smartthings.ui.internetmonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.models.InternetMonitorLog
import ke.co.appslab.smartthings.repositories.InternetLogRepo
import ke.co.appslab.smartthings.repositories.InternetMonitorRepo
import ke.co.appslab.smartthings.utils.NonNullMediatorLiveData

class InternetLogViewModel : ViewModel() {
    private val internetLogRepo = InternetLogRepo()
    private val internetMonitorRepo = InternetMonitorRepo()
    private val internetInfoMediatorLiveData = NonNullMediatorLiveData<Boolean>()
    private val internetLogMediatorLiveData = NonNullMediatorLiveData<InternetMonitorLog>()

    fun getInternetInfoResponse(): LiveData<Boolean> = internetInfoMediatorLiveData

    fun getInternetMonitorLogsResponse(): LiveData<InternetMonitorLog> = internetLogMediatorLiveData


    fun loadInternetInfo() {
        val internetInfoLiveData = internetMonitorRepo.loadInternetInfo()
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
        val internetLogLiveData = internetMonitorRepo.fetchInternetMonitorLogs()
        internetLogMediatorLiveData.addSource(
            internetLogLiveData
        ) { internetLogMediatorLiveData ->
            when {
                this.internetLogMediatorLiveData.hasActiveObservers() -> this.internetLogMediatorLiveData.removeSource(
                    internetLogLiveData
                )
            }
            this.internetLogMediatorLiveData.setValue(internetLogMediatorLiveData)
        }

    }

    fun monitorInternet() {
        internetLogRepo.monitorInternetStatus()
    }

}