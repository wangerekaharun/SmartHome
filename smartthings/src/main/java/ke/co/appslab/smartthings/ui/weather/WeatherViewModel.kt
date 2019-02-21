package ke.co.appslab.smartthings.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smartthings.datastates.WeatherState
import ke.co.appslab.smartthings.models.Weather
import ke.co.appslab.smartthings.repositories.WeatherRepo
import ke.co.appslab.smartthings.utils.NonNullMediatorLiveData
import ke.co.appslab.smartthings.utils.Result

class WeatherViewModel : ViewModel() {
    private val weatherMediatorLiveData = NonNullMediatorLiveData<WeatherState>()
    private val weatherRepo = WeatherRepo()

    fun getSendWeatherResponse(): LiveData<WeatherState> = weatherMediatorLiveData

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