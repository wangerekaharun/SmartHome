package ke.co.appslab.smarthome.ui.electricitymonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.models.InternetStatusLog
import ke.co.appslab.smarthome.utils.nonNull
import ke.co.appslab.smarthome.utils.observe
import kotlinx.android.synthetic.main.fragment_electricity_monitor.*
import androidx.appcompat.app.AppCompatActivity
import ke.co.appslab.smarthome.utils.getDurationFormatted


class InternetMonitorFragment : Fragment() {
    private var isInternetOn = true
    private val internetMonitorViewModel: InternetMonitorViewModel by lazy {
        ViewModelProviders.of(this).get(InternetMonitorViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_electricity_monitor, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchElectricityLogs()
        observeLiveData()
    }

    private fun observeLiveData() {
        internetMonitorViewModel.getInternetInfoResponse().nonNull().observe(this) {
            handlePowerInfoResponse(it)
        }
        internetMonitorViewModel.getInternetMonitorLogsResponse().nonNull().observe(this) {
            handleElectricityLogsResponse(it)
        }
    }

    private fun handleElectricityLogsResponse(it: InternetStatusLog) {
        hideDialog()
        when (it.timestampOff) {
            null -> timeElapsedTextView.text = getString(R.string.internet_has_been_on_for,
                it.timeStampOn?.let { timeStampOn -> getDurationFormatted(timeStampOn, context!!) })
            else -> {
                timeElapsedTextView.text = getString(R.string.internet_has_been_off_for,
                    it.timestampOff?.let { timeStampOff -> getDurationFormatted(timeStampOff, context!!) })
            }
        }

    }

    private fun handlePowerInfoResponse(it: Boolean) {
        hideDialog()
        when {
            it -> {
                isInternetOn = true
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorLightsOnBackground
                    )
                )
                internetStatusImg.setImageResource(R.drawable.lights_on_house)
                internetConnectionStatusImg.setImageResource(R.drawable.ic_wifi)
                overallStatusText.text = getString(R.string.internet_is_on)
                activity?.window?.statusBarColor =
                    ContextCompat.getColor(activity!!, R.color.colorLightsOnBackgroundDarker)

            }
            else -> {
                isInternetOn = false
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorLightsOffBackground
                    )
                )
                internetStatusImg.setImageResource(R.drawable.lights_off_house)
                internetConnectionStatusImg.setImageResource(R.drawable.ic_no_wifi)
                overallStatusText.text = getString(R.string.internet_is_off)
                activity?.window?.statusBarColor =
                    ContextCompat.getColor(activity!!, R.color.colorLightsOffBackgroundDarker)
            }
        }

    }

    private fun fetchElectricityLogs() {
        showDialog()
        internetMonitorViewModel.loadInternetInfo()
        internetMonitorViewModel.fetchInternetMonitorLogs()
    }


    private fun showDialog() {
        progressBar.visibility = View.VISIBLE
        internetStatusImg.visibility = View.GONE
        overallStatusText.visibility = View.GONE
        timeElapsedTextView.visibility = View.GONE

    }

    private fun hideDialog() {
        progressBar.visibility = View.GONE
        internetStatusImg.visibility = View.VISIBLE
        overallStatusText.visibility = View.VISIBLE
        timeElapsedTextView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        activity?.window?.statusBarColor = ContextCompat.getColor(activity!!, R.color.colorPrimaryDark)
    }
}