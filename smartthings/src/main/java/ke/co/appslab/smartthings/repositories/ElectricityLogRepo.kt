package ke.co.appslab.smartthings.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ke.co.appslab.smartthings.models.ElectricityLog
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_INFO_CONNECTED
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_LOGS
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_ONLINE_ENDPOINT
import ke.co.appslab.smartthings.utils.SharedPref.IS_CONNECTED
import ke.co.appslab.smartthings.utils.SharedPref.PREF_NAME
import ke.co.appslab.smartthings.utils.SmartThingsApplications


class ElectricityLogRepo {
    private val sharedPreferences: SharedPreferences by lazy {
        SmartThingsApplications.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun monitorElectricity() {
        val firebaseDatabase = FirebaseDatabase.getInstance().reference
        val databaseKey = firebaseDatabase.child(FIREBASE_LOGS).push().key

        val onlineDatabaseRef = firebaseDatabase.child(FIREBASE_INFO_CONNECTED)
        val currentUserRef = firebaseDatabase.child(FIREBASE_ONLINE_ENDPOINT)

        onlineDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ElectricityLogRepo", "DatabaseError :$databaseError")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value as Boolean
                when {
                    value -> {
                        val electricityLog = ElectricityLog(
                            timeStampOn = System.currentTimeMillis()
                        )
                        val currentLogDbRef = databaseKey?.let { firebaseDatabase.child(FIREBASE_LOGS).child(it) }
                        currentLogDbRef?.setValue(electricityLog)

                        currentUserRef.setValue(true)
                        currentUserRef.onDisconnect().setValue(false)

                        electricityLog.timestampOff = System.currentTimeMillis()
                        currentLogDbRef?.onDisconnect()?.setValue(electricityLog)
                    }
                }

            }

        })


    }
}