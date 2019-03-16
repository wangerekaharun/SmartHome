package ke.co.appslab.smarthome.ui.electricitymonitor

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ke.co.appslab.smarthome.R
import ke.co.appslab.smarthome.models.ElectricityLog
import ke.co.appslab.smarthome.utils.nonNull
import ke.co.appslab.smarthome.utils.observe
import kotlinx.android.synthetic.main.fragment_electricity_monitor.*
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import androidx.appcompat.app.AppCompatActivity
import ke.co.appslab.smarthome.utils.getDurationFormatted


class ElectricityMonitorFragment : Fragment() {
    private var isPowerOn = true
    private val electricityMonitorViewModel: ElectricityMonitorViewModel by lazy {
        ViewModelProviders.of(this).get(ElectricityMonitorViewModel::class.java)
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
        electricityMonitorViewModel.getPowerInfoResponse().nonNull().observe(this) {
            handlePowerInfoResponse(it)
        }
        electricityMonitorViewModel.getElectricityMonitorLogsResponse().nonNull().observe(this) {
            handleElectricityLogsResponse(it)
        }
    }

    private fun handleElectricityLogsResponse(it: ElectricityLog) {
        hideDialog()
        when (it.timestampOff) {
            null -> timeElapsedTextView.text = getString(R.string.power_has_been_on_for,
                it.timeStampOn?.let { timeStampOn -> getDurationFormatted(timeStampOn, context!!) })
            else -> {
                timeElapsedTextView.text = getString(R.string.power_has_been_off_for,
                    it.timestampOff?.let { timeStampOff -> getDurationFormatted(timeStampOff, context!!) })
            }
        }

    }

    private fun handlePowerInfoResponse(it: Boolean) {
        hideDialog()
        when {
            it -> {
                isPowerOn = true
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorLightsOnBackground
                    )
                )
                electricityStatusImg.setImageResource(R.drawable.lights_on_house)
                overallStatusText.text = getString(R.string.power_is_on)
                activity?.window?.statusBarColor =
                    ContextCompat.getColor(activity!!, R.color.colorLightsOnBackgroundDarker)

            }
            else -> {
                isPowerOn = false
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorLightsOffBackground
                    )
                )
                electricityStatusImg.setImageResource(R.drawable.lights_off_house)
                overallStatusText.text = getString(R.string.power_is_off)
                activity?.window?.statusBarColor =
                    ContextCompat.getColor(activity!!, R.color.colorLightsOffBackgroundDarker)
            }
        }

    }

    private fun fetchElectricityLogs() {
        showDialog()
        electricityMonitorViewModel.loadPowerInfo()
        electricityMonitorViewModel.fetchElectricityMonitorLogs()
    }


    private fun showDialog() {
        progressBar.visibility = View.VISIBLE
        electricityStatusImg.visibility = View.GONE
        overallStatusText.visibility = View.GONE
        timeElapsedTextView.visibility = View.GONE

    }

    private fun hideDialog() {
        progressBar.visibility = View.GONE
        electricityStatusImg.visibility = View.VISIBLE
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