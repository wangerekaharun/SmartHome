package ke.co.appslab.smarthome.ui.account

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.utils.SharedPref.HOME_NICKNAME
import ke.co.appslab.smarthome.utils.SharedPref.PREF_NAME
import ke.co.appslab.smarthome.utils.SharedPref.USER_DOCUMENT_ID
import ke.co.appslab.smarthome.utils.nonNull
import ke.co.appslab.smarthome.utils.observe
import kotlinx.android.synthetic.main.fragment_account.*
import org.jetbrains.anko.toast

class AccountFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences by lazy {
        activity!!.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
    }
    private val accountViewModel: AccountViewModel by lazy {
        ViewModelProviders.of(this).get(AccountViewModel::class.java)
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
        setupSwitchCheckListeners()
        observerLiveData()
    }

    private fun observerLiveData() {
        accountViewModel.getUpdateSettingsReponse().nonNull().observe(this){
            context?.toast(it.responseString)
        }
    }

    private fun setupSwitchCheckListeners() {
        val documentId = sharedPreferences.getString(USER_DOCUMENT_ID, null)
        armSystemSwitch.setOnCheckedChangeListener { _, isChecked ->
            accountViewModel.updateSettings("armSystem", isChecked, documentId)
        }
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            accountViewModel.updateSettings("allowNotifications", isChecked, documentId)
        }
        accessRemotelySwitch.setOnCheckedChangeListener { _, isChecked ->
            accountViewModel.updateSettings("accessSystemRemotely", isChecked, documentId)
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