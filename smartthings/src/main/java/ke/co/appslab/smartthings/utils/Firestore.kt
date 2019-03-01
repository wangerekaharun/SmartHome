package ke.co.appslab.smartthings.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun awaitData(task: Task<QuerySnapshot>): QuerySnapshot = suspendCancellableCoroutine { continuation ->
    task
        .addOnCanceledListener { continuation.cancel() }
        .addOnSuccessListener { continuation.resume(it) }
        .addOnFailureListener { continuation.resumeWithException(it) }
}

suspend fun Task<QuerySnapshot>.await() = awaitData(this)