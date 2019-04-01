package ke.co.appslab.smartthings.utils

import android.app.Application
import com.droidnet.DroidNet
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.jakewharton.threetenabp.AndroidThreeTen

class SmartThingsApplications : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        AndroidThreeTen.init(this)
        DroidNet.init(this);
    }

    override fun onLowMemory() {
        super.onLowMemory()
        DroidNet.getInstance().removeAllInternetConnectivityChangeListeners();
    }
}