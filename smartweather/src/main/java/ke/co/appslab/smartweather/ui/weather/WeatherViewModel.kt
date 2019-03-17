package ke.co.appslab.smartweather.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartweather.datastates.FirebaseState
import ke.co.appslab.smartweather.models.Pressure
import ke.co.appslab.smartweather.models.Temperature
import ke.co.appslab.smartweather.models.Weather
import ke.co.appslab.smartweather.repositories.WeatherRepo
import ke.co.appslab.smartweather.utils.NonNullMediatorLiveData

class WeatherViewModel : ViewModel() {
    private val temperatureMediatorLiveData = NonNullMediatorLiveData<FirebaseState>()
    private val pressureMediatorLiveData = NonNullMediatorLiveData<FirebaseState>()
    private val weatherRepo = WeatherRepo()

    fun getSendTemperatureResponse(): LiveData<FirebaseState> = temperatureMediatorLiveData

    fun getSendPressureResponse(): LiveData<FirebaseState> = pressureMediatorLiveData

    fun sendTemperatureData(temperature: Temperature) {
        val temperatureLiveData = weatherRepo.sendTemperatureData(temperature)
        temperatureMediatorLiveData.addSource(
            temperatureLiveData
        ) { temperatureMediatorLiveData ->
            when {
                this.temperatureMediatorLiveData.hasActiveObservers() -> this.temperatureMediatorLiveData.removeSource(
                    temperatureLiveData
                )
            }
            this.temperatureMediatorLiveData.setValue(temperatureMediatorLiveData)
        }
    }

    fun sendPressureData(pressure: Pressure) {
        val pressureLiveData = weatherRepo.sendPressureData(pressure)
        pressureMediatorLiveData.addSource(
            pressureLiveData
        ) { pressureMediatorLiveData ->
            when {
                this.pressureMediatorLiveData.hasActiveObservers() -> this.pressureMediatorLiveData.removeSource(
                    pressureLiveData
                )
            }
            this.pressureMediatorLiveData.setValue(pressureMediatorLiveData)
        }
    }
}