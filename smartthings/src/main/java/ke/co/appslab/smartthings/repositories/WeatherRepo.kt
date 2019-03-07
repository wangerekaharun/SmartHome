package ke.co.appslab.smartthings.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smartthings.datastates.FirebaseState
import ke.co.appslab.smartthings.models.Weather

class WeatherRepo {

    fun sendWeatherData(weather: Weather): LiveData<FirebaseState> {
        val weatherMutableLiveData = MutableLiveData<FirebaseState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("weather")
            .add(weather)
            .addOnSuccessListener { weatherMutableLiveData.setValue(FirebaseState("Weather updated")) }
            .addOnFailureListener { weatherMutableLiveData.setValue(FirebaseState(it.message.toString())) }

        return weatherMutableLiveData
    }
}