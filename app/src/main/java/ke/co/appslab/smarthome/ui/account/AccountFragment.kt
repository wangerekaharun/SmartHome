package ke.co.appslab.smarthome.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import ke.co.appslab.smarthome.R
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpView(firebaseAuth)
    }

    private fun setUpView(firebaseAuth: FirebaseAuth?) {
        firebaseAuth?.currentUser?.let {
            ownerNameText.text = it.displayName
            ownerEmailText.text = it.email
            homeMembersText.text = it.email
            Glide.with(activity!!).load(it.photoUrl).into(userProfileImg)
        }
    }
}