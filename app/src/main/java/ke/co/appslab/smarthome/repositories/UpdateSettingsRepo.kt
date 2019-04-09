package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ke.co.appslab.smarthome.datastates.DoorBellState
import ke.co.appslab.smarthome.datastates.SettingsState
import ke.co.appslab.smarthome.utils.Constants

class UpdateSettingsRepo {

    fun updateSettingsRepo(setting: String, value: Boolean, documentId: String): LiveData<SettingsState> {
        val updateSettingsMutableLiveData = MutableLiveData<SettingsState>()
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection(Constants.USER_LOGS).document(documentId)
            .update(
                setting, value
            )
            .addOnSuccessListener {
                updateSettingsMutableLiveData.value = SettingsState("Settings updated")
            }
            .addOnFailureListener {
                updateSettingsMutableLiveData.value = SettingsState("Error occurred in updating settings")
            }

        return updateSettingsMutableLiveData
    }
}