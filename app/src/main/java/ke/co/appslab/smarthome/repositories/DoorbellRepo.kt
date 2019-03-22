package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ke.co.appslab.smarthome.datastates.DoorBellState
import ke.co.appslab.smarthome.models.DoorbellEntry
import ke.co.appslab.smarthome.utils.Constants
import ke.co.appslab.smarthome.utils.Constants.TIMESTAMP
import ke.co.appslab.smarthome.utils.Result

class DoorbellRepo {
    private val doorBellEntriesList = ArrayList<DoorbellEntry>()

    fun getDoorBellEntries(): LiveData<DoorBellState> {
        val doorbellMutableStateLiveData = MutableLiveData<DoorBellState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("doorbell")
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val list = ArrayList<DoorbellEntry>()
                querySnapshot.forEach {
                    val doorbellEntry = it.toObject(DoorbellEntry::class.java)
                    doorbellEntry.documentId= it.id
                    list.add(doorbellEntry)
                }
                doorbellMutableStateLiveData.value = DoorBellState(null, list)

            }
            .addOnFailureListener {
                doorbellMutableStateLiveData.value = DoorBellState(it.message)
            }

        return doorbellMutableStateLiveData
    }

}