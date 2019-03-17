package ke.co.appslab.smartthings.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import ke.co.appslab.smartthings.R
import ke.co.appslab.smartthings.utils.BoardDefaults
import ke.co.appslab.smartthings.utils.nonNull
import ke.co.appslab.smartthings.utils.observe
import kotlinx.android.synthetic.main.activity_doorbell.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class DoorbellActivity : AppCompatActivity() {
    private var mCamera: DoorbellCamera? = null
    private var mButtonInputDriver: ButtonInputDriver? = null
    private var mCameraHandler: Handler? = null
    private var mCameraThread: HandlerThread? = null
    private var mCloudHandler: Handler? = null
    private var mCloudThread: HandlerThread? = null
    private val doorbellViewModel: DoorbellViewModel by lazy {
        ViewModelProviders.of(this).get(DoorbellViewModel::class.java)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doorbell)

        checkPermissions()
        initializeHandlers()

        //Initialize the doorbell button driver
        initPIO()

        //observe live data emitted by view model
        observerLiveData()


        mCamera = DoorbellCamera.getInstance()
        mCamera?.initializeCamera(this, mCameraHandler!!, imageAvailableListener)
    }

    private fun observerLiveData() {
        doorbellViewModel.getDoorbellLogsResponse().nonNull().observe(this) {
            statusText.visibility = View.VISIBLE
            statusText.text = it.responseString
            viewDoorbellImage.visibility = View.GONE
        }
    }

    private fun initializeHandlers() {
        // Creates new handlers and associated threads for camera and networking operations.
        mCameraThread = HandlerThread("CameraBackground")
        mCameraThread?.start()
        mCameraHandler = Handler(mCameraThread?.looper)

        mCloudThread = HandlerThread("CloudThread")
        mCloudThread?.start()
        mCloudHandler = Handler(mCloudThread?.looper)
    }

    private fun checkPermissions() {
        when {
            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
                Log.e(TAG, "No permission")
                return
            }
        }
    }

    private val imageAvailableListener = object : DoorbellCamera.ImageCapturedListener {
        override fun onImageCaptured(bitmap: Bitmap) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            runOnUiThread {
                statusText.visibility = View.GONE
                viewDoorbellImage.visibility = View.VISIBLE
                viewDoorbellImage.setImageBitmap(bitmap)
            }
            onPictureTaken(bitmap)
        }
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

        mCameraThread?.quitSafely()
        mCloudThread?.quitSafely()
        try {
            mButtonInputDriver?.close()
        } catch (e: IOException) {
            Log.e(TAG, "button driver error", e)
        }

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                // Doorbell rang!
                Log.d(TAG, "button pressed")
                statusText.visibility = View.VISIBLE
                viewDoorbellImage.visibility = View.GONE
                statusText.text = getString(R.string.door_bell_ringing)
                mCamera?.takePicture()
                true
            }
            else -> super.onKeyUp(keyCode, event)
        }
    }

    // Upload image data to Firebase as a doorbell event.

    private fun onPictureTaken(imageBytes: Bitmap) {
        runOnUiThread {
            doorbellViewModel.uploadDoorbellImage(imageBytes, getString(R.string.cloud_vision_key))
        }
    }

    companion object {
        private val TAG = DoorbellActivity::class.java.simpleName
    }
}