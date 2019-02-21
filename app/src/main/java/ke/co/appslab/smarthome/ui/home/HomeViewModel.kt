package ke.co.appslab.smarthome.ui.home

import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.WeatherDataState
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class HomeViewModel : ViewModel(){
    private val weatherDataMediatorLiveData = NonNullMediatorLiveData<WeatherDataState>()
}