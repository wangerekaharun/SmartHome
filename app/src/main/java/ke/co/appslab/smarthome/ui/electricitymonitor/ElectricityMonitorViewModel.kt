package ke.co.appslab.smarthome.ui.electricitymonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.models.ElectricityLog
import ke.co.appslab.smarthome.repositories.ElectricityMonitorRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class ElectricityMonitorViewModel : ViewModel() {
    private val electricityMonitorRepo = ElectricityMonitorRepo()
    private val powerInfoMediatorLiveData = NonNullMediatorLiveData<Boolean>()
    private val electricityLogMediatorLiveData = NonNullMediatorLiveData<ElectricityLog>()


    fun getPowerInfoResponse(): LiveData<Boolean> = powerInfoMediatorLiveData

    fun getElectricityMonitorLogsResponse(): LiveData<ElectricityLog> = electricityLogMediatorLiveData


    fun loadPowerInfo() {
        val powerInfoLiveData = electricityMonitorRepo.loadPowerInfo()
        powerInfoMediatorLiveData.addSource(
            powerInfoLiveData
        ) { powerInfoMediatorLiveData ->
            when {
                this.powerInfoMediatorLiveData.hasActiveObservers() -> this.powerInfoMediatorLiveData.removeSource(
                    powerInfoLiveData
                )
            }
            this.powerInfoMediatorLiveData.setValue(powerInfoMediatorLiveData)
        }
    }

    fun fetchElectricityMonitorLogs() {
        val electricityLogLiveData = electricityMonitorRepo.fetchElectricityMonitorLogs()
        electricityLogMediatorLiveData.addSource(
            electricityLogLiveData
        ) { electricityLogMediatorLiveData ->
            when {
                this.electricityLogMediatorLiveData.hasActiveObservers() -> this.electricityLogMediatorLiveData.removeSource(
                    electricityLogLiveData
                )
            }
            this.electricityLogMediatorLiveData.setValue(electricityLogMediatorLiveData)
        }

    }
}