package ke.co.appslab.smarthome.ui.firebase

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import ke.co.appslab.smarthome.models.UserLog
import ke.co.appslab.smarthome.utils.Constants.USER_LOGS

class InstanceIdService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveToken(token)
    }

    private fun saveToken(token: String) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val userLog = UserLog(
            firebaseToken = token
        )
        firebaseFirestore.collection(USER_LOGS).add(userLog)

    }
}