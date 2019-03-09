package ke.co.appslab.smartthings.repositories

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ke.co.appslab.smartthings.datastates.FirebaseState
import ke.co.appslab.smartthings.models.ImagesDoorbell
import ke.co.appslab.smartthings.utils.CloudVisionUtils
import ke.co.appslab.smartthings.utils.Constants
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.io.IOException

class DoorbellLogsRepo {
    private lateinit var downloadUrl: String

    fun uploadDoorbellImage(imageBytes: Bitmap,apiKey: String): LiveData<FirebaseState> {
        val firebaseStateMutableLiveData = MutableLiveData<FirebaseState>()
        val storageRef = FirebaseStorage.getInstance().getReference(Constants.FIREBASE_DOORBELL_REF)
        val imageStorageRef = storageRef.child(Constants.DOORBELL_IMAGE_PREFIX + System.currentTimeMillis() + ".jpg")
        val stream = ByteArrayOutputStream()
        imageBytes.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val uploadTask = imageStorageRef.putBytes(stream.toByteArray())

        uploadTask.addOnFailureListener {
            firebaseStateMutableLiveData.value = FirebaseState("Failed to upload image : ${it.message}")
        }.addOnSuccessListener {
            imageStorageRef.downloadUrl
                .addOnSuccessListener {
                    downloadUrl = it.toString()
                    //upload storage to firebase
                    val doorbellCollection = FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOORBELL_REF)
                    annotateImage(doorbellCollection, stream.toByteArray(),apiKey)

                }
        }

        return firebaseStateMutableLiveData;
    }

    private fun annotateImage(
        ref: CollectionReference,
        imageBytes: ByteArray?,
        apiKey: String
    ) {
        doAsync {
            Log.d(TAG, "sending image to cloud vision")
            // annotate image by uploading to Cloud Vision API
            try {
                val annotations = CloudVisionUtils.annotateImage(imageBytes!!,apiKey)
                Log.d(TAG, "cloud vision annotations:$annotations")

                val imagesDoorbell = ImagesDoorbell(
                    timestamp = System.currentTimeMillis(),
                    image = downloadUrl,
                    annotations = annotations
                )
                ref.add(imagesDoorbell)
            } catch (e: IOException) {
                Log.e(TAG, "Cloud Vision API error: ", e)
            }
        }

    }

    companion object {
        private val TAG = DoorbellLogsRepo::class.java.simpleName
    }
}