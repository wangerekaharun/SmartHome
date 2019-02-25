package ke.co.appslab.smarthome.ui.authentification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.firebase.auth.FirebaseAuth
import ke.co.appslab.smarthome.R
import org.jetbrains.anko.toast

class ThingsAuthFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var connectionsClient: ConnectionsClient
    private var connectedEndpointId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_things_auth, container, false)

        if (firebaseAuth.currentUser == null) {
            throw Exception("You can't do that here")
        }
        connectionsClient = Nearby.getConnectionsClient(activity!!)
        context?.toast("Toast")

        return view
    }

    override fun onStart() {
        super.onStart()
        checkPermissionsForDiscovery()
    }

    override fun onStop() {
        stopDiscovery()
        super.onStop()
    }

    override fun onDestroy() {
        if (connectedEndpointId != null) {
            connectionsClient.disconnectFromEndpoint(connectedEndpointId!!)
            connectedEndpointId = null
        }
        super.onDestroy()
    }

    private fun checkPermissionsForDiscovery() {
        val perm = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (perm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
            )
        } else {
            startDiscovery()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.size == 1) {
            startDiscovery()
        } else {
            Log.e(TAG, "User didn't grant permission")
            activity?.finish()
        }
    }

    private fun startDiscovery() {
        connectionsClient.startDiscovery(
            "ke.co.appslab.smartthings",
            endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        )
            .addOnSuccessListener(activity!!) {
                Log.d(TAG, "startDiscovery onSuccess")
            }
            .addOnFailureListener(activity!!) { e ->
                Log.e(TAG, "startAdvertising onFailure", e)
            }
    }

    private fun stopDiscovery() {
        connectionsClient.stopDiscovery()
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "onEndpointFound $endpointId name: ${info.endpointName} id: ${info.serviceId}")
            connect(endpointId)
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "onEndpointLost $endpointId")
        }
    }

    private fun connect(endpointId: String) {
        connectionsClient.requestConnection(
            "",
            endpointId,
            connectionLifecycleCallback
        )
            .addOnSuccessListener(activity!!) {
                Log.d(TAG, "requestConnection onSuccess")
            }
            .addOnFailureListener(activity!!) { e ->
                Log.e(TAG, "requestConnection onFailure", e)
            }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "onConnectionInitiated $endpointId name: ${info.endpointName}")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
                .addOnSuccessListener(activity!!) {
                    Log.d(TAG, "acceptConnection onSuccess")
                }
                .addOnFailureListener(activity!!) { e ->
                    Log.e(TAG, "acceptConnection onFailure", e)
                }
        }

        override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
            Log.d(TAG, "onConnectionResult $endpointId status: ${resolution.status}")
            connectedEndpointId = endpointId
            val token = GoogleSignIn.getLastSignedInAccount(activity!!)!!.idToken!!
            sendPayload(endpointId, token)
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "onDisconnected $endpointId")
//            activity?.finish()
        }
    }

    private fun sendPayload(endpointId: String, token: String) {
        Log.d(TAG, "Sending id token $token")
        val payload = Payload.fromBytes(token.toByteArray())
        connectionsClient.sendPayload(endpointId, payload)
            .addOnSuccessListener(activity!!) {
                Log.d(TAG, "sendPayload onSuccess")
            }
            .addOnFailureListener(activity!!) { e ->
                Log.e(TAG, "sendPayload onFailure", e)
            }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d(TAG, "onPayloadReceived $endpointId payload: ${payload.id}")
            Log.d(TAG, String(payload.asBytes()!!))
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            Log.d(TAG, "onPayloadTransferUpdate $endpointId ${update.status}")
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                Log.d(TAG, "Disconnecting")
                connectionsClient.disconnectFromEndpoint(endpointId)
//                activity?.finish()
            }
        }
    }


    companion object {
        private const val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0
        private const val TAG = "NearbyConnsActivity"
    }
}
