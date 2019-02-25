package ke.co.appslab.smarthome

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupNavigation()

    }

    private fun setupNavigation() {
        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(navigation, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.d("DestinationLabel", destination.label.toString())
            when (destination.label) {
                getString(R.string.authentificationFragment) -> {
                    navigation.visibility = View.GONE
                }
                getString(R.string.homeFragment) -> {
                    navigation.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.navHostFragment).navigateUp()


}
