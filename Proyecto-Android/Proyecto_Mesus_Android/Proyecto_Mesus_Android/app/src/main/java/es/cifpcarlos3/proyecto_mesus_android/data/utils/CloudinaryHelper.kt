package es.cifpcarlos3.proyecto_mesus_android.data.utils

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

object CloudinaryHelper {
    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            val config = mapOf(
                "cloud_name" to "duqflaaqz",
                "secure" to true
            )
            MediaManager.init(context, config)
            isInitialized = true
        }
    }

    suspend fun uploadImage(file: Any): String? = kotlin.coroutines.suspendCoroutine { continuation ->
        val uploadRequest = when (file) {
            is android.net.Uri -> MediaManager.get().upload(file)
            is String -> MediaManager.get().upload(file)
            else -> MediaManager.get().upload(file.toString())
        }
        
        uploadRequest.unsigned("msq0rqxs")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    continuation.resumeWith(Result.success(url))
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    continuation.resumeWith(Result.success(null))
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            }).dispatch()
    }
}
