package ke.co.appslab.smartthings.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ke.co.appslab.smartthings.models.InternetMonitorLog
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_LOGS
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_ONLINE

class InternetMonitorRepo {
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference


    fun loadInternetInfo(): LiveData<Boolean> {
        val internetInfoMutableLiveData = MutableLiveData<Boolean>()
        val connectedRef = firebaseDatabase.child(FIREBASE_ONLINE)
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                val isOnline = dataSnapShot.value as Boolean
                internetInfoMutableLiveData.value = isOnline

            }
        })
        return internetInfoMutableLiveData
    }


    fun fetchInternetMonitorLogs(): LiveData<InternetMonitorLog> {
        val internetLogMutableLiveData = MutableLiveData<InternetMonitorLog>()
        val internetyLogsRef = firebaseDatabase.child(FIREBASE_LOGS);
        internetyLogsRef.limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                dataSnapShot.children.forEach {
                    val electricityLog = it.getValue(InternetMonitorLog::class.java)
                    internetLogMutableLiveData.value = electricityLog
                }
            }

        })
        return internetLogMutableLiveData
    }
}
