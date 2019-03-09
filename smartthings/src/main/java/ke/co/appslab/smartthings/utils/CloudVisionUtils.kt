package ke.co.appslab.smartthings.utils

import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import ke.co.appslab.smartthings.BuildConfig
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


object CloudVisionUtils {
    val TAG = CloudVisionUtils::class.java.simpleName
    private val LABEL_DETECTION = "LABEL_DETECTION"
    private val MAX_LABEL_RESULTS = 10

    /**
     * Construct an annotated image request for the provided image to be executed
     * using the provided API interface.
     *
     * @param imageBytes image bytes in JPEG format.
     * @return collection of annotation descriptions and scores.
     */
    @Throws(IOException::class)
    fun annotateImage(imageBytes: ByteArray, apiKey: String): Map<String, Float> {
        // Construct the Vision API instance
        val httpTransport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val initializer = VisionRequestInitializer(apiKey)
        val vision = Vision.Builder(httpTransport, jsonFactory, null)
            .setVisionRequestInitializer(initializer)
            .build()

        // Create the image request
        val imageRequest = AnnotateImageRequest()
        val img = Image()
        img.encodeContent(imageBytes)
        imageRequest.image = img

        // Add the features we want
        val labelDetection = Feature()
        labelDetection.type = LABEL_DETECTION
        labelDetection.maxResults = MAX_LABEL_RESULTS
        imageRequest.features = Collections.singletonList(labelDetection)

        // Batch and execute the request
        val requestBatch = BatchAnnotateImagesRequest()
        requestBatch.requests = Collections.singletonList(imageRequest)
        val response = vision.images()
            .annotate(requestBatch)
            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            .setDisableGZipContent(true)
            .execute()

        return convertResponseToMap(response)
    }

    /**
     * Process an encoded image and return a collection of vision
     * annotations describing features of the image data.
     *
     * @return collection of annotation descriptions and scores.
     */
    private fun convertResponseToMap(response: BatchAnnotateImagesResponse): Map<String, Float> {
        // Convert response into a readable collection of annotations
        val annotations = HashMap<String, Float>()
        val labels = response.responses.get(0).labelAnnotations
        labels?.let {
            labels.forEach { label ->
                annotations.put(label.description, label.score)
            }
        }

        Log.d(TAG, "Cloud Vision request completed:$annotations")
        return annotations
    }
}