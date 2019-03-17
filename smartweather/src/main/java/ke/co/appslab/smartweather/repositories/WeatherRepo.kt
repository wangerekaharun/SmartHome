package ke.co.appslab.smartweather.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smartweather.datastates.FirebaseState
import ke.co.appslab.smartweather.models.Pressure
import ke.co.appslab.smartweather.models.Temperature

class WeatherRepo {

    fun sendTemperatureData(temperature: Temperature): LiveData<FirebaseState> {
        val temperatureMutableLiveData = MutableLiveData<FirebaseState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("temperature")
            .add(temperature)
            .addOnSuccessListener { temperatureMutableLiveData.setValue(FirebaseState("Temperature updated")) }
            .addOnFailureListener { temperatureMutableLiveData.setValue(FirebaseState(it.message.toString())) }

        return temperatureMutableLiveData
    }

    fun sendPressureData(pressure: Pressure): LiveData<FirebaseState> {
        val pressureMutableLiveData = MutableLiveData<FirebaseState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("pressure")
            .add(pressure)
            .addOnSuccessListener { pressureMutableLiveData.setValue(FirebaseState("Pressure updated")) }
            .addOnFailureListener { pressureMutableLiveData.setValue(FirebaseState(it.message.toString())) }

        return pressureMutableLiveData
    }
}