package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smarthome.datastates.AnswerRingState

class AnswerRingRepo {

    fun answerRing(ringId: String, disposition: Boolean, userId: String): LiveData<AnswerRingState> {
        val answerRingMutableLiveData = MutableLiveData<AnswerRingState>()
        val ringReference = FirebaseFirestore.getInstance().collection("doorbell").document(ringId)
        ringReference.update(
            "answer.uid", userId, "answer.disposition", disposition
        ).addOnCompleteListener {
            answerRingMutableLiveData.value = AnswerRingState("Answer written to database")
        }.addOnFailureListener {
            answerRingMutableLiveData.value = AnswerRingState("Answer not written to database")
        }
        return answerRingMutableLiveData

    }
}