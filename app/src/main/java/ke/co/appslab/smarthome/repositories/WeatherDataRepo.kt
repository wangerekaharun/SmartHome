package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smarthome.datastates.WeatherDataState
import ke.co.appslab.smarthome.models.Weather

class WeatherDataRepo {

    fun getWeatherData(dayNumber: String, sessionId: Int): LiveData<WeatherDataState> {
        val sessionsModelMutableLiveData = MutableLiveData<WeatherDataState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection(dayNumber)
            .whereEqualTo("id", sessionId)
            .get()
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> for (queryDocumentSnapshot in it.result!!) {
                        val weatherModel = queryDocumentSnapshot.toObject(Weather::class.java)
                        sessionsModelMutableLiveData.value = WeatherDataState(weatherModel, null)
                    }
                    else -> sessionsModelMutableLiveData.value = WeatherDataState(null, "Error getting session details")
                }
            }

        return sessionsModelMutableLiveData
    }
}