package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ke.co.appslab.smarthome.datastates.MotionLogState
import ke.co.appslab.smarthome.datastates.OverviewState
import ke.co.appslab.smarthome.models.DoorbellEntry
import ke.co.appslab.smarthome.models.MotionImageLog
import ke.co.appslab.smarthome.utils.Constants

class OverviewRepo {

    fun getTotalVisitorsAllowed(): LiveData<OverviewState> {
        val allowedVisitorsMutableStateLiveData = MutableLiveData<OverviewState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("doorbell")
            .orderBy(Constants.TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val allowedPersonList = it.toObjects(DoorbellEntry::class.java)
                allowedVisitorsMutableStateLiveData.value = OverviewState(allowedPersonList.size)
            }
            .addOnFailureListener {
                allowedVisitorsMutableStateLiveData.value = OverviewState(0, "Could not get total allowed persons")
            }
        return allowedVisitorsMutableStateLiveData

    }

    fun getTotalVisitorsDisallowed(): LiveData<OverviewState> {
        val disAllowedVisitorsMutableStateLiveData = MutableLiveData<OverviewState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("doorbell")
            .whereEqualTo("answer.disposition", false)
            .get()
            .addOnSuccessListener {
                val allowedPersonList = it.toObjects(DoorbellEntry::class.java)
                disAllowedVisitorsMutableStateLiveData.value = OverviewState(allowedPersonList.size)
            }
            .addOnFailureListener {
                disAllowedVisitorsMutableStateLiveData.value = OverviewState(0, "Could not get total allowed persons")
            }
        return disAllowedVisitorsMutableStateLiveData
    }


    fun getTotalMotions(): LiveData<OverviewState> {
        val motionCountMutableStateLiveData = MutableLiveData<OverviewState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("motions")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot?.let {
                    val entriesList = it.toObjects(MotionImageLog::class.java)
                    motionCountMutableStateLiveData.value = OverviewState(entriesList.size)
                }

            }
            .addOnFailureListener {
                motionCountMutableStateLiveData.value = OverviewState(0, "Could not get motions count")
            }

        return motionCountMutableStateLiveData
    }
}