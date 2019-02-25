package ke.co.appslab.smarthome.ui.authentification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import ke.co.appslab.smarthome.datastates.AuthenticateUserState
import ke.co.appslab.smarthome.repositories.AuthenticateUserRepo
import ke.co.appslab.smarthome.utils.NonNullMediatorLiveData

class AuthenticateUserViewModel : ViewModel() {
    private val authenticateUserMediatorLiveData = NonNullMediatorLiveData<AuthenticateUserState>()
    private val authenticateUserRepo = AuthenticateUserRepo()


    fun getAuthenticateUserResponse(): LiveData<AuthenticateUserState> = authenticateUserMediatorLiveData

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val authenticateUserLiveData = authenticateUserRepo.firebaseAuthWithGoogle(account)
        authenticateUserMediatorLiveData.addSource(
            authenticateUserLiveData
        ) { autheniticateUserMediatirLiveData ->
            when {
                this.authenticateUserMediatorLiveData.hasActiveObservers() -> this.authenticateUserMediatorLiveData.removeSource(
                    authenticateUserLiveData
                )
            }
            this.authenticateUserMediatorLiveData.setValue(autheniticateUserMediatirLiveData)
        }
    }
}