package ke.co.appslab.smartthings.repositories

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ke.co.appslab.smartthings.datastates.FirebaseState
import ke.co.appslab.smartthings.models.MotionImageLog
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_IMAGE_PREFIX
import ke.co.appslab.smartthings.utils.Constants.FIREBASE_MOTION_REF
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MotionSensorRepo {
    lateinit var downloadUrl : String

    fun uploadMotionImage(imageBytes: Bitmap): LiveData<FirebaseState> {
        val firebaseStateMutableLiveData = MutableLiveData<FirebaseState>()
        val storageRef = FirebaseStorage.getInstance().getReference(FIREBASE_MOTION_REF)
        val imageStorageRef = storageRef.child(FIREBASE_IMAGE_PREFIX + System.currentTimeMillis() + ".jpg")
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
                    val motionCollection = FirebaseFirestore.getInstance().collection(FIREBASE_MOTION_REF)
                    val motionImageLog = MotionImageLog(
                        timestamp = System.currentTimeMillis(),
                        imageRef = downloadUrl,
                        activityLabel = "Motion"
                    )
                    motionCollection.add(motionImageLog).addOnCompleteListener {
                        firebaseStateMutableLiveData.value = FirebaseState("Image uploaded successfully")
                    }
                }
        }

        return firebaseStateMutableLiveData;
    }
}