package ke.co.appslab.smarthome.datastates

import com.google.firebase.auth.FirebaseUser

data class AuthenticateUserState (
    val firebaseUser : FirebaseUser?,
    val authSuccess : Boolean = false
)