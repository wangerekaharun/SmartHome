package ke.co.appslab.smartthings.utils

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class SmartThingsApplications : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}