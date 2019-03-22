package ke.co.appslab.smartthings.repositories

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ke.co.appslab.smartthings.datastates.DoorbellState
import ke.co.appslab.smartthings.datastates.FirebaseState
import ke.co.appslab.smartthings.models.DoorbelLogs
import ke.co.appslab.smartthings.models.ImagesDoorbell
import ke.co.appslab.smartthings.datastates.RingAnswerState
import ke.co.appslab.smartthings.utils.CloudVisionUtils
import ke.co.appslab.smartthings.utils.Constants
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDate

class DoorbellLogsRepo {
    private lateinit var downloadUrl: String
    val doorbellCollection = FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOORBELL_REF)

    fun uploadDoorbellImage(imageBytes: Bitmap, apiKey: String): LiveData<DoorbellState> {
        val firebaseStateMutableLiveData = MutableLiveData<DoorbellState>()
        val storageRef = FirebaseStorage.getInstance().getReference(Constants.FIREBASE_DOORBELL_REF)
        val imageStorageRef = storageRef.child(Constants.DOORBELL_IMAGE_PREFIX + System.currentTimeMillis() + ".jpg")
        val stream = ByteArrayOutputStream()
        imageBytes.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val uploadTask = imageStorageRef.putBytes(stream.toByteArray())

        uploadTask.addOnFailureListener {
            firebaseStateMutableLiveData.value = DoorbellState("Failed to upload image : ${it.message}", false)
        }.addOnSuccessListener {
            imageStorageRef.downloadUrl
                .addOnSuccessListener {
                    downloadUrl = it.toString()
                    //upload storage to firebase

                    val imagesDoorbell = ImagesDoorbell(
                        timestamp = System.currentTimeMillis(),
                        image = downloadUrl
                    )
                    doorbellCollection.add(imagesDoorbell)
                        .addOnSuccessListener { document ->
                            firebaseStateMutableLiveData.value = DoorbellState(document.id, true)
                            Log.d(TAG, document.id)


                        }
                }
        }

        return firebaseStateMutableLiveData;
    }

    private fun annotateImage(
        ref: CollectionReference,
        imageBytes: ByteArray?,
        apiKey: String
    ): String? {
        var responseString: String? = null
        doAsync {
            Log.d(TAG, "sending image to cloud vision")
            // annotate image by uploading to Cloud Vision API
            try {
                val annotations = CloudVisionUtils.annotateImage(imageBytes!!, apiKey)
                Log.d(TAG, "cloud vision annotations:$annotations")

                val imagesDoorbell = ImagesDoorbell(
                    timestamp = System.currentTimeMillis(),
                    image = downloadUrl,
                    annotations = annotations
                )
                ref.add(imagesDoorbell)
                    .addOnSuccessListener { document ->
                        uiThread {
                            responseString = document.id
                            Log.d(TAG, document.id)
                        }

                    }
            } catch (e: IOException) {
                Log.e(TAG, "Cloud Vision API error: ", e)
            }
        }
        return responseString
    }

    fun observeRingAnswerChanges(ringId: String): LiveData<RingAnswerState> {
        val ringAnswerMutableLiveData = MutableLiveData<RingAnswerState>()
        val docRef = doorbellCollection.document(ringId)
        docRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            when {
                snapshot != null && snapshot.exists() -> {
                    Log.d(TAG, "Changes Detected")
                    val doorbelLogs = snapshot.toObject(DoorbelLogs::class.java)
                    ringAnswerMutableLiveData.value = RingAnswerState(doorbelLogs, null)
                }
                else -> {
                    ringAnswerMutableLiveData.value = RingAnswerState(null, "Current data: null")
                    Log.d(TAG, "Current data: null")
                }
            }
        })

        return ringAnswerMutableLiveData
    }

    companion object {
        private val TAG = DoorbellLogsRepo::class.java.simpleName
    }
}