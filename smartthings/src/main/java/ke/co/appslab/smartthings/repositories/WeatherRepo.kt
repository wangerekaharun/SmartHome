package ke.co.appslab.smartthings.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smartthings.datastates.WeatherState
import ke.co.appslab.smartthings.models.Weather
import ke.co.appslab.smartthings.utils.Result

class WeatherRepo {

    fun sendWeatherData(weather: Weather): LiveData<WeatherState> {
        val weatherMutableLiveData = MutableLiveData<WeatherState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("weather")
            .add(weather)
            .addOnSuccessListener { weatherMutableLiveData.setValue(WeatherState("Weather updated")) }
            .addOnFailureListener { weatherMutableLiveData.setValue(WeatherState(it.message.toString())) }

        return weatherMutableLiveData
    }
}