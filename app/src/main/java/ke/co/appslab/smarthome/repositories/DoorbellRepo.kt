package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smarthome.datastates.DoorBellState
import ke.co.appslab.smarthome.models.DoorbellEntry

class DoorbellRepo {

    fun getDoorBellEntries(): LiveData<DoorBellState> {
        val doorbellMutableStateLiveData = MutableLiveData<DoorBellState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("logs")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot?.let {
                    val entriesList = it.toObjects(DoorbellEntry::class.java)
                    doorbellMutableStateLiveData.value = DoorBellState(null, entriesList)
                }

            }
            .addOnFailureListener {
                doorbellMutableStateLiveData.value = DoorBellState(it.message)
            }

        return doorbellMutableStateLiveData
    }
}