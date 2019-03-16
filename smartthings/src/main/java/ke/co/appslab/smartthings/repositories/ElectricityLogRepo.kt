package ke.co.appslab.smartthings.repositories

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.firestore.ServerTimestamp
import ke.co.appslab.smartthings.models.ElectricityLog
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_INFO_CONNECTED
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_LOGS
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_ONLINE_ENDPOINT


class ElectricityLogRepo {

    fun monitorElectricity() {
        val firebaseDatabase = FirebaseDatabase.getInstance().reference
        val databaseKey = firebaseDatabase.child(FIREBASE_LOGS).push().key

        val onlineDatabaseRef = firebaseDatabase.child(FIREBASE_INFO_CONNECTED)
        val currentUserRef = firebaseDatabase.child(FIREBASE_ONLINE_ENDPOINT)

        onlineDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ElectricityLogRepo","DatabaseError :$databaseError")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value as Boolean
                when {
                    value -> {
                        val electricityLog = ElectricityLog()
                        electricityLog.timeStampOn = ServerValue.TIMESTAMP
                        val currentLogDbRef = databaseKey?.let { firebaseDatabase.child(FIREBASE_LOGS).child(it) }
                        currentLogDbRef?.setValue(electricityLog)

                        currentUserRef.setValue(true)
                        currentUserRef.onDisconnect().setValue(false)

                        electricityLog.timestampOff = ServerValue.TIMESTAMP
                        currentLogDbRef?.setValue(electricityLog)
                    }
                }

            }

        })


    }
}