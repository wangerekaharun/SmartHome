package ke.co.appslab.smarthome.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.WeatherDataState
import ke.co.appslab.smarthome.repositories.WeatherDataRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class HomeViewModel : ViewModel() {
    private val weatherDataMediatorLiveData = NonNullMediatorLiveData<WeatherDataState>()
    private val weatherDataRepo = WeatherDataRepo()

    fun getWeatherDataResponse(): LiveData<WeatherDataState> = weatherDataMediatorLiveData

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
}