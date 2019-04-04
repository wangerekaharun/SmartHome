package ke.co.appslab.smarthome.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.OverviewState
import ke.co.appslab.smarthome.datastates.PressureState
import ke.co.appslab.smarthome.datastates.TemperatureState
import ke.co.appslab.smarthome.models.InternetStatusLog
import ke.co.appslab.smarthome.repositories.InternetMonitorRepo
import ke.co.appslab.smarthome.repositories.OverviewRepo
import ke.co.appslab.smarthome.repositories.WeatherDataRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class HomeViewModel : ViewModel() {
    private val temperatureLogMediatorLiveData = NonNullMediatorLiveData<TemperatureState>()
    private val pressureLogMediatorLiveData = NonNullMediatorLiveData<PressureState>()
    private val weatherDataRepo = WeatherDataRepo()
    private val electricityMonitorRepo = InternetMonitorRepo()
    private val powerInfoMediatorLiveData = NonNullMediatorLiveData<Boolean>()
    private val electricityLogMediatorLiveData = NonNullMediatorLiveData<InternetStatusLog>()
    private val overviewRepo = OverviewRepo()
    private val allowedPersonsMediatorLiveData = NonNullMediatorLiveData<OverviewState>()
    private val disallowedPersonsMediatorLiveData = NonNullMediatorLiveData<OverviewState>()
    private val motionsCountMediatorLiveData = NonNullMediatorLiveData<OverviewState>()

    fun getTemperatureLogResponse(): LiveData<TemperatureState> = temperatureLogMediatorLiveData


    fun getPowerInfoResponse(): LiveData<Boolean> = powerInfoMediatorLiveData

    fun getElectricityMonitorLogsResponse(): LiveData<InternetStatusLog> = electricityLogMediatorLiveData

    fun getPressureLogResponse(): LiveData<PressureState> = pressureLogMediatorLiveData

    fun getAllowedPersonsResponse() : LiveData<OverviewState> = allowedPersonsMediatorLiveData

    fun getDisallowedPersonsResponse() : LiveData<OverviewState> = disallowedPersonsMediatorLiveData

    fun getMotionsCountResponse() : LiveData<OverviewState> = motionsCountMediatorLiveData


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
        val powerInfoLiveData = electricityMonitorRepo.loadInternetInfo()
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
        val electricityLogLiveData = electricityMonitorRepo.fetchInternetMonitorLogs()
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

    fun getTotalVisitorsAllowed(){
        val allowedVisitorsLiveData = overviewRepo.getTotalVisitorsAllowed()
        allowedPersonsMediatorLiveData.addSource(
            allowedVisitorsLiveData
        ) { allowedPersonsMediatorLiveData ->
            when {
                this.allowedPersonsMediatorLiveData.hasActiveObservers() -> this.allowedPersonsMediatorLiveData.removeSource(
                    allowedVisitorsLiveData
                )
            }
            this.allowedPersonsMediatorLiveData.setValue(allowedPersonsMediatorLiveData)
        }
    }

    fun getTotalVisitorsDisallowed(){
        val disallowedVisitorsLiveData = overviewRepo.getTotalVisitorsDisallowed()
        disallowedPersonsMediatorLiveData.addSource(
            disallowedVisitorsLiveData
        ) { disallowedPersonsMediatorLiveData ->
            when {
                this.disallowedPersonsMediatorLiveData.hasActiveObservers() -> this.disallowedPersonsMediatorLiveData.removeSource(
                    disallowedVisitorsLiveData
                )
            }
            this.disallowedPersonsMediatorLiveData.setValue(disallowedPersonsMediatorLiveData)
        }
    }

    fun getTotalMotions(){
        val motionCountLiveData = overviewRepo.getTotalMotions()
        motionsCountMediatorLiveData.addSource(
            motionCountLiveData
        ) { motionsCountMediatorLiveData ->
            when {
                this.motionsCountMediatorLiveData.hasActiveObservers() -> this.motionsCountMediatorLiveData.removeSource(
                    motionCountLiveData
                )
            }
            this.motionsCountMediatorLiveData.setValue(motionsCountMediatorLiveData)
        }
    }
}