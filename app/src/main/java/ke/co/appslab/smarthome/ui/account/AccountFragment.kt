package ke.co.appslab.smarthome.ui.account

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.utils.SharedPref.HOME_NICKNAME
import ke.co.appslab.smarthome.utils.SharedPref.PREF_NAME
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences by lazy {
        activity!!.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpView(firebaseAuth)
        homeNameLinear.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_editAccountFragment)
        }
    }

    private fun setUpView(firebaseAuth: FirebaseAuth?) {
        firebaseAuth?.currentUser?.let {
            ownerNameText.text = it.displayName
            ownerEmailText.text = it.email
            homeMembersText.text = it.email
            Glide.with(activity!!).load(it.photoUrl).into(userProfileImg)
        }
        val homeName = sharedPreferences.getString(HOME_NICKNAME, null)
        homeName?.let { homeNameText.text = it }
    }
}