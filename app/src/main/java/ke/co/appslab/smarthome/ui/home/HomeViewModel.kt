package ke.co.appslab.smarthome.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.PressureState
import ke.co.appslab.smarthome.datastates.TemperatureState
import ke.co.appslab.smarthome.models.ElectricityLog
import ke.co.appslab.smarthome.repositories.ElectricityMonitorRepo
import ke.co.appslab.smarthome.repositories.WeatherDataRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class HomeViewModel : ViewModel() {
    private val temperatureLogMediatorLiveData = NonNullMediatorLiveData<TemperatureState>()
    private val pressureLogMediatorLiveData = NonNullMediatorLiveData<PressureState>()
    private val weatherDataRepo = WeatherDataRepo()
    private val electricityMonitorRepo = ElectricityMonitorRepo()
    private val powerInfoMediatorLiveData = NonNullMediatorLiveData<Boolean>()
    private val electricityLogMediatorLiveData = NonNullMediatorLiveData<ElectricityLog>()

    fun getTemperatureLogResponse(): LiveData<TemperatureState> = temperatureLogMediatorLiveData


    fun getPowerInfoResponse(): LiveData<Boolean> = powerInfoMediatorLiveData

    fun getElectricityMonitorLogsResponse(): LiveData<ElectricityLog> = electricityLogMediatorLiveData

    fun getPressureLogResponse(): LiveData<PressureState> = pressureLogMediatorLiveData


    fun getTemperatureLogs(){
        val temperatureLogLiveData = weatherDataRepo.getTemperatureLogs()
        temperatureLogMediatorLiveData.addSource(
            temperatureLogLiveData
        ) { weatherMediatorLiveData ->
            when {
                this.temperatureLogMediatorLiveData.hasActiveObservers() -> this.temperatureLogMediatorLiveData.removeSource(
                    temperatureLogLiveData
                )
            }
            this.temperatureLogMediatorLiveData.setValue(weatherMediatorLiveData)
        }
    }

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

    fun getPressureLogs(){
        val pressureLogLiveData = weatherDataRepo.getPressureLogs()
        pressureLogMediatorLiveData.addSource(
            pressureLogLiveData
        ) { pressureLogMediatorLiveData ->
            when {
                this.pressureLogMediatorLiveData.hasActiveObservers() -> this.pressureLogMediatorLiveData.removeSource(
                    pressureLogLiveData
                )
            }
            this.pressureLogMediatorLiveData.setValue(pressureLogMediatorLiveData)
        }
    }
}