package ke.co.appslab.smartthings.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.datastates.PressureState
import ke.co.appslab.smartthings.datastates.TemperatureState
import ke.co.appslab.smartthings.repositories.WeatherRepo
import ke.co.appslab.smartthings.utils.NonNullMediatorLiveData

class WeatherViewModel : ViewModel() {
    private val temperatureLogMediatorLiveData = NonNullMediatorLiveData<TemperatureState>()
    private val pressureLogMediatorLiveData = NonNullMediatorLiveData<PressureState>()
    private val weatherDataRepo = WeatherRepo()

    fun getTemperatureLogResponse(): LiveData<TemperatureState> = temperatureLogMediatorLiveData

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