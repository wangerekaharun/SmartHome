package ke.co.appslab.smarthome

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import ke.co.appslab.smarthome.utils.SessionManager
import ke.co.appslab.smarthome.utils.SharedPref.PREF_NAME
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences by lazy { getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    private val sessionManager: SessionManager by lazy { SessionManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.thingsAuthFragment))
        setupActionBarWithNavController(findNavController(R.id.navHostFragment),appBarConfiguration)
        setupNavigation()

    }

    private fun setupNavigation() {
        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(navigation, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.d("Destinations", destination.label.toString())
            when (destination.label) {
                getString(R.string.authentificationFragment) -> {
                    navigation.visibility = View.GONE
                }
                getString(R.string.homeFragment) -> {
                    navigation.visibility = View.VISIBLE
                }
                getString(R.string.thingsAuthFragment) -> {
                    navigation.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.navHostFragment).navigateUp()


}
