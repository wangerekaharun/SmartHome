package ke.co.appslab.smartthings.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.datastates.FirebaseState
import ke.co.appslab.smartthings.models.Weather
import ke.co.appslab.smartthings.repositories.WeatherRepo
import ke.co.appslab.smartthings.utils.NonNullMediatorLiveData

class WeatherViewModel : ViewModel() {
    private val weatherMediatorLiveData = NonNullMediatorLiveData<FirebaseState>()
    private val weatherRepo = WeatherRepo()

    fun getSendWeatherResponse(): LiveData<FirebaseState> = weatherMediatorLiveData

    fun sendWeatherData(weather: Weather) {
        val weatherLiveData = weatherRepo.sendWeatherData(weather)
        weatherMediatorLiveData.addSource(
            weatherLiveData
        ) { weatherMediatorLiveData ->
            when {
                this.weatherMediatorLiveData.hasActiveObservers() -> this.weatherMediatorLiveData.removeSource(
                    weatherLiveData
                )
            }
            this.weatherMediatorLiveData.setValue(weatherMediatorLiveData)
        }
    }
}