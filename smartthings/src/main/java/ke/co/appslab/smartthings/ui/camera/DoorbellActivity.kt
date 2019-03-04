package ke.co.appslab.smartthings.ui.camera

import android.Manifest
import ke.co.appslab.smartthings.utils.CloudVisionUtils
import com.google.android.gms.tasks.OnSuccessListener
import android.media.ImageReader.OnImageAvailableListener
import ke.co.appslab.smartthings.utils.BoardDefaults
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import android.os.HandlerThread
import android.content.pm.PackageManager
import android.os.Bundle
import android.app.Activity
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import com.google.android.things.contrib.driver.button.Button
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.models.ImagesDoorbell
import kotlinx.android.synthetic.main.activity_doorbell.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class DoorbellActivity : Activity() {

    lateinit var firestore: FirebaseFirestore
    lateinit var firebaseStorage: FirebaseStorage
    private var mCamera: DoorbellCamera? = null
    private var image: String? = null

    /**
     * Driver for the doorbell button;
     */
    private var mButtonInputDriver: ButtonInputDriver? = null

    /**
     * A [Handler] for running Camera tasks in the background.
     */
    private var mCameraHandler: Handler? = null

    /**
     * An additional thread for running Camera tasks that shouldn't block the UI.
     */
    private var mCameraThread: HandlerThread? = null

    /**
     * A [Handler] for running Cloud tasks in the background.
     */
    private var mCloudHandler: Handler? = null

    /**
     * An additional thread for running Cloud tasks that shouldn't block the UI.
     */
    private var mCloudThread: HandlerThread? = null

    /**
     * Listener for new camera images.
     */
    private val mOnImageAvailableListener = OnImageAvailableListener { reader ->
        val image = reader.acquireLatestImage()
        // get image bytes
        val imageBuf = image.getPlanes()[0].getBuffer()
        val imageBytes = ByteArray(imageBuf.remaining())
        imageBuf.get(imageBytes)
        image.close()

        onPictureTaken(imageBytes)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doorbell)
        Log.d(TAG, "Doorbell Activity created.")

        // We need permission to access the camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // A problem occurred auto-granting the permission
            Log.e(TAG, "No permission")
            return
        }

        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        // Creates new handlers and associated threads for camera and networking operations.
        mCameraThread = HandlerThread("CameraBackground")
        mCameraThread!!.start()
        mCameraHandler = Handler(mCameraThread!!.looper)

        mCloudThread = HandlerThread("CloudThread")
        mCloudThread!!.start()
        mCloudHandler = Handler(mCloudThread!!.looper)

        // Initialize the doorbell button driver
        initPIO()

        // Camera code is complicated, so we've shoved it all in this closet class for you.
        mCamera = DoorbellCamera.instance
        mCamera!!.initializeCamera(this, mCameraHandler!!, mOnImageAvailableListener)
    }

    private fun initPIO() {
        try {
            mButtonInputDriver = ButtonInputDriver(
                BoardDefaults.gpioForButton,
                Button.LogicState.PRESSED_WHEN_LOW,
                KeyEvent.KEYCODE_ENTER
            )
            mButtonInputDriver!!.register()
        } catch (e: IOException) {
            mButtonInputDriver = null
            Log.w(TAG, "Could not open GPIO pins", e)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mCamera!!.shutDown()

        mCameraThread!!.quitSafely()
        mCloudThread!!.quitSafely()
        try {
            mButtonInputDriver!!.close()
        } catch (e: IOException) {
            Log.e(TAG, "button driver error", e)
        }

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Doorbell rang!
            Log.d(TAG, "button pressed")
            statusText.text = "Doorbell ringing"
            mCamera!!.takePicture()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    /**
     * Upload image data to Firebase as a doorbell event.
     */
    private fun onPictureTaken(imageBytes: ByteArray?) {
        if (imageBytes != null) {
            val log = firestore.collection("doorbell")
            val imageRef = firebaseStorage.reference.child(log.path)

            // upload image to storage
            val task = imageRef.putBytes(imageBytes)
            task.addOnSuccessListener(OnSuccessListener<Any> { taskSnapshot ->
                // mark image in the database
                imageRef.downloadUrl
                    .addOnSuccessListener {
                        image = it.toString()
                    }
                Log.i(TAG, "Image upload successful")
                // process image annotations
                annotateImage(log, imageBytes)
            }).addOnFailureListener {
                // clean up this entry
                Log.w(TAG, "Unable to upload image to Firebase")
            }
        }
    }

    /**
     * Process image contents with Cloud Vision.
     */
    private fun annotateImage(ref: CollectionReference, imageBytes: ByteArray?) {
        mCloudHandler!!.post(Runnable {
            Log.d(TAG, "sending image to cloud vision")
            // annotate image by uploading to Cloud Vision API
            try {
                val annotations = CloudVisionUtils.annotateImage(imageBytes!!)
                Log.d(TAG, "cloud vision annotations:$annotations")

                val currentTime =LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
                val imagesDoorbell = ImagesDoorbell(
                    timestamp = currentTime.format(formatter),
                    image = image!!,
                    annotations = annotations
                )
                ref.add(imagesDoorbell)
            } catch (e: IOException) {
                Log.e(TAG, "Cloud Vision API error: ", e)
            }
        })
    }

    companion object {
        private val TAG = DoorbellActivity::class.java.simpleName
    }
}