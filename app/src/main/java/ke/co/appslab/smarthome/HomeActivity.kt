package ke.co.appslab.smarthome

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import ke.co.appslab.smarthome.utils.SessionManager
import ke.co.appslab.smarthome.utils.SharedPref.PREF_NAME
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences by lazy { getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    private val sessionManager: SessionManager by lazy { SessionManager(applicationContext) }
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.thingsAuthFragment))
        setupActionBarWithNavController(findNavController(R.id.navHostFragment), appBarConfiguration)
        setupNavigation()

    }

    private fun setupNavigation() {
        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(navigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.label){
                getString(R.string.title_edit_account) -> navigation.visibility = View.GONE
                getString(R.string.title_electricity_monitor) -> navigation.visibility = View.GONE
                else -> navigation.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.navHostFragment).navigateUp()


}
