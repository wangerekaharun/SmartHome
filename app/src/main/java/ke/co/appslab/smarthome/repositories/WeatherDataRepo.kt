package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ke.co.appslab.smarthome.datastates.PressureState
import ke.co.appslab.smarthome.datastates.TemperatureState
import ke.co.appslab.smarthome.models.PressureLog
import ke.co.appslab.smarthome.models.TemperatureLog
import ke.co.appslab.smarthome.models.Weather

class WeatherDataRepo {
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    fun getTemperatureLogs(): LiveData<TemperatureState> {
        val temperatureLogMutableLiveData = MutableLiveData<TemperatureState>()
        firebaseFirestore.collection("temperature")
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener {
                when {
                    !it.isEmpty -> {
                        val temperatureLogList = it.toObjects(TemperatureLog::class.java)
                        temperatureLogMutableLiveData.value = TemperatureState(temperatureLogList, null)
                    }
                }

            }
            .addOnFailureListener {
                temperatureLogMutableLiveData.value = TemperatureState(null, it.message)
            }

        return temperatureLogMutableLiveData
    }

    fun getPressureLogs(): LiveData<PressureState> {
        val pressureStateMutableLiveData = MutableLiveData<PressureState>()
        firebaseFirestore.collection("pressure")
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener {
                when{
                    !it.isEmpty ->{
                        val pressureLogList = it.toObjects(PressureLog::class.java)
                        pressureStateMutableLiveData.value = PressureState(pressureLogList, null)
                    }
                }
            }
            .addOnFailureListener {
                pressureStateMutableLiveData.value = PressureState(null, it.message)
            }

        return pressureStateMutableLiveData
    }
}