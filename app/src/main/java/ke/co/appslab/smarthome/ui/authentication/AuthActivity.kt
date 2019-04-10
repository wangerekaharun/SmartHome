package ke.co.appslab.smarthome.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import ke.co.appslab.smarthome.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }


    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.authHostFragment).navigateUp()
}
