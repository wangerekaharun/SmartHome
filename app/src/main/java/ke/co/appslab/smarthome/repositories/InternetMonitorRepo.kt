package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ke.co.appslab.smarthome.models.InternetStatusLog
import ke.co.appslab.smarthome.utils.Constants.FIREBASE_LOGS
import ke.co.appslab.smarthome.utils.Constants.FIREBASE_ONLINE

class InternetMonitorRepo {
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference


    fun loadInternetInfo(): LiveData<Boolean> {
        val internetInfoMutableLiveData = MutableLiveData<Boolean>()
        val onlineRef = firebaseDatabase.child(FIREBASE_ONLINE)
        onlineRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                val isOnline = dataSnapShot.value as Boolean
                internetInfoMutableLiveData.value = isOnline

            }
        })
        return internetInfoMutableLiveData
    }


    fun fetchInternetMonitorLogs(): LiveData<InternetStatusLog> {
        val internetLogMutableLiveData = MutableLiveData<InternetStatusLog>()
        val internetLogsRef = firebaseDatabase.child(FIREBASE_LOGS);
        internetLogsRef.limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                dataSnapShot.children.forEach {
                    val electricityLog = it.getValue(InternetStatusLog::class.java)
                    internetLogMutableLiveData.value = electricityLog
                }
            }

        })
        return internetLogMutableLiveData
    }
}
