package ke.co.appslab.smarthome.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.datastates.WeatherDataState
import ke.co.appslab.smarthome.models.ElectricityLog
import ke.co.appslab.smarthome.utils.getDurationFormatted
import ke.co.appslab.smarthome.utils.nonNull
import ke.co.appslab.smarthome.utils.observe
import kotlinx.android.synthetic.main.fragment_authentification.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by lazy { ViewModelProviders.of(this).get(HomeViewModel::class.java) }
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when (firebaseAuth) {
            null -> {
                findNavController().navigate(R.id.action_homeActivity_to_thingsActivity)
            }
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun setTextViews(it: FirebaseUser) {
        ownerGreetingsText.text = getString(R.string.welcome_greetings, it.displayName)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getWeatherData()
        fetchElectricityLogs()
        observeLiveData()
        firebaseAuth.currentUser?.let { setTextViews(it) }

        clickListeners()
    }

    private fun fetchElectricityLogs() {
        homeViewModel.loadPowerInfo()
        homeViewModel.fetchElectricityMonitorLogs()
    }

    private fun clickListeners() {
        electricityMonitorCardView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_electricityMonitorFragment)
        }
    }

    private fun observeLiveData() {
        homeViewModel.getWeatherDataResponse().nonNull().observe(this) {
            setUpViews(it)
        }
        homeViewModel.getPowerInfoResponse().nonNull().observe(this) {
            handlePowerInfoResponse(it)
        }
        homeViewModel.getElectricityMonitorLogsResponse().nonNull().observe(this) {
            handleElectricityLogsResponse(it)
        }
    }

    private fun handleElectricityLogsResponse(it: ElectricityLog) {
        when (it.timestampOff) {
            null -> powerDurationCountTextView.text = getString(R.string.power_status_desc,
                it.timeStampOn?.let { timeStampOn -> getDurationFormatted(timeStampOn, context!!) })
            else -> {
                powerDurationCountTextView.text = getString(R.string.power_status_desc,
                    it.timestampOff?.let { timeStampOff -> getDurationFormatted(timeStampOff, context!!) })
            }
        }
    }

    private fun handlePowerInfoResponse(it: Boolean) {
        when{
            it -> {
                powerStatusImg.setImageResource(R.drawable.ic_idea)
                powerStatusTextView.text = getString(R.string.power_is_on)
            }
            else -> {
                powerStatusImg.setImageResource(R.drawable.ic_flash_off)
                powerStatusTextView.text = getString(R.string.power_is_off)
            }
        }

    }

    private fun setUpViews(it: WeatherDataState) {
        val weather = it.weather
        weather?.let {
            temperatureText.text = getString(R.string.temperature, it[0].temperature)
            pressureText.text = getString(R.string.pressure, it[0].pressure)
        }
    }

    private fun getWeatherData() {
        homeViewModel.getWeatherData()
    }
}