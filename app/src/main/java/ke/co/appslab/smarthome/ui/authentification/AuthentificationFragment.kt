package ke.co.appslab.smarthome.ui.authentification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.datastates.AuthenticateUserState
import ke.co.appslab.smarthome.utils.nonNull
import ke.co.appslab.smarthome.utils.observe
import kotlinx.android.synthetic.main.fragment_authentification.view.*
import org.jetbrains.anko.toast

class AuthentificationFragment : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInBtn : SignInButton
    lateinit var progressBar : ProgressBar

    private val authenticateUserViewModel: AuthenticateUserViewModel by lazy {
        ViewModelProviders.of(this).get(AuthenticateUserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_authentification, container, false)
        googleSignInBtn = view.googleSignInBtn
        progressBar = view.progressBar

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context!!, gso)
        //check whether the user is signed in first

        auth = FirebaseAuth.getInstance()
        when {
            auth.currentUser != null -> // already signed in
                navigateThingsAuth()
            else -> // not signed in
                showUI(view.googleSignInBtn)
        }
        //observe livedata emitted by view model
        observeLiveData()

        return view
    }

    private fun observeLiveData() {
        authenticateUserViewModel.getAuthenticateUserResponse().nonNull().observe(this) {
            handleAuthenticateUserResponse(it)
        }
    }

    private fun handleAuthenticateUserResponse(it: AuthenticateUserState) {
        val authSuccess = it.authSuccess
        hideDialog()
        when (authSuccess) {
            true -> {
                navigateThingsAuth()
            }
            false -> {
                context?.toast("Authentification failed.")
            }
        }
    }

    private fun showUI(googleSignInBtn: SignInButton) {
        googleSignInBtn.setOnClickListener {
            signInUser()
            showDialog()
        }
    }

    private fun signInUser() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun navigateThingsAuth() {
        findNavController().navigate(R.id.thingsAuthFragment, null)

    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    account?.let { firebaseAuthWithGoogle(it) }
                } catch (e: ApiException) {
                    // Google Sign In failed
                    context?.toast("Google sign in failed")
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        authenticateUserViewModel.firebaseAuthWithGoogle(account)
    }

    private fun showDialog(){
        progressBar.visibility = View.VISIBLE
        googleSignInBtn.visibility = View.GONE
    }

    private fun hideDialog(){
        progressBar.visibility = View.GONE
        googleSignInBtn.visibility = View.VISIBLE
    }
}