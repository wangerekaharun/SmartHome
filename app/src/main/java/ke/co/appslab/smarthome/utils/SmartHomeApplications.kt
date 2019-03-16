package ke.co.appslab.smarthome.utils

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen

class SmartHomeApplications : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this);
        FirebaseApp.initializeApp(this);
    }
}