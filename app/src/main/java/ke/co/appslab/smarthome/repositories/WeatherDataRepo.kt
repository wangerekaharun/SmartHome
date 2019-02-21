package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smarthome.datastates.WeatherDataState
import ke.co.appslab.smarthome.models.Weather

class WeatherDataRepo {

    fun getWeatherData(): LiveData<WeatherDataState> {
        val weatherDataMutableLiveData = MutableLiveData<WeatherDataState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("weather")
            .get()
            .addOnSuccessListener {
                when {
                    !it.isEmpty -> {
                        val weatherModel = it.toObjects(Weather::class.java)
                        weatherDataMutableLiveData.value = WeatherDataState(weatherModel, null)
                    }
                }

            }
            .addOnFailureListener {
                weatherDataMutableLiveData.value = WeatherDataState(null,it.message)}

        return weatherDataMutableLiveData
    }
}