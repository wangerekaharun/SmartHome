package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ke.co.appslab.smarthome.datastates.MotionLogState
import ke.co.appslab.smarthome.models.MotionImageLog
import ke.co.appslab.smarthome.utils.Constants.TIMESTAMP

class MotionLogRepo {

    fun getMotionLogs(): LiveData<MotionLogState> {
        val motionLogMutableStateLiveData = MutableLiveData<MotionLogState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("motions")
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot?.let {
                    val entriesList = it.toObjects(MotionImageLog::class.java)
                    motionLogMutableStateLiveData.value = MotionLogState(null, entriesList)
                }

            }
            .addOnFailureListener {
                motionLogMutableStateLiveData.value = MotionLogState(it.message)
            }

        return motionLogMutableStateLiveData
    }

}