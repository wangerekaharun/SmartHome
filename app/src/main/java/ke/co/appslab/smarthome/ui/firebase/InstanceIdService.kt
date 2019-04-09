package ke.co.appslab.smarthome.ui.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.models.UserLog
import ke.co.appslab.smarthome.utils.Constants.USER_LOGS
import ke.co.appslab.smarthome.utils.SharedPref.PREF_NAME
import ke.co.appslab.smarthome.utils.SharedPref.USER_DOCUMENT_ID

class InstanceIdService : FirebaseMessagingService() {
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveToken(token)
    }

    private fun saveToken(token: String) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val userLog = UserLog(
            firebaseToken = token
        )
        firebaseFirestore.collection(USER_LOGS).add(userLog).addOnSuccessListener {
            sharedPreferences.edit().putString(USER_DOCUMENT_ID, it.id).apply()
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.data?.isNotEmpty()?.let {
            sendNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)

        }
    }

    private fun sendNotification(title: String?, body: String?) {

        val pendingIndent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.homeFragment)
            .createPendingIntent()

        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_smarthome)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIndent)
            .setAutoCancel(true)
            .setSound(notificationSound)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}