package ke.co.appslab.smartthings.ui.electicitymonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.models.ElectricityMonitorLog
import ke.co.appslab.smartthings.repositories.ElectricityLogRepo
import ke.co.appslab.smartthings.repositories.ElectricityMonitorRepo
import ke.co.appslab.smartthings.utils.NonNullMediatorLiveData

class ElectricityLogViewModel : ViewModel() {
    private val electricityLogRepo = ElectricityLogRepo()
    private val electricityMonitorRepo = ElectricityMonitorRepo()
    private val powerInfoMediatorLiveData = NonNullMediatorLiveData<Boolean>()
    private val electricityLogMediatorLiveData = NonNullMediatorLiveData<ElectricityMonitorLog>()

    fun getPowerInfoResponse(): LiveData<Boolean> = powerInfoMediatorLiveData

    fun getElectricityMonitorLogsResponse(): LiveData<ElectricityMonitorLog> = electricityLogMediatorLiveData


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

    fun monitorElectricity() {
        electricityLogRepo.monitorElectricity()
    }

}