package ke.co.appslab.smarthome.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import ke.co.appslab.smarthome.datastates.AuthenticateUserState

class AuthenticateUserRepo {
    lateinit var auth: FirebaseAuth


    fun firebaseAuthWithGoogle(account: GoogleSignInAccount): LiveData<AuthenticateUserState> {
        val authenticateUserMutableLiveData = MutableLiveData<AuthenticateUserState>()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        authenticateUserMutableLiveData.value = AuthenticateUserState(user, true)
                    }
                    else -> {
                        // If sign in fails, display a message to the user.
                        authenticateUserMutableLiveData.value = AuthenticateUserState(null, false)
                    }
                }

            }

        return authenticateUserMutableLiveData
    }
}