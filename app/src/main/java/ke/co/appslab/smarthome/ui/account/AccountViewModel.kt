package ke.co.appslab.smarthome.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ke.co.appslab.smarthome.datastates.SettingsState
import ke.co.appslab.smarthome.repositories.UpdateSettingsRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class AccountViewModel : ViewModel() {
    private val settingsMediatorLiveData = NonNullMediatorLiveData<SettingsState>()
    private val settingsRepo = UpdateSettingsRepo()


    fun getUpdateSettingsReponse(): LiveData<SettingsState> = settingsMediatorLiveData

    fun updateSettings(setting: String, value: Boolean, documentId: String) {
        val settingsLiveData = settingsRepo.updateSettingsRepo(setting, value, documentId)
        settingsMediatorLiveData.addSource(
            settingsLiveData
        ) { settingsMediatorLiveData ->
            when {
                this.settingsMediatorLiveData.hasActiveObservers() -> this.settingsMediatorLiveData.removeSource(
                    settingsLiveData
                )
            }
            this.settingsMediatorLiveData.setValue(settingsMediatorLiveData)
        }
    }
}