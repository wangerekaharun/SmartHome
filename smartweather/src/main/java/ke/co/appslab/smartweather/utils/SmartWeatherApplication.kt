package ke.co.appslab.smartweather.utils

import android.app.Application
import com.google.firebase.FirebaseApp

class SmartWeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
    }
}