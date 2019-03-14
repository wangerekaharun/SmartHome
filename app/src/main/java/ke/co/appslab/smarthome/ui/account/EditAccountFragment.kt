package ke.co.appslab.smarthome.ui.account

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.utils.SharedPref.HOME_NICKNAME
import ke.co.appslab.smarthome.utils.SharedPref.PREF_NAME
import kotlinx.android.synthetic.main.fragment_edit_account.*

class EditAccountFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_edit_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_account, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.accountFragment -> {
                val homeName = homeNameEdit.text.toString()
                saveHomeName(homeName)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveHomeName(homeName: String) {
        when {
            !validateName(homeName) -> return
            else -> {
                sharedPreferences.edit().putString(HOME_NICKNAME, homeName).apply()
                findNavController().navigate(R.id.action_editAccountFragment_to_accountFragment)
            }
        }
    }

    private fun validateName(homeName: String): Boolean {
        var valid = true
        when {
            TextUtils.isEmpty(homeName) -> {
                homeNameLayout.error = getString(R.string.name_empty_error)
                homeNameEdit.requestFocus()

                valid = false
            }
            else -> homeNameLayout.error = null
        }
        return valid
    }


}