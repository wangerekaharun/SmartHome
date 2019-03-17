package ke.co.appslab.smartthings.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ke.co.appslab.smartthings.models.ElectricityMonitorLog
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_LOGS
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_ONLINE

class ElectricityMonitorRepo {
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference


    fun loadPowerInfo(): LiveData<Boolean> {
        val powerInfoMutableLiveData = MutableLiveData<Boolean>()
        val onlineRef = firebaseDatabase.child(FIREBASE_ONLINE)
        onlineRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                val isOnline = dataSnapShot.value as Boolean
                powerInfoMutableLiveData.value = isOnline

            }
        })
        return powerInfoMutableLiveData
    }


    fun fetchElectricityMonitorLogs(): LiveData<ElectricityMonitorLog> {
        val electricityLogMutableLiveData = MutableLiveData<ElectricityMonitorLog>()
        val electricityLogsRef = firebaseDatabase.child(FIREBASE_LOGS);
        electricityLogsRef.limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                dataSnapShot.children.forEach {
                    val electricityLog = it.getValue(ElectricityMonitorLog::class.java)
                    electricityLogMutableLiveData.value = electricityLog
                }
            }

        })
        return electricityLogMutableLiveData
    }
}
