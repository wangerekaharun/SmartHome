package ke.co.appslab.smarthome.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.WeatherDataState
import ke.co.appslab.smarthome.models.ElectricityLog
import ke.co.appslab.smarthome.repositories.ElectricityMonitorRepo
import ke.co.appslab.smarthome.repositories.WeatherDataRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class HomeViewModel : ViewModel() {
    private val weatherDataMediatorLiveData = NonNullMediatorLiveData<WeatherDataState>()
    private val weatherDataRepo = WeatherDataRepo()
    private val electricityMonitorRepo = ElectricityMonitorRepo()
    private val powerInfoMediatorLiveData = NonNullMediatorLiveData<Boolean>()
    private val electricityLogMediatorLiveData = NonNullMediatorLiveData<ElectricityLog>()

    fun getWeatherDataResponse(): LiveData<WeatherDataState> = weatherDataMediatorLiveData


    fun getPowerInfoResponse(): LiveData<Boolean> = powerInfoMediatorLiveData

    fun getElectricityMonitorLogsResponse(): LiveData<ElectricityLog> = electricityLogMediatorLiveData


    fun getWeatherData(){
        val weatherLiveData = weatherDataRepo.getWeatherData()
        weatherDataMediatorLiveData.addSource(
            weatherLiveData
        ) { weatherMediatorLiveData ->
            when {
                this.weatherDataMediatorLiveData.hasActiveObservers() -> this.weatherDataMediatorLiveData.removeSource(
                    weatherLiveData
                )
            }
            this.weatherDataMediatorLiveData.setValue(weatherMediatorLiveData)
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
}