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


class ElectricityMonitorFragment : Fragment() {
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
                it.timeStampOn?.let { timeStampOn -> getDurationFormatted(timeStampOn) })
            else -> {
                timeElapsedTextView.text = getString(R.string.power_has_been_off_for,
                    it.timestampOff?.let { timeStampOff -> getDurationFormatted(timeStampOff) })
            }
        }

    }

    private fun handlePowerInfoResponse(it: Boolean) {
        hideDialog()
        when {
            it -> {
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorLightsOnBackground
                    )
                )
                electricityStatusImg.setImageResource(R.drawable.lights_on_house)
                overallStatusText.text = getString(R.string.power_is_on)
            }
            else -> {
                constraintLayoutContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorLightsOffBackground
                    )
                )
                electricityStatusImg.setImageResource(R.drawable.lights_on_house)
                overallStatusText.text = getString(R.string.power_is_off)
            }
        }

    }

    private fun fetchElectricityLogs() {
        showDialog()
        electricityMonitorViewModel.loadPowerInfo()
        electricityMonitorViewModel.fetchElectricityMonitorLogs()
    }

    private fun getDurationFormatted(timestamp: Long): String {
        val duration = getDifferenceBetweenTimeAndNow(timestamp)

        val text: String
        when {
            duration.toMinutes() < 1 -> text = resources.getString(R.string.few_seconds)
            duration.toMinutes() < 60 -> text = resources.getQuantityString(
                R.plurals.mins_formatted, duration.toMinutes() as Int,
                duration.toMinutes() as Int
            )
            duration.toHours() < 24 -> {
                val hoursLong = duration.toHours()
                val minutes = duration.minusHours(hoursLong)
                val minutesElapsed = minutes.toMinutes()
                text = resources
                    .getQuantityString(
                        R.plurals.hours_formatted,
                        hoursLong.toInt(),
                        hoursLong.toInt()
                    ) + ", " + resources
                    .getQuantityString(R.plurals.mins_formatted, minutesElapsed.toInt(), minutesElapsed.toInt())
            }
            else -> {
                val days = duration.toDays()
                val hours = duration.minusDays(days)
                val hoursLong = hours.toHours()
                val minutes = hours.minusHours(hoursLong)
                val minutesElapsed = minutes.toMinutes()
                text =
                    resources.getQuantityString(R.plurals.days_formatted, days.toInt(), days.toInt()) + ", " + resources
                        .getQuantityString(
                            R.plurals.hours_formatted,
                            hoursLong.toInt(),
                            hoursLong.toInt()
                        ) + ", " + resources
                        .getQuantityString(R.plurals.mins_formatted, minutesElapsed.toInt(), minutesElapsed.toInt())

            }
        }

        return text
    }

    private fun getDifferenceBetweenTimeAndNow(timestamp: Long): Duration {
        val today = LocalDateTime.now()
        val otherTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        return Duration.between(otherTime, today)
    }

    private fun showDialog(){
        progressBar.visibility = View.VISIBLE
        electricityStatusImg.visibility = View.GONE
        overallStatusText.visibility= View.GONE
        timeElapsedTextView.visibility = View.GONE

    }

    private fun hideDialog(){
        progressBar.visibility = View.GONE
        electricityStatusImg.visibility = View.VISIBLE
        overallStatusText.visibility= View.VISIBLE
        timeElapsedTextView.visibility = View.VISIBLE
    }
}