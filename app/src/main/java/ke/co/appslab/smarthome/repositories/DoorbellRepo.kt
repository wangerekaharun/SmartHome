package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smarthome.datastates.DoorBellState
import ke.co.appslab.smarthome.models.DoorbellEntry
import ke.co.appslab.smarthome.utils.Result

class DoorbellRepo {
    private val doorBellEntriesList = ArrayList<DoorbellEntry>()

    fun getDoorBellEntries(): LiveData<DoorBellState> {
        val doorbellMutableStateLiveData = MutableLiveData<DoorBellState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("doorbell")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot?.let { querySnapshot1 ->
                    val entriesList = querySnapshot1.toObjects(DoorbellEntry::class.java)
                    entriesList.forEach { doorBellEntry ->
                        querySnapshot1.forEach {
                            val newEntry = doorBellEntry.copy(documentId = it.id)
                            doorBellEntriesList.add(newEntry)
                            doorbellMutableStateLiveData.value = DoorBellState(null, doorBellEntriesList)
                        }
                    }

                }

            }
            .addOnFailureListener {
                doorbellMutableStateLiveData.value = DoorBellState(it.message)
            }

        return doorbellMutableStateLiveData
    }

}