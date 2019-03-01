package ke.co.appslab.smartthings.ui.auth

import android.app.Activity
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import ke.co.appslab.smartthings.R
import kotlinx.android.synthetic.main.activity_things_connection.*

class ThingsConnectionActivity : Activity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var connectionsClient: ConnectionsClient
    private var token: String? = null
    private var deviceName : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_things_connection)
        connectionsClient = Nearby.getConnectionsClient(this)

        connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        FirebaseAuth.getInstance().signOut()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            super.onAvailable(network)
            Log.d(TAG, "onAvailable $network")
            if (token != null) {
                trySignIn()
            }
        }

        override fun onLost(network: Network?) {
            Log.d(TAG, "onLost $network")
        }
    }

    override fun onStart() {
        super.onStart()
        startAdvertising()
    }

    override fun onStop() {
        stopAdvertising()
        super.onStop()
    }

    override fun onDestroy() {
        connectionsClient.stopAllEndpoints()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onDestroy()
    }

    private fun startAdvertising() {
        waitingLinear.visibility = View.VISIBLE
        detailsLinear.visibility = View.GONE
        connectionsClient.startAdvertising(
            "Firebase Doorbell Thing",
            packageName,
            connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        )
            .addOnSuccessListener(this) {
                Log.d(TAG, "startAdvertising onSuccess")
            }
            .addOnFailureListener(this) { e ->
                Log.e(TAG, "startAdvertising onFailure", e)
            }
    }

    private fun stopAdvertising() {
        connectionsClient.stopAdvertising()
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            deviceName = info.endpointName
            Log.d(TAG, "onConnectionInitiated $endpointId name: ${info.endpointName}")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
                .addOnSuccessListener(this@ThingsConnectionActivity) { _ ->
                    Log.d(TAG, "acceptConnection onSuccess")
                }
                .addOnFailureListener(this@ThingsConnectionActivity) { e ->
                    Log.e(TAG, "acceptConnection onFailure", e)
                }
        }

        override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
            Log.d(TAG, "onConnectionResult $endpointId status: ${resolution.status}")
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "onDisconnected $endpointId")
            trySignIn()
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d(TAG, "onPayloadReceived $endpointId payload: ${payload.id}")
            token = String(payload.asBytes()!!)
            Log.d(TAG, token)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            Log.d(TAG, "onPayloadTransferUpdate $endpointId ${update.status}")
        }
    }

    private fun trySignIn() {
        Log.d(TAG, "Signing in with token $token")
        val credential = GoogleAuthProvider.getCredential(token, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener(this) { result ->
                val user = result.user
                Log.d(TAG, "signInWithCredential ${user.displayName} ${user.email}")
                waitingLinear.visibility = View.GONE
                detailsLinear.visibility = View.VISIBLE
                setUpViews(user)
                // finish()
            }
            .addOnFailureListener(this) { e ->
                Log.e(TAG, "signInWithCredential onFailure", e)
            }
    }

    private fun setUpViews(user: FirebaseUser?) {
        user?.let {
            ownerEmailText.text= it.email
            ownerNameText.text = it.displayName
            deviceNameText.text = deviceName
        }

    }


    companion object {
        private const val TAG = "NearbyConnectionsActivity"
    }

}
