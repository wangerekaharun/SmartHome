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
import ke.co.appslab.smarthome.datastates.PressureState
import ke.co.appslab.smarthome.datastates.TemperatureState
import ke.co.appslab.smarthome.models.InternetStatusLog
import ke.co.appslab.smarthome.utils.getDurationFormatted
import ke.co.appslab.smarthome.utils.nonNull
import ke.co.appslab.smarthome.utils.observe
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by lazy { ViewModelProviders.of(this).get(HomeViewModel::class.java) }
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when (firebaseAuth.currentUser) {
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

        showProgressBar()
        fetchInternetLogsLogs()
        observeLiveData()
        getActivitiesOverviews()
        firebaseAuth.currentUser?.let { setTextViews(it) }

        clickListeners()
    }

    private fun getActivitiesOverviews() {
        homeViewModel.getTotalMotions()
        homeViewModel.getTotalVisitorsAllowed()
        homeViewModel.getTotalVisitorsDisallowed()
    }

    private fun fetchInternetLogsLogs() {
        homeViewModel.loadPowerInfo()
        homeViewModel.fetchElectricityMonitorLogs()
        homeViewModel.getPressureLogs()
        homeViewModel.getTemperatureLogs()
    }

    private fun clickListeners() {
        internetMonitorCardView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_electricityMonitorFragment)
        }
    }

    private fun observeLiveData() {
        homeViewModel.getTemperatureLogResponse().nonNull().observe(this) {
            showTemperatureReadings(it)
        }
        homeViewModel.getPowerInfoResponse().nonNull().observe(this) {
            handleInternetInfoResponse(it)
        }
        homeViewModel.getElectricityMonitorLogsResponse().nonNull().observe(this) {
            handleInternetLogsResponse(it)
        }
        homeViewModel.getPressureLogResponse().nonNull().observe(this) {
            showPressureReadings(it)
        }
        homeViewModel.getAllowedPersonsResponse().nonNull().observe(this) {
            hideProgressBar()
            allowedPersonsCountText.text = it.overviewCount.toString()
        }
        homeViewModel.getDisallowedPersonsResponse().nonNull().observe(this) {
            hideProgressBar()
            disallowedPersonsCountText.text = it.overviewCount.toString()
        }
        homeViewModel.getMotionsCountResponse().nonNull().observe(this) {
            hideProgressBar()
            motionsCountText.text = it.overviewCount.toString()
        }
    }

    private fun showPressureReadings(it: PressureState) {
        hideProgressBar()
        it.pressureLogList?.let {
            pressureText.text = getString(R.string.pressure, it.first().pressure)
        }

    }

    private fun handleInternetLogsResponse(it: InternetStatusLog) {
        hideProgressBar()
        when (it.timestampOff) {
            null -> internetDurationCountTextView.text = getString(R.string.internet_on_status_desc,
                it.timeStampOn?.let { timeStampOn -> getDurationFormatted(timeStampOn, context!!) })
            else -> {
                internetDurationCountTextView.text = getString(R.string.internet_off_status_desc,
                    it.timestampOff?.let { timeStampOff -> getDurationFormatted(timeStampOff, context!!) })
            }
        }
    }

    private fun handleInternetInfoResponse(it: Boolean) {
        hideProgressBar()
        when {
            it -> {
                internetStatusImg.setImageResource(R.drawable.ic_wifi)
                internetStatusTextView.text = getString(R.string.internet_is_on)
            }
            else -> {
                internetStatusImg.setImageResource(R.drawable.ic_no_wifi)
                internetStatusTextView.text = getString(R.string.internet_is_off)
            }
        }

    }

    private fun showTemperatureReadings(it: TemperatureState) {
        hideProgressBar()
        it.temperatureLogList?.let {
            temperatureText.text = getString(R.string.temperature, it.first().temperature)
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        overViewLinear.alpha = 0.6f
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        overViewLinear.alpha = 1f
    }
}